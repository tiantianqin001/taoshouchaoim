package com.pingmo.chengyan.activity.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.activity.shop.bean.UploadBean;
import com.pingmo.chengyan.customview.CircleTextImage;
import com.pingmo.chengyan.dialoge.LoadDialog;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.pingmo.chengyan.utils.QZXTools;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class RegistActivity extends BaseActivity implements View.OnClickListener {
    //手机号
    @BindView(R.id.et_login_username)
    EditText et_login_username;

    @BindView(R.id.et_verification_code)
    EditText et_verification_code;

    @BindView(R.id.btn_captcha)
    TextView btn_captcha;

    @BindView(R.id.et_invitation_code)
    EditText et_invitation_code;

    @BindView(R.id.et_invitation_password)
    EditText et_invitation_password;
    @BindView(R.id.ll_login_del)
    RelativeLayout ll_login_del;
    @BindView(R.id.et_invitation_two_password)
    EditText et_invitation_two_password;
    //注册
    @BindView(R.id.rl_login_button)
    RelativeLayout rl_login_button;

    @BindView(R.id.ctv)
    ImageView ctv;
    private String phone;
    //去登录
    @BindView(R.id.tv_go_login)
    TextView tv_go_login;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Common.Interface_err:
                    if (loadDialog!=null){
                        loadDialog.dismiss();
                        loadDialog = null;
                    }
                    String info = (String) msg.obj;
                    ToastUtils.show(info);
                    break;
                case Common.Interface_success:
                    //注册成功进入登录界面 todo 带过去用户名和密码
                    if (loadDialog!=null){
                        loadDialog.dismiss();
                        loadDialog = null;
                    }
                    Intent intent = new Intent(RegistActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();

                    break;
            }

        }
    };
    private CountTimer countTimer;
    private LoadDialog loadDialog;
    private ExecutorService executorService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_layout);
        isShow = true;
        ButterKnife.bind(this);
        executorService = Executors.newSingleThreadExecutor();

        initData();
        initListener();
        addActivity(this);


    }

    private void initListener() {
        //获取验证码
        btn_captcha.setOnClickListener(this);
        rl_login_button.setOnClickListener(this);

        tv_go_login.setOnClickListener(this);
        ll_login_del.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_login_del:
                finish();
                break;
            //去登录
            case R.id.tv_go_login:
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_captcha:
                //获取验证码
                phone = et_login_username.getText().toString().trim();
                if (QZXTools.isEmpty(phone)){
                    ToastUtils.show("请输入手机号");
                    return;
                }
                if (!QZXTools.isMobile(phone)){
                    ToastUtils.show("你输入的手机号不正确");
                    return;
                }

                //获取验证码
                getVitationCode(phone);

                countTimer = new CountTimer(10000*6, 1000);
                countTimer.start();
                break;
            case R.id.rl_login_button:
                //注册
                //获取手机号
                phone = et_login_username.getText().toString().trim();
                if (QZXTools.isEmpty(phone)){
                    ToastUtils.show("请输入手机号");
                    return;
                }
                //验证码
                String verificationCode = et_verification_code.getText().toString().trim();
                if (QZXTools.isEmpty(verificationCode)){
                    ToastUtils.show("请输入验证码");
                    return;
                }
                //昵称
                String invitation_code = et_invitation_code.getText().toString().trim();
                //第一个密码
                String invitation_password = et_invitation_password.getText().toString().trim();
                if (QZXTools.isEmpty(invitation_password)){
                    ToastUtils.show("请输入密码");
                    return;
                }

                if (invitation_password.length() <6){
                    ToastUtils.show("密码太短");
                    return;
                }

                //第二次输入密码
                String invitation_two_password = et_invitation_two_password.getText().toString().trim();
                if (QZXTools.isEmpty(invitation_two_password)){
                    ToastUtils.show("请再次输入密码");
                    return;
                }
                //判断两次密码一样
                if (!invitation_password.equals(invitation_two_password)){
                    ToastUtils.show("两次密码不一样");
                    return;
                }


                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                            regist(phone,verificationCode,invitation_code,invitation_password);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });



               // regist(phone,null,invitation_code,invitation_password);

                break;
        }
    }

    /**
     * 开始注册
     * @param phone
     * @param verificationCode
     * @param invitation_code     //昵称
     * @param invitation_password
     */

    private void regist(String phone, String verificationCode, String invitation_code, String invitation_password) throws ExecutionException, InterruptedException {
        loadDialog = new LoadDialog(this);
        loadDialog.show();
        //先上传头像  头像上传成功后
//        ctv.setText4CircleImage(invitation_code);
//        Bitmap bitmap = Bitmap.createBitmap(ctv.getWidth(), ctv.getHeight(), Bitmap.Config.ARGB_8888);
//        Bitmap bitmap =   Glide.with(this)
//                .asBitmap()
//                .load(R.mipmap.image_index)
//                .submit(300, 300).get();
//
//        Canvas canvas = new Canvas(bitmap);
//        ctv.draw(canvas);
//
//        File file = null;
//        try {
//            file = saveFile(bitmap, System.currentTimeMillis()+".jpg");
//            uploadHeadIconFile(file,verificationCode,invitation_password,phone,invitation_code);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }




        String url = Common.REGIST;
        Map<String, String> mapParams = new LinkedHashMap<>();
//        mapParams.put("code","888888");
        mapParams.put("code",verificationCode);
        mapParams.put("password",invitation_password);
        //  mapParams.put("password",invitation_password);
        // mapParams.put("phone","13137687374");
        mapParams.put("phone",phone);
        mapParams.put("headImg","");
        mapParams.put("nickName",invitation_code);
        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = Message.obtain();
                message.obj ="注册失败";
                message.what = Common.Interface_err;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String content = response.body().string();
                QZXTools.logE("onResponse: " + content, null);

                try {
                    JSONObject jsonObject = new JSONObject(content);
                    int code = jsonObject.optInt("code");
                    String msg = jsonObject.optString("msg");
                    if (code == 9000){
                        Message message = Message.obtain();
                        message.obj =msg;
                        message.what = Common.Interface_err;
                        handler.sendMessage(message);
                    } else if (code == 200){
                        Message message = Message.obtain();
                        message.obj =msg;
                        message.what = Common.Interface_success;
                        handler.sendMessage(message);
                    }else {
                        Message message = Message.obtain();
                        message.obj =msg;
                        message.what = Common.Interface_err;
                        handler.sendMessage(message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void getVitationCode(String phone) {
        String url = Common.SENDCODE;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("phone",phone);
        mapParams.put("type","0");

        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                QZXTools.logE("onResponse: " + e.getMessage(), null);
                if (!isShow){
                    return;
                }

                Message message = Message.obtain();
                message.what = Common.Interface_net_err;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String content = response.body().string();
                QZXTools.logE("onResponse: " + content, null);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(content);
                    String code = jsonObject.optString("code");
                    String msg = jsonObject.optString("msg");
                    if (code.equals("200")) {
//                        MainEnterpriseListBean mainEnterpriseListBean = JSON.parseObject(content,
//                                MainEnterpriseListBean.class);

                        if (!isShow){
                            return;
                        }
//                        Message message = Message.obtain();
//                        message.what = Common.Interface_success;
//                        message.obj = mainEnterpriseListBean;
//                        handler.sendMessage(message);
                    }else {
                        if (!isShow){
                            return;
                        }
                        Message message = Message.obtain();
                        message.what = Common.Interface_err;
                        message.obj = msg;
                        handler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isShow = false;
        if (countTimer!=null){
            countTimer.cancel();
            countTimer = null;
        }

        if (executorService!=null){
            executorService.shutdown();
            executorService=null;
        }

        removeActivity(this);
    }


     class CountTimer extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.e("Tag", "倒计时=" + (millisUntilFinished/1000));
            btn_captcha.setText(millisUntilFinished / 1000 + "s后重新发送");
            //设置倒计时中的按钮外观
            btn_captcha.setClickable(false);//倒计时过程中将按钮设置为不可点击
        }
        @Override
        public void onFinish() {
            btn_captcha.setText("重新发送");
            btn_captcha.setClickable(true);
        }
    }

    public File saveFile(Bitmap bm, String fileName) throws IOException {//将Bitmap类型的图片转化成file类型，便于上传到服务器
        String path = Environment.getExternalStorageDirectory() + "/Ask";
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(path +"/"+ fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return myCaptureFile;

    }

    private void uploadHeadIconFile(File file,String verificationCode,String invitation_password,String phone, String invitation_code) {

       // showWaitingDlg();
        String url = Common.UPLOAD;
        Map<String, String> mapParams = new LinkedHashMap<>();
        OkHttp3_0Utils.getInstance().asyncPostSingleOkHttp(url, "file", mapParams, file, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = Message.obtain();
                message.what = Common.Interface_err;
                message.obj = "头像上传失败，请稍后重试";
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String content = response.body().string();
                QZXTools.logE("onResponse: " + content, null);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(content);
                    int code = jsonObject.optInt("code");
                    String msg = jsonObject.optString("msg");
                    if (code == 200) {
                        JSONObject jsonObject1 = jsonObject.optJSONObject("data");
                        if (jsonObject1==null)return;
                        //头像的地址
                        String headPhoto = jsonObject1.optString("url");



                    } else {
                        Message message = Message.obtain();
                        message.what = Common.Interface_err;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what = Common.Interface_err;
                    message.obj = "头像上传失败，请稍后重试";
                    mHandler.sendMessage(message);
                }
            }
        });
    }


}
