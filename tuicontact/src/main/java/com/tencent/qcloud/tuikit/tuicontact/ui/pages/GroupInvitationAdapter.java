package com.tencent.qcloud.tuikit.tuicontact.ui.pages;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.qcloud.tuicore.TUIThemeManager;
import com.tencent.qcloud.tuicore.component.gatherimage.ShadeImageView;
import com.tencent.qcloud.tuicore.component.imageEngine.impl.GlideEngine;
import com.tencent.qcloud.tuicore.util.BackgroundTasks;
import com.tencent.qcloud.tuicore.util.ImageUtil;
import com.tencent.qcloud.tuicore.util.ThreadHelper;
import com.tencent.qcloud.tuikit.tuicontact.R;
import com.tencent.qcloud.tuikit.tuicontact.bean.GroupSelectBean;
import com.tencent.qcloud.tuikit.tuicontact.net.Common;
import com.tencent.qcloud.tuikit.tuicontact.ui.OkHttp3_0Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GroupInvitationAdapter extends RecyclerView.Adapter {
    private  TextView agree;
    private  TextView reject;
    private  TextView name;
    private Activity context;
    private List<GroupSelectBean> groupSelectBeanList;
    private String token;
    public  ShadeImageView avatar;
    private  TextView description;
    private  LinearLayout ll_agree_reject;
    private  TextView tv_agree_reject;

    public GroupInvitationAdapter(Activity context, List<GroupSelectBean> groupSelectBeanList, String token) {
        this.context = context;
        this.groupSelectBeanList = groupSelectBeanList;
        this.token = token;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_new_group_item, parent, false);
        ViewHolder  viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setIsRecyclable(false);
        GroupSelectBean groupSelectBean = groupSelectBeanList.get(position);
        String savedIcon = ImageUtil.getGroupConversationAvatar("group_"+groupSelectBean.getGroupId());

        //显示群头像
        //根据id获取存储的文件路径
        final File file = new File( savedIcon);
        Bitmap existsBitmap = null;
        if (file.exists() && file.isFile()) {
            //文件存在，加载到内存
            BitmapFactory.Options options = new BitmapFactory.Options();
            existsBitmap = BitmapFactory.decodeFile(file.getPath(), options);
            if (options.outWidth > 0 && options.outHeight > 0) {
                //当前文件是图片
                GlideEngine.loadUserIcon(avatar, existsBitmap);
            }

        }else {
            //todo 判断是不是线上地址
           // GlideEngine.loadUserIcon(avatar, existsBitmap);
        }

//        GlideEngine.loadUserIcon(avatar, savedIcon, TUIThemeManager.getAttrResId(avatar.getContext(),
//                R.attr.core_default_group_icon), 5);
        String groupName = replaceBlank(groupSelectBean.getGroupName());
        name.setText(groupName);
        String inviterName = groupSelectBean.getInviterName();

        int status = groupSelectBean.getStatus();
        //status 0 还没有授权  1 已同意  2 已拒绝
        if(status == 0){
            ll_agree_reject.setVisibility(View.VISIBLE);
            tv_agree_reject.setVisibility(View.GONE);
            String replaceAll = inviterName.replaceAll("\r|\n*", "");
            description.setText(replaceAll+"邀请"+groupSelectBean.getUserNickName()+"入群");
        }else {
            ll_agree_reject.setVisibility(View.GONE);
            tv_agree_reject.setVisibility(View.VISIBLE);

            String replaceAll = inviterName.replaceAll("\r|\n*", "");

            if (status == 1){
                tv_agree_reject.setText(groupSelectBean.getOperatorName()+" 已同意");
                description.setText(replaceAll+"邀请"+groupSelectBean.getUserNickName()+"入群");
            }else if (status == 2){
                tv_agree_reject.setText(groupSelectBean.getOperatorName()+" 已拒绝");
                description.setText(replaceAll+"拒绝"+groupSelectBean.getUserNickName()+"入群");
            }
        }



        //同意和拒绝的点击
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = Common.GROUP_INVITATION_OPERATE;
                Map<String, String> headerParams = new LinkedHashMap<>();
                headerParams.put("token", token);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id",groupSelectBean.getId());
                    jsonObject.put("status",true);
                    OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url,jsonObject , headerParams,
                            new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                }
                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    String content = response.body().string();
                                    Log.i("", "onResponse: "+content);
                                    try {
                                        JSONObject jsonObject = new JSONObject(content);
                                        int code = jsonObject.optInt("code");
                                        if (code == 200){
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    groupSelectBeanList.remove(position);
                                                    notifyDataSetChanged();
                                                }
                                            });


                                        } else if (code == 403){
                                            Intent intent = new Intent();
                                            intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                            intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                                    "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                                            context.sendBroadcast(intent);
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = Common.GROUP_INVITATION_OPERATE;
                Map<String, String> headerParams = new LinkedHashMap<>();
                headerParams.put("token", token);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id",groupSelectBean.getId());
                    jsonObject.put("status",false);
                    OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url,jsonObject , headerParams,
                            new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                                }
                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    String content = response.body().string();
                                    Log.i("", "onResponse: "+content);
                                    try {
                                        JSONObject jsonObject = new JSONObject(content);
                                        int code = jsonObject.optInt("code");
                                        if (code == 200){
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    groupSelectBeanList.remove(position);
                                                    notifyDataSetChanged();
                                                }
                                            });


                                        } else if (code == 403){
                                            Intent intent = new Intent();
                                            intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                            intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                                    "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                                            context.sendBroadcast(intent);
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return groupSelectBeanList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder{



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //头像
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            agree = itemView.findViewById(R.id.agree);
            reject = itemView.findViewById(R.id.reject);
            ll_agree_reject = itemView.findViewById(R.id.ll_agree_reject);
            tv_agree_reject = itemView.findViewById(R.id.tv_agree_reject);

        }
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\\t|\\r|\\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
}
