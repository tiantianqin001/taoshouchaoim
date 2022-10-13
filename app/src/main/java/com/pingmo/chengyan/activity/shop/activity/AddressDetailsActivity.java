package com.pingmo.chengyan.activity.shop.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TextView;


import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;
import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.shop.bean.JsonBean;
import com.pingmo.chengyan.activity.shop.common.Constant;
import com.pingmo.chengyan.activity.shop.common.Resource;
import com.pingmo.chengyan.activity.shop.model.entity.AddressEntity;
import com.pingmo.chengyan.activity.shop.utils.GetJsonDataUtil;
import com.pingmo.chengyan.activity.shop.view.ClearWriteEditText;
import com.pingmo.chengyan.activity.shop.viewmodel.WeChatMallViewModel;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;


import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

/**
 * 商城 地址详情 新增地址
 */
public class AddressDetailsActivity extends TitleWhiteBaseActivity implements View.OnClickListener {

    private ClearWriteEditText cet_addressee, cet_phone, cet_address;
    private TextView tv_area;
    private CheckBox cb_default;

    private WeChatMallViewModel mViewModel;
    private AddressEntity mAddressEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_detaills);
        mHandler.sendEmptyMessage(MSG_LOAD_DATA);
    }

    @Override
    protected void initView() {
        setTitle("地址详情");
        cet_addressee = findViewById(R.id.cet_addressee);
        cet_phone = findViewById(R.id.cet_phone);
        tv_area = findViewById(R.id.tv_area);
        cet_address = findViewById(R.id.cet_address);
        cb_default = findViewById(R.id.cb_default);
        findViewById(R.id.ll_addressee).setOnClickListener(this);
        findViewById(R.id.ll_phone).setOnClickListener(this);
        findViewById(R.id.ll_area).setOnClickListener(this);
        findViewById(R.id.ll_address).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);

        mAddressEntity = getIntent().getParcelableExtra(Constant.PUBLIC_PARAMETER);
        if (mAddressEntity != null) {
            cet_addressee.setText(mAddressEntity.addressee);
            cet_phone.setText(mAddressEntity.phone);
            tv_area.setText(mAddressEntity.region);
            cet_address.setText(mAddressEntity.address);
            if (mAddressEntity.getIsDefaultBoolean()) {
                cb_default.setChecked(true);
            } else {
                cb_default.setChecked(false);
            }
        }
    }



    @Override
    protected void initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(WeChatMallViewModel.class);
        mViewModel.getAddAddress().observe(this, new Observer<Resource<Object>>() {
            @Override
            public void onChanged(Resource<Object> objectResource) {
                ToastUtils.show("添加成功");
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_addressee:
                break;
            case R.id.ll_phone:
                break;
            case R.id.ll_area:
                if (isLoaded) {
                    hideKeyboard(v);
                    showPickerView();
                } else {
                    ToastUtils.show("请初始化地区数据");
                }
                break;
            case R.id.ll_address:

                break;
            case R.id.btn_save:
                saveAddRess();
                break;
            default:

        }
    }

    private void saveAddRess() {
        String address = cet_address.getText().toString();
        String addressee = cet_addressee.getText().toString();
        String phone = cet_phone.getText().toString();
        String area = tv_area.getText().toString();
        String userId = SharedPreferenceUtil.getInstance(this).getString("userId");
        int isDefault = 1;
        if (!cb_default.isChecked()) {
            isDefault = 0;
        }
        if (TextUtils.isEmpty(address)) {
            ToastUtils.show("请填写详细地址");
            cet_address.setShakeAnimation();
            return;
        }
        if (TextUtils.isEmpty(addressee)) {
            ToastUtils.show("请填写收件人");
            cet_addressee.setShakeAnimation();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show("请填写手机号");
            cet_phone.setShakeAnimation();
            return;
        }
        if (phone.length() != 11) {
            ToastUtils.show("请填写正确的手机号");
            cet_phone.setShakeAnimation();
            return;
        }
        if (TextUtils.isEmpty(area)) {
            ToastUtils.show("请填选择所在区");
            return;
        }
        if (mAddressEntity != null) {
            mAddressEntity.setIsDefault(String.valueOf(isDefault));
            mAddressEntity.address = address;
            mAddressEntity.addressee = addressee;
            mAddressEntity.phone = phone;
            mAddressEntity.region = area;
            mAddressEntity.userId = userId;
            mViewModel.setUpdateAddress(mAddressEntity);

            mViewModel.getUpdateAddress().observe(this, new Observer<Resource<Object>>() {
                @Override
                public void onChanged(Resource<Object> objectResource) {
                    Log.i("", "onChanged: "+objectResource);
                    finish();
                }
            });
        } else {
            mViewModel.setAddAddress(address, addressee, String.valueOf(isDefault), phone, area,userId);
        }
    }


    private List<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private static boolean isLoaded = false;
    //省
    private String mOpt1tx;
    private int mOpt1Position = 0;
    //市
    private String mOpt2tx;
    //区
    private String mOpt3tx;
    private int mOpt2Position = 0;
    //选中的地区 ，
    private String mTx;

    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> cityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String cityName = jsonBean.get(i).getCityList().get(c).getName();
                cityList.add(cityName);//添加城市
                ArrayList<String> city_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
//                if (jsonBean.get(i).getCityList().get(c).getArea() == null
//                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
//                    city_AreaList.add("");
//                } else {
//                    city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
//                }
                city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                province_AreaList.add(city_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(cityList);

            /**
             * 添加地区数据
             */
            options3Items.add(province_AreaList);
        }

        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

    }

    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {//如果已创建就不再重新创建子线程了

                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 子线程中解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;
                case MSG_LOAD_SUCCESS:
                    isLoaded = true;
                    break;
                case MSG_LOAD_FAILED:
                    break;
            }
        }
    };

    private void showPickerView() {// 弹出选择器

        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                mOpt1tx = options1Items.size() > 0 ?
                        options1Items.get(options1).getPickerViewText() : "";
                mOpt1Position = options1;
                mOpt2tx = options2Items.size() > 0
                        && options2Items.get(options1).size() > 0 ?
                        options2Items.get(options1).get(options2) : "";
                mOpt2Position = options2;
                mOpt3tx = options2Items.size() > 0
                        && options3Items.get(options1).size() > 0
                        && options3Items.get(options1).get(options2).size() > 0 ?
                        options3Items.get(options1).get(options2).get(options3) : "";

                mTx = mOpt1tx + "," + mOpt2tx + "," + mOpt3tx;
                //网络请求成功再改
//                mSivArea.setValue(mTx.replace(",", ""));
//                userInfoViewModel.setRegion(mTx);
                tv_area.setText(mTx);
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器*/
//        pvOptions.setPicker(options1Items, options2Items);//二级选择器
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.setSelectOptions(mOpt1Position, mOpt2Position);
        pvOptions.show();
    }

    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
