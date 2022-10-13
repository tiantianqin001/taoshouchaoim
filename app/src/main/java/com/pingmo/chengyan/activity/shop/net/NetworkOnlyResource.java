package com.pingmo.chengyan.activity.shop.net;



import static com.pingmo.chengyan.activity.shop.common.SealTalkUrlCode.WRONG_PASSWORD_MAX;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;


import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.activity.shop.bean.UploadBean;
import com.pingmo.chengyan.activity.shop.common.ErrorCode;
import com.pingmo.chengyan.activity.shop.common.LogTag;
import com.pingmo.chengyan.activity.shop.common.NetConstant;
import com.pingmo.chengyan.activity.shop.common.Resource;
import com.pingmo.chengyan.activity.shop.common.ThreadManager;
import com.pingmo.chengyan.activity.shop.utils.KLog;
import com.pingmo.chengyan.activity.shop.viewmodel.Result;


public abstract class NetworkOnlyResource<ResultType, RequestType> {
    private final ThreadManager threadManager;

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    public NetworkOnlyResource() {
        this.threadManager = ThreadManager.getInstance();
        if (threadManager.isInMainThread()) {
            init();
        } else {
            threadManager.runOnUIThread(() -> init());
        }

    }

    private void init() {
        result.setValue(Resource.loading(null));
        fetchFromNetwork();
    }

    private void fetchFromNetwork() {
        LiveData<RequestType> apiResponse = createCall();
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            if (response != null) {
                if (response instanceof Result) {
                    int code = ((Result) response).code;
                    if (code != NetConstant.REQUEST_SUCCESS_CODE) {
                        String msg = ((Result) response).msg;
                        result.setValue(Resource.error(code, null, TextUtils.isEmpty(msg) ? "网络异常,请稍后重试" : msg));
                        if (!TextUtils.isEmpty(msg)) {
                            if ("无权限操作".equals(msg)) {
                              //  ((MutableLiveData<Boolean>) IMManager.getInstance().kickedOffline2).postValue(true);
                                return;
                            }
                            ToastUtils.show(msg);
                        }
                        if (NetConstant.REQUEST_TOKEN_FAIL_CODE == code) {
                          //  ((MutableLiveData<Boolean>) IMManager.getInstance().kickedOffline2).postValue(true);
                            return;
                        } else {
                            KLog.e("未知接口异常");
                        }
                        return;
                    } else {
                        // do nothing
                        if (WRONG_PASSWORD_MAX == code) {
                            //支付密码连错五次，提示修改支付密码
                            KLog.d("支付密码连错五次，提示修改支付密码");
                        }
                    }
                }
                threadManager.runOnWorkThread(() -> {
                    ResultType resultType = NetworkOnlyResource.this.transformRequestType(response); //自定义的
                    if (resultType == null) {
                        resultType = NetworkOnlyResource.this.transformDefault(response); //默认
                    }
                    try {
                        NetworkOnlyResource.this.saveCallResult(resultType);
                    } catch (Exception e) {
                       Log.e(LogTag.DB, "saveCallResult failed:" + e.toString());
                    }
                    result.postValue(Resource.success(resultType));
                });
            } else {
                result.setValue(Resource.error(ErrorCode.API_ERR_OTHER.getCode(), null));
            }
        });
    }

    /**
     * 重写此方法完成请求类型和响应类型转换
     * 如果是请求结果是 Result<ResultType>  类型则不用重写
     *
     * @param response
     * @return
     */
    @WorkerThread
    protected ResultType transformRequestType(RequestType response) {
        return null;
    }

    @WorkerThread
    private ResultType transformDefault(RequestType response) {
        if (response instanceof Result) {
            Object result = ((Result) response).getResult();
            if (result != null) {
                try {
                    return (ResultType) result;
                } catch (Exception e) {
                    return null;
                }
            } else {
                return null;
            }
        } else if (response instanceof UploadBean) {
            Object result = ((UploadBean) response);
            if (result != null) {
                try {
                    return (ResultType) result;
                } catch (Exception e) {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    @WorkerThread
    protected void saveCallResult(@NonNull ResultType item) {
    }

    @NonNull
    @MainThread
    protected abstract LiveData<RequestType> createCall();
}