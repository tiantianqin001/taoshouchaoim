package com.tencent.qcloud.tuikit.tuichat.ui.page;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.Projection;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuikit.tuichat.R;

import com.tencent.qcloud.tuikit.tuichat.util.FileUtil;
import com.tencent.qcloud.tuikit.tuichat.util.PermissionHelper;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static androidx.constraintlayout.widget.StateSet.TAG;

public class GeographicMapActivity extends Activity implements View.OnClickListener, AMapLocationListener,
        AMap.OnMapClickListener, AMap.OnMapTouchListener, GeocodeSearch.OnGeocodeSearchListener,
        AMap.OnCameraChangeListener, AMap.OnMapScreenShotListener {

    private TextView tv_base_title;
    private LinearLayout ll_indicator_back;

    public static IUIKitCallback mCallBack;
    private MapView mMapView;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //初始化AMapLocationClientOption对象
    AMapLocationClientOption mLocationOption = null;

    private GeocodeSearch geocodeSearch;
    private TextView tv_locatoin;
    private String simpleAddress;

    private boolean isFist = true;
    private double latitude;
    private double longitude;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置沉静状态栏
        ImmersionBar.with(this)
                .transparentBar()
                .init();
        //获取权限
        requestPerm();

        setContentView(R.layout.geographic_map);

        ll_indicator_back = findViewById(R.id.ll_indicator_back);
        ll_indicator_back.setOnClickListener(this);
        tv_base_title = findViewById(R.id.tv_base_title);
        tv_base_title.setText("位置");
        //获取位置
        tv_locatoin = findViewById(R.id.tv_locatoin);

        LinearLayout ll_base_edit = findViewById(R.id.ll_base_edit);
        ll_base_edit.setVisibility(View.VISIBLE);
        TextView tv_base_edit = findViewById(R.id.tv_base_edit);
        tv_base_edit.setText("确定");

        ll_base_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack!=null){
                    mMapView.getMap().getMapScreenShot(GeographicMapActivity.this);
                    mMapView.getMap().invalidate();// 刷新地图
                  //  latitude = latLng.latitude;
                    //longitude = latLng.longitude;

                }

                Intent intent = getIntent();
                intent.putExtra("address", simpleAddress);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);

                if (mCallBack!=null){
                    mCallBack.onSuccess(intent);
                }

                finish();
            }
        });

        //设置地图
        mMapView = (MapView) findViewById(R.id.map);
        AMap viewMap = mMapView.getMap();
        //对amap添加单击地图事件监听器
        viewMap.setOnMapClickListener(this);
        viewMap.setOnCameraChangeListener(this);// 对amap添加移动地图事件监听器

        //地理搜索类
        try {
            geocodeSearch = new GeocodeSearch(this);
            geocodeSearch.setOnGeocodeSearchListener(this);
        } catch (AMapException e) {
            e.printStackTrace();
        }


        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_indicator_back) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    private void requestPerm() {
        XXPermissions.with(this)
                //单个权限
                .permission(Permission.RECORD_AUDIO)
                .permission(Permission.ACCESS_FINE_LOCATION)
                .permission(Permission.ACCESS_COARSE_LOCATION)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .permission(Permission.CAMERA)
                // .interceptor(new IPermissionInterceptor() {})
                //  .unchecked()
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            //  ToastUtils.show("权限所有的获取");
                            //开始定位
                            initLoc();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            Log.e(TAG, "onDenied：被永久拒绝授权，请手动授予权限 ");
                            XXPermissions.startPermissionActivity(GeographicMapActivity.this, permissions);
                        } else {
                            Log.e(TAG, "onDenied: 权限获取失败");
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == XXPermissions.REQUEST_CODE) {
            if (XXPermissions.isGranted(this, Permission.ACCESS_FINE_LOCATION) &&
                    XXPermissions.isGranted(this, Permission.ACCESS_COARSE_LOCATION)) {
                ToastUtils.show("权限已获取");

                //开始定位
                initLoc();
            } else {
                ToastUtils.show("权限获取失败");
            }
        }
    }


    //定位
    private void initLoc() {
        //初始化定位
        try {
            //在构造AMapLocationClient 之前必须进行合规检查，设置接口之前保证隐私政策合规，检查接口如下
            AMapLocationClient.updatePrivacyShow(this, true, true);
            AMapLocationClient.updatePrivacyAgree(this, true);
            mLocationClient = new AMapLocationClient(getApplicationContext());
            //设置定位回调监听
            mLocationClient.setLocationListener(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            //设置是否只定位一次,默认为false
            mLocationOption.setOnceLocation(false);
            //设置是否强制刷新WIFI，默认为强制刷新
            mLocationOption.setWifiActiveScan(true);
            //设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(false);
            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(1000);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            //启动定位
            mLocationClient.startLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ;
    }


    /**
     * func:获取屏幕中心的经纬度坐标
     *
     * @return 经纬度
     */
    public LatLng getMapCenterPoint() {
        int left = mMapView.getLeft();
        int top = mMapView.getTop();
        int right = mMapView.getRight();
        int bottom = mMapView.getBottom();
        // 获得屏幕点击的位置
        int x = (int) (mMapView.getX() + (right - left) / 2);
        int y = (int) (mMapView.getY() + (bottom - top) / 2);
        Projection projection = mMapView.getMap().getProjection();
        LatLng pt = projection.fromScreenLocation(new Point(x, y));
        return pt;
    }


    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码


                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
//                Log.e("Amap" + "getLatitude" + amapLocation.getLatitude(), null);
//                Log.e("Amap" + "getLongitude" + amapLocation.getLongitude(), null);
//                Log.e("Amap" + "location" + amapLocation.getAddress(), null);
                //获取屏幕中心的经纬度

            if (isFist){
                LatLng latLng = getMapCenterPoint();
                latitude = latLng.latitude;
                longitude = latLng.longitude;


//                MarkerOptions otMarkerOptions = new MarkerOptions();
//                otMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_current_port));
//                otMarkerOptions.position(latLng);
//                getAddressByLatlng(latLng);
//                mMapView.getMap().addMarker(otMarkerOptions);


                MarkerOptions  markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .position(latLng)
                        .draggable(true);
                mMapView.getMap().addMarker(markerOption);
                //设置当前的经纬度
                mMapView.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));


                MyLocationStyle myLocationStyle;
                myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
                myLocationStyle.interval(1000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
                myLocationStyle.showMyLocation(true);////设置是否显示定位小蓝点，用于满足只想使用定位，不想使用定位小蓝点的场景，设置false以后图面上不再有定位蓝点的概念，但是会持续回调位置信息。
                mMapView.getMap().setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
                mMapView.getMap().setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

                mMapView.getMap().getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
                isFist = false;
                UiSettings uiSettings =    mMapView.getMap().getUiSettings();
                uiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
                uiSettings.setMyLocationButtonEnabled(true);
            }



            }

            //  SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setString("current_Address", amapLocation.getAddress());
            //  SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setString("getLatitude", amapLocation.getLatitude() + "");
            //   SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setString("getLongitude", amapLocation.getLongitude() + "");


        } else {
            //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
            Log.e("AmapError", "c Error, ErrCode:"
                    + amapLocation.getErrorCode() + ", errInfo:"
                    + amapLocation.getErrorInfo());

            //Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMapView.getMap().clear();
         latitude = latLng.latitude;
         longitude = latLng.longitude;
        MarkerOptions otMarkerOptions = new MarkerOptions();
        otMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.chance_fream));
        otMarkerOptions.position(latLng);
        getAddressByLatlng(latLng);
        mMapView.getMap().addMarker(otMarkerOptions);
        mMapView.getMap().moveCamera(CameraUpdateFactory.changeLatLng(latLng));


        //SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setString("getLatitude", latitude + "");
        //  SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setString("getLongitude", longitude + "");
    }

    private void getAddressByLatlng(LatLng latLng) {
        //逆地理编码查询条件：逆地理编码查询的地理坐标点、查询范围、坐标类型。
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500f, GeocodeSearch.AMAP);
        //异步查询
        geocodeSearch.getFromLocationAsyn(query);
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
        String formatAddress = regeocodeAddress.getFormatAddress();
        simpleAddress = formatAddress.substring(9);
       // Log.i("当前位置。。。。。"+simpleAddress, null);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_locatoin.setText(simpleAddress);
            }
        });

    }
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        LatLng latLng = cameraPosition.target;
        mMapView.getMap().clear();
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        MarkerOptions otMarkerOptions = new MarkerOptions();
        otMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.chance_fream));
        otMarkerOptions.position(latLng);
        getAddressByLatlng(latLng);
        mMapView.getMap().addMarker(otMarkerOptions);
        mMapView.getMap().moveCamera(CameraUpdateFactory.changeLatLng(latLng));


        //SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setString("getLatitude", latitude + "");
        // SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setString("getLongitude", longitude + "");
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

    }

    @Override
    public void onMapScreenShot(Bitmap bitmap) {


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String path =Environment.getExternalStorageDirectory() + "/test_"
                + sdf.format(new Date()) + ".png";
        if(null == bitmap){
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(
                    path);
            boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            try {
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (b){
                //对图片压缩
//                Bitmap bitmap1 = openImage(path);
//                Bitmap bitmap2 = compressImage(bitmap1);
//
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitmap2.compress(Bitmap.CompressFormat.PNG, 100, baos);



            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }








}
