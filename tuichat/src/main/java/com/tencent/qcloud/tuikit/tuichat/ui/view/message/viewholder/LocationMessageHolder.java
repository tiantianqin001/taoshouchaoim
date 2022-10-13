package com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.tencent.qcloud.tuicore.component.imageEngine.impl.GlideEngine;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.TUIChatService;
import com.tencent.qcloud.tuikit.tuichat.bean.message.LocationMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;
import com.tencent.qcloud.tuikit.tuichat.ui.page.GeographicMapStaileActivity;


public class LocationMessageHolder extends MessageContentHolder {
private String url = "https://restapi.amap.com/v3/staticmap?";
    private ImageView mMapView;
    private double latitude;
    private double longitude;
    String address = "";
    private ImageView webView;
    private RelativeLayout rl_base;


    public LocationMessageHolder(View itemView) {
        super(itemView);

    }

    @Override
    public int getVariableLayout() {
        return R.layout.chat_location_layout;
    }

    @Override
    public void layoutVariableViews(TUIMessageBean msg, int position) {

        msgArea.setBackground(null);
        msgArea.setPadding(0, 0, 0, 0);
        webView = itemView.findViewById(R.id.iv_location_map);
        rl_base = itemView.findViewById(R.id.rl_base);
      TextView tv_location_map = itemView.findViewById(R.id.tv_location_map);


      if (msg instanceof LocationMessageBean){
          address = ((LocationMessageBean)msg).desc;
          latitude = ((LocationMessageBean) msg).latitude;
          longitude = ((LocationMessageBean) msg).longitude;
          ViewTreeObserver viewTreeObserver = webView.getViewTreeObserver();
          viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
              @Override
              public void onGlobalLayout() {
                  int height = webView.getMeasuredHeight();
                  int width = webView.getMeasuredWidth();
                  String homeUrl = url+"location="+longitude+","+latitude+"&zoom=18&markers=mid,,A:"+longitude+","+latitude
                          +"&size="+width/2+"*"+height/2+"&key=f8eb8f7894136df5e68da3f6a93e7f0f";
                  GlideEngine.loadImage(webView,homeUrl);
              }
          });
        //  setIamge(webView,homeUrl);
          //设置数据
          tv_location_map.setText(address);


      }
        rl_base.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开地图

                Intent intent = new Intent(itemView.getContext(), GeographicMapStaileActivity.class);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                intent.putExtra("address",address);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                TUIChatService.getAppContext().startActivity(intent);
            }
        });

    }




}
