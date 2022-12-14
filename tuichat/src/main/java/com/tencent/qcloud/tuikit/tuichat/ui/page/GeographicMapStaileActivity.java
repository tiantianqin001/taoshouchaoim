package com.tencent.qcloud.tuikit.tuichat.ui.page;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
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
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.Projection;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static androidx.constraintlayout.widget.StateSet.TAG;

public class GeographicMapStaileActivity extends Activity implements View.OnClickListener {

    private TextView tv_base_title;
    private LinearLayout ll_indicator_back;

    public static IUIKitCallback mCallBack;
    private MapView mMapView;
    //??????AMapLocationClient?????????
    public AMapLocationClient mLocationClient = null;
    //?????????AMapLocationClientOption??????
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
        //???????????????
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //?????????????????????
        ImmersionBar.with(this)
                .transparentBar()
                .init();


        setContentView(R.layout.geographic_map_staile);

        ll_indicator_back = findViewById(R.id.ll_indicator_back);
        ll_indicator_back.setOnClickListener(this);
        tv_base_title = findViewById(R.id.tv_base_title);
        tv_base_title.setText("????????????");

        //????????????
        mMapView = (MapView) findViewById(R.id.map);
        AMap viewMap = mMapView.getMap();

        //???activity??????onCreate?????????mMapView.onCreate(savedInstanceState)???????????????
        mMapView.onCreate(savedInstanceState);

        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);
        String address = getIntent().getStringExtra("address");
        mMapView.getMap().clear();
        LatLng latLng = new LatLng(latitude,longitude);
        MarkerOptions otMarkerOptions = new MarkerOptions();
        otMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.chance_fream));
        otMarkerOptions.snippet(address);
        otMarkerOptions.position(latLng);
        mMapView.getMap().addMarker(otMarkerOptions).showInfoWindow();


        mMapView.getMap().setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        mMapView.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
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
        //???activity??????onDestroy?????????mMapView.onDestroy()???????????????
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //???activity??????onResume?????????mMapView.onResume ()???????????????????????????
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //???activity??????onPause?????????mMapView.onPause ()????????????????????????
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //???activity??????onSaveInstanceState?????????mMapView.onSaveInstanceState (outState)??????????????????????????????
        mMapView.onSaveInstanceState(outState);
    }

}
