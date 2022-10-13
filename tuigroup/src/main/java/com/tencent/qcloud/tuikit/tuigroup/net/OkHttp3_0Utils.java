package com.tencent.qcloud.tuikit.tuigroup.net;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttp3_0Utils {
    private static volatile OkHttp3_0Utils okHttp3_0Utils;

    private OkHttpClient okHttpClient;

    /**
     * 默认不适用缓存的，需改代码转换
     */
    private static final boolean OPEN_CACHE = false;

    private static final int CONNECT_TIMEOUT = 10;
    private static final int READ_TIMEOUT =10;
    private static final int WRITE_TIMEOUT = 10;


    private OkHttp3_0Utils() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.retryOnConnectionFailure(true);
        //默认超时都是10秒
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);//连接超时
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);//读取超时
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS); //写超时
        //设置4次重连
        builder.addInterceptor(new RetryIntercepter(3));//重试
        //支持https
     //   builder.sslSocketFactory(createSSLSocketFactory());

        //自定义连接池最大空闲连接数和等待时间大小，否则默认最大5个空闲连接
        builder.connectionPool(new ConnectionPool(32,5, TimeUnit.MINUTES));



    /*    if (OPEN_CACHE) {
            int cacheSize = 10 * 1024 * 1024;//10M
            //内部的缓存文件区域：/data/data/包名/cache
            Cache cache = new Cache(new File(QZXTools.getInternalStorageForCache
                    (MyApplication.getInstance().getApplicationContext())), cacheSize);
            //缓存
            builder.cache(cache);
        }*/

        //拦截器可以重写Reqest以及Response，对于重写Response需要注意这个危险性

        //应用拦截器
        builder.addInterceptor(new LoggingInterceptor());
        //网络拦截器
        builder.addNetworkInterceptor(new CacheControlInterceptor());
        //请求头拦截器
       // builder.addInterceptor(new RequestHeadceptor());
        //添加token 过去的拦截
       // builder.addInterceptor(new TokenInterceptor());
        //容许 https  请求
        builder.hostnameVerifier(new AllowAllHostnameVerifier());
        okHttpClient = builder.build();
    }

    public static OkHttp3_0Utils getInstance() {
        if (okHttp3_0Utils == null) {
            synchronized (OkHttp3_0Utils.class) {
                if (okHttp3_0Utils == null) {
                    okHttp3_0Utils = new OkHttp3_0Utils();
                }
            }
        }
        return okHttp3_0Utils;
    }

    /**
     * 取消指定tag的请求
     */
    public void cancelTagRequest(Object tag) {
        if (okHttpClient != null && tag != null) {
            for (Call call : okHttpClient.dispatcher().queuedCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
            for (Call call : okHttpClient.dispatcher().runningCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
        }
    }

    /**
     * 取消所有网络请求
     */
    public void cancelAllRequest() {
        if (okHttpClient != null) {
            for (Call call : okHttpClient.dispatcher().queuedCalls()) {
                call.cancel();
            }

            for (Call call : okHttpClient.dispatcher().runningCalls()) {
                call.cancel();
            }
        }
    }

    /**
     * 封装异步GET模式---普通模式
     * <p>
     * 一、header会覆盖同Key的值、addHeader允许存在同Key多值
     * 二、使用HttpUrl来类似于Post请求增加Query参数
     *
     * @param url              请求的Url地址
     * @param responseCallback 请求的异步回调，注意：这是在子线程中
     */

    public void asyncGetOkHttp(String url, Callback responseCallback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 封装异步GET模式---询问参数模式
     * <p>
     * 使用HttpUrl来类似于Post请求增加Query参数
     *
     * @param url              请求的Url地址
     * @param queryParams      拼接的Get询问参数
     * @param responseCallback 请求的异步回调，注意：这是在子线程中
     */
    public void asyncGetOkHttp(String url, Map<String, String> queryParams, Callback responseCallback) {
        //借助于HttpUrl的Builder添加Get模式的查询参数
        Iterator<Map.Entry<String, String>> iterator = queryParams.entrySet().iterator();
        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            builder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        HttpUrl httpUrl = builder.build();
        //把HttpUrl转化成Url
        Request request = new Request.Builder()
                .url(httpUrl.toString())
                .build();

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseCallback.onFailure(call,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseCallback.onResponse(call,response);
            }
        };

        okHttpClient.newCall(request).enqueue(callback);
    }


    /**
     * 封装异步GET模式---询问参数模式
     * <p>
     * 使用HttpUrl来类似于Post请求增加Query参数
     *
     * @param url              请求的Url地址
     * @param queryParams      拼接的Get询问参数
     * @param responseCallback 请求的异步回调，注意：这是在子线程中
     */
    public void asyncGetHeaderOkHttp(String url, Map<String, String> queryParams,Map<String,String> headerParams, Callback responseCallback) {
        //借助于HttpUrl的Builder添加Get模式的查询参数
        Iterator<Map.Entry<String, String>> iterator = queryParams.entrySet().iterator();
        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            builder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        HttpUrl httpUrl = builder.build();
        //把HttpUrl转化成Url
        Request request = new Request.Builder()
                .headers(Headers.of(headerParams))
                .url(httpUrl.toString())
                .build();

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseCallback.onFailure(call,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseCallback.onResponse(call,response);
            }
        };

        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 封装异步GET模式---添加Header模式
     * <p>
     * 一、header会覆盖同Key的值、addHeader允许存在同Key多值
     *
     * @param url              请求的Url地址
     * @param headerParams     需要添加的请求头参数
     * @param responseCallback 请求的异步回调，注意：这是在子线程中
     */
    public void asyncGetOkHttpHadHeader(String url, Map<String, String> headerParams, Callback responseCallback) {
        Request request = new Request.Builder()
                .headers(Headers.of(headerParams))
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 封装异步POST模式---普通的表单模式
     * 一、必传请求体参数
     *
     * @param url              请求的Url地址
     * @param responseCallback 请求的异步回调，注意：这是在子线程中
     */
    public void  asyncBodyOkHttp(String url, JSONObject jsonObject, Map<String, String> headerParams,
                                Callback responseCallback) {
        try {

            //MediaType  设置Content-Type 标头中包含的媒体类型值
            RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                    , jsonObject.toString());
            Request request = new Request.Builder()
                    .headers(Headers.of(headerParams))
                    .url(url)
                    .post(requestBody)
                    .build();

            Callback callback = new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    responseCallback.onFailure(call,e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    responseCallback.onResponse(call,response);
                }
            };


            okHttpClient.newCall(request).enqueue(callback);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     * 封装异步POST模式---普通的表单模式
     * 一、必传请求体参数
     *
     * @param url              请求的Url地址

     * @param responseCallback 请求的异步回调，注意：这是在子线程中
     */
    public void asyncBodyOkHttp(String url,  RequestBody requestBody, Map<String, String> headerParams,
                                Callback responseCallback) {
        try {

            Request request = new Request.Builder()
                    .headers(Headers.of(headerParams))
                    .url(url)
                    .post(requestBody)
                    .build();

            Callback callback = new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    responseCallback.onFailure(call,e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    responseCallback.onResponse(call,response);
                }
            };


            okHttpClient.newCall(request).enqueue(callback);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     * 封装异步POST模式---普通的表单模式
     * 一、必传请求体参数
     *
     * @param url              请求的Url地址
     * @param params           POST请求所需要的请求体参数
     * @param responseCallback 请求的异步回调，注意：这是在子线程中
     */
    public void asyncBodyOkHttp(String url, Map<String, String> params,
                                Callback responseCallback) {
        try {
            if (params==null || params.size()==0)return;
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (TextUtils.isEmpty(entry.getValue())){
                    continue;
                }
                jsonObject.put(entry.getKey(),entry.getValue());
            }
            //MediaType  设置Content-Type 标头中包含的媒体类型值
            RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                    , jsonObject.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            Callback callback = new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    responseCallback.onFailure(call,e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    responseCallback.onResponse(call,response);
                }
            };


            okHttpClient.newCall(request).enqueue(callback);
        }catch (Exception e){
            e.printStackTrace();
        }
    }






    /**
     * 封装异步POST模式---普通的表单模式
     * 一、必传请求体参数
     *
     * @param url              请求的Url地址
     * @param requestBody           POST请求所需要的请求体参数
     * @param responseCallback 请求的异步回调，注意：这是在子线程中
     */
    public void asyncPostOkHttp(String url, RequestBody requestBody, Callback responseCallback) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            okHttpClient.newCall(request).enqueue(responseCallback);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 封装异步POST模式---普通的表单模式
     * 一、必传请求体参数
     *
     * @param url              请求的Url地址
     * @param params           POST请求所需要的请求体参数
     * @param responseCallback 请求的异步回调，注意：这是在子线程中
     */
    public void asyncPostOkHttp(String url, Map<String, String> params, Callback responseCallback) {
        if (params==null || params.size()==0)return;
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (TextUtils.isEmpty(entry.getValue())){
                continue;
            }
            builder.add(entry.getKey(), entry.getValue());
        }
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 封装异步POST模式---普通的表单模式+Header参数模式
     * 一、必传请求体参数
     *
     * @param url              请求的Url地址
     * @param params           POST请求所需要的请求体参数
     * @param headerParams     需要添加的请求头参数
     * @param responseCallback 请求的异步回调，注意：这是在子线程中
     */
    public void asyncPostOkHttp(String url, Map<String, String> params,
                                Map<String, String> headerParams, Callback responseCallback) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .headers(Headers.of(headerParams))
                .build();
        okHttpClient.newCall(request).enqueue(responseCallback);
    }

    //以二进制的形式上传文件
    public static final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");

    /**
     * 根据文件名获取MIME类型
     */
    private MediaType guessMimeType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        fileName = fileName.replace("#", "");   //解决文件名中含有#号异常的问题
        String contentType = fileNameMap.getContentTypeFor(fileName);
        if (contentType == null) {
            return MEDIA_TYPE_STREAM;
        }
        return MediaType.parse(contentType);
    }

    /**
     * 封装异步POST模式---多种类型的请求模式
     * <p>
     * 一、常用于文件上传
     * 说明：Multipart/form-data是上传文件的一种方式，是浏览器用表单上传文件的方式
     * 二、如果没有要上传的问文件就不要用此方法
     * <p>
     * <p>
     * 添加要上传的参数
     * 方式一：
     * addPart(Headers.of("Content-Disposition","form-data; name=\"file\";filename=\"file.png\"),
     * RequestBody.create(MEDIA_TYPE_PNG, files.get(j))
     * 方式二：
     * //内部做了字符串的拼接处理了
     * addFormDataPart(String name, @Nullable String filename, RequestBody body)
     *
     * @param url              请求的Url地址
     * @param fileKeyName      上传文件时和服务端匹配的key
     * @param fileMap          要上传的带MIME类型的文件,第一个参数是文件名：file.getName()
     *                         通过guessMimeType解析得到MIME类型
     *                         例如PNG图片文件：
     *                         MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
     *                         例如二进制流文件：
     *                         MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");
     * @param responseCallback 请求的异步回调，注意：这是在子线程中
     */
    public void asyncPostMultiOkHttp(String url, Map<String, String> headerParams, String fileKeyName,
                                     Map<String, File> fileMap, Callback responseCallback) {
        //multipart/form-data 必须这种类型
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        //添加文件参数
        for (Map.Entry<String, File> file : fileMap.entrySet()) {
            /*
             * 例如PNG图片文件：
             * MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
             * 例如二进制流文件：
             * MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");
             * */

            //RequestBody requestBody = RequestBody.create(guessMimeType(file.getKey()), file.getValue());
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_STREAM, file.getValue());
            //注意name:"file" 对应服务端要求的文件参数
            builder.addFormDataPart(fileKeyName, file.getValue().getName(), requestBody);
        }
        MultipartBody multipartBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .headers(Headers.of(headerParams))
                .post(multipartBody)
                .build();
        okHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 封装异步POST模式---多种类型的请求模式,单个文件模式
     */
    public void asyncPostSingleOkHttp(String url, String fileKeyName, Map<String, String> params,
                                      File file, Callback responseCallback) {
        //multipart/form-data 必须这种类型
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (params != null) {
            //添加一般参数
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }

        if (file != null && fileKeyName != null) {
            RequestBody requestBody = RequestBody.create(guessMimeType(file.getName()), file);
            //注意name:"file" 对应服务端要求的文件参数
            builder.addFormDataPart(fileKeyName, file.getName(), requestBody);
        }

        MultipartBody multipartBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();
        okHttpClient.newCall(request).enqueue(responseCallback);
    }




    private int threadCount = 4;

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }



    private static final int Fail = 17;
    private static final int Process = 18;
    private static final int Complete = 19;
    private static final int MsgProcess = 20;
    private static final int CommonComplete = 21;
    private int count = 0;
    private int[] progressArray;
    private DownloadCallback handlerCallback;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (handlerCallback == null) {
                return;
            }
            switch (msg.what) {
                case Fail:
                    handlerCallback.downloadFailure();
                    break;
                case Process:
                    int value = 0;
                    for (int i = 0; i < progressArray.length; i++) {
                        value += progressArray[i];
                    }
                    handlerCallback.downloadProcess(value);
                    break;
                case Complete:
                    count++;
                    if (count == threadCount) {
                        handlerCallback.downloadComplete(null);
                    }
                    break;
                case MsgProcess:
                    handlerCallback.downloadProcess(msg.arg1);
                    break;
                case CommonComplete:
                    if (call != null) {
                        call = null;
                    }

                    if (msg.obj != null) {
                        String filePath = (String) msg.obj;
                        handlerCallback.downloadComplete(filePath);
                    } else {
                        handlerCallback.downloadComplete(null);
                    }
                    break;
            }
        }
    };

    /**
     * 如果使用new Handler的匿名内部类就要在Activity中的onDestroy方法中调用该方法
     */
    public void cleanHandler() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            handlerCallback = null;
        }
    }



    //---------------------------------多线程下载单个大文件----------------------------------------

    //------------------------------------断点下载单个文件-----------------------------------------
    private Call call;



    /**
     * 取消断点下载请求
     */
    public void cancleDownloadMulti() {
        if (call != null) {
            call.cancel();
            call = null;
        }
    }
    //------------------------------------断点下载单个文件-----------------------------------------

    /**
     * 文件下载回调接口
     */
    public interface DownloadCallback {

        /**
         * 下载的进度
         */
        void downloadProcess(int value);

        /**
         * 下载完成
         */
        void downloadComplete(String filePath);

        /**
         * 下载失败
         */
        void downloadFailure();

    }


    /**
     * 重试拦截器
     */
    public class RetryIntercepter implements Interceptor {

        public int maxRetry;//最大重试次数
        private int retryNum = 0;//假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

        public RetryIntercepter(int maxRetry) {
            this.maxRetry = maxRetry;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
           // request.newBuilder().addHeader("Connection", "close").build();
            System.out.println("retryNum=" + retryNum);
            Response response = chain.proceed(request);
            while (!response.isSuccessful() && retryNum < maxRetry) {
                retryNum++;
                System.out.println("retryNum=" + retryNum);
                response.close(); // 很简单，加上这一句
                response = chain.proceed(request);
            }
            return response;
        }

    }
    /**
     * 缓存的网络拦截器
     */
    class CacheControlInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request().newBuilder().addHeader("Connection","close").build();
//            if (QZXTools.isNetworkAvailable()) {
//                QZXTools.logE("有网络", null);
//            } else {
//                QZXTools.logE("无网络", null);
//                //QZXTools.popToast(MyApplication.getInstance().getApplicationContext(), "请打开网络！", false);
//            }
            Response response = chain.proceed(request);
            return response;
        }
    }

    /**
     * 日志拦截器
     */
    class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();


            Response response = chain.proceed(request);

            if (!response.isSuccessful()){
                response.close(); // 很简单，加上这一句
                response = chain.proceed(request);
            }



            long t2 = System.nanoTime();


            return response;
        }
    }







    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null,  new TrustManager[] { new TrustAllCerts() }, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }


    public static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
    }





}
