package com.tencent.qcloud.tuikit.tuigroup.ui.page;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.qcloud.tuicore.component.CustomLinearLayoutManager;
import com.tencent.qcloud.tuicore.component.TitleBarLayout;
import com.tencent.qcloud.tuicore.component.action.PopDialogAdapter;
import com.tencent.qcloud.tuicore.component.action.PopMenuAction;
import com.tencent.qcloud.tuicore.component.activities.BaseLightActivity;
import com.tencent.qcloud.tuicore.component.gatherimage.ShadeImageView;
import com.tencent.qcloud.tuicore.component.imageEngine.impl.GlideEngine;
import com.tencent.qcloud.tuicore.component.interfaces.ITitleBarLayout;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuicore.util.ScreenUtil;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuigroup.R;
import com.tencent.qcloud.tuikit.tuigroup.TUIGroupConstants;
import com.tencent.qcloud.tuikit.tuigroup.bean.GroupInfo;
import com.tencent.qcloud.tuikit.tuigroup.bean.GroupMemberInfo;
import com.tencent.qcloud.tuikit.tuigroup.net.Common;
import com.tencent.qcloud.tuikit.tuigroup.net.EncryptUtil;
import com.tencent.qcloud.tuikit.tuigroup.net.OkHttp3_0Utils;
import com.tencent.qcloud.tuikit.tuigroup.presenter.GroupManagerPresenter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SetGroupManagerActivity extends BaseLightActivity {
    private static final String TAG = "";
    private TitleBarLayout titleBarLayout;
    private TextView managerCountLabel;

    private GroupManagerPresenter presenter;
    private GroupInfo groupInfo;
    private RecyclerView managerList;
    private ManagerAdapter managerAdapter;
    private ShadeImageView ownerFace;
    private TextView ownerName;
    private View setManagerView;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_group_manager);
        titleBarLayout = findViewById(R.id.set_group_manager_title_bar);
        managerCountLabel = findViewById(R.id.set_group_manager_manager_label);
        managerList = findViewById(R.id.set_group_manager_manager_list);
        ownerFace = findViewById(R.id.set_group_manager_owner_face);
        ownerName = findViewById(R.id.set_group_manager_owner_name);
        setManagerView = findViewById(R.id.set_group_manager_add_manager);

        titleBarLayout.setBackgroundColor(getResources().getColor(R.color.white));
        titleBarLayout.getLeftIcon().setBackground(getResources().getDrawable(R.drawable.back));


        RelativeLayout rootView = titleBarLayout.getRootView();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rootView.getLayoutParams();
        params.setMargins(0,0,0,0);
        rootView.setLayoutParams(params);

        titleBarLayout.setTitle(getString(R.string.group_set_manager), ITitleBarLayout.Position.MIDDLE);
        managerList.setLayoutManager(new CustomLinearLayoutManager(this));
        managerAdapter = new ManagerAdapter();
        managerList.setAdapter(managerAdapter);
        groupInfo = (GroupInfo) getIntent().getSerializableExtra(TUIGroupConstants.Group.GROUP_INFO);

        presenter = new GroupManagerPresenter();

        setClickListener();
        loadGroupManager();
        loadGroupOwner();
    }

    private void loadGroupManager() {
        //获取管理员
        presenter.loadGroupManager(groupInfo.getId(), new IUIKitCallback<List<GroupMemberInfo>>() {
            @Override
            public void onSuccess(List<GroupMemberInfo> data) {
                if (data != null) {
                    managerCountLabel.setText(getString(R.string.group_add_manager_count_label, data.size()));
                    managerAdapter.setGroupMemberInfoList(data);
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
    }

    private void loadGroupOwner() {
        presenter.loadGroupOwner(groupInfo.getId(), new IUIKitCallback<GroupMemberInfo>() {
            @Override
            public void onSuccess(GroupMemberInfo data) {
                String faceUrl = data.getIconUrl();
                String displayName = getDisplayName(data);
                GlideEngine.loadUserIcon(ownerFace, faceUrl);
                ownerName.setText(displayName);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
    }

    private void setClickListener() {
        titleBarLayout.getLeftIcon().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setManagerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetGroupManagerActivity.this, GroupMemberActivity.class);
                intent.putExtra(TUIGroupConstants.Selection.IS_SELECT_MODE, true);
                intent.putExtra(TUIGroupConstants.Selection.IS_GROUP_MANAGE,true);
                intent.putExtra(TUIGroupConstants.Group.GROUP_INFO, groupInfo);
                startActivityForResult(intent, 1);
            }
        });

        managerAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onClick(View view, GroupMemberInfo memberInfo, int position) {
                Drawable drawable = view.getBackground();
                if (drawable != null) {
                    drawable.setColorFilter(0xd9d9d9, PorterDuff.Mode.SRC_IN);
                }
                View itemPop = LayoutInflater.from(SetGroupManagerActivity.this).inflate(R.layout.group_manager_pop_menu, null);
                PopupWindow popupWindow = new PopupWindow(itemPop, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow.setBackgroundDrawable(new ColorDrawable());
                popupWindow.setOutsideTouchable(true);
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (drawable != null) {
                            drawable.clearColorFilter();
                        }
                    }
                });
                TextView popText = itemPop.findViewById(R.id.pop_text);
                popText.setText(R.string.group_remove_manager_label);
                popText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        presenter.clearGroupManager(groupInfo.getId(), memberInfo.getAccount(), new IUIKitCallback<Void>() {
                            @Override
                            public void onSuccess(Void data) {
                                loadGroupManager();
                            }

                            @Override
                            public void onError(String module, int errCode, String errMsg) {

                            }
                        });
                    }
                });
                int x = view.getWidth() / 2;
                int y = - view.getHeight() / 3;
                int popHeight = ScreenUtil.dip2px(45) * 3;
                if (y + popHeight + view.getY() + view.getHeight() > managerList.getBottom()) {
                    y = y - popHeight;
                }
                popupWindow.showAsDropDown(view, x, y, Gravity.TOP | Gravity.START);
            }
        });

    }
    private static String SP_NAME = "huimiaomiao_share_date";
    private SharedPreferences sp;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            List<String> selectedList = data.getStringArrayListExtra(TUIGroupConstants.Selection.LIST);
            if (selectedList != null && !selectedList.isEmpty()) {
                //设置管理员
                sp =  getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                String token = sp.getString("token", "");

                token = EncryptUtil.getInstance(this).decrypt(token);
                String url = Common.SET_ADMINISTRATOR;

                Map<String , String> headerParams = new LinkedHashMap<>();
                headerParams.put("token", token);
                JSONArray jsonArray = new JSONArray();
                for (String s : selectedList) {
                    jsonArray.put(Integer.valueOf(s));

                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("groupId", Integer.valueOf(groupInfo.getId()));
                    jsonObject.put("members", jsonArray);
                    OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, jsonObject, headerParams, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String content = response.body().string();
                            Log.i(TAG, "onResponse: "+content);
                            try {
                                JSONObject jsonObject1 = new JSONObject(content);
                                int code = jsonObject1.optInt("code");
                                if (code == 200){
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadGroupManager();

                                        }
                                    });
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                for (String userId : selectedList) {
                    presenter.setGroupManager(groupInfo.getId(), userId, new IUIKitCallback<Void>() {
                        @Override
                        public void onSuccess(Void data) {
                            loadGroupManager();

                        }

                        @Override
                        public void onError(String module, int errCode, String errMsg) {
                            ToastUtil.toastShortMessage(errCode + ", " + errMsg);
                        }
                    });
                }
            }
        }
    }

    class ManagerAdapter extends RecyclerView.Adapter<ManagerAdapter.ManagerViewHolder> {

        private List<GroupMemberInfo> groupMemberInfoList;

        private OnItemLongClickListener onItemLongClickListener;

        public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
            this.onItemLongClickListener = onItemLongClickListener;
        }

        public void setGroupMemberInfoList(List<GroupMemberInfo> groupMemberInfoList) {
            this.groupMemberInfoList = groupMemberInfoList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ManagerAdapter.ManagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_manager_item, parent, false);
            return new ManagerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ManagerViewHolder holder, @SuppressLint("RecyclerView") int position) {
            GroupMemberInfo groupMemberInfo = groupMemberInfoList.get(position);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.onClick(v, groupMemberInfo, position);
                    }
                    return true;
                }
            });
            String displayName = getDisplayName(groupMemberInfo);
            holder.managerName.setText(displayName);
            GlideEngine.loadUserIcon(holder.faceIcon, groupMemberInfo.getIconUrl());
        }

        @Override
        public int getItemCount() {
            if (groupMemberInfoList == null || groupMemberInfoList.isEmpty()) {
                return 0;
            }
            return groupMemberInfoList.size();
        }

        class ManagerViewHolder extends RecyclerView.ViewHolder {
            ShadeImageView faceIcon;
            TextView managerName;
            public ManagerViewHolder(@NonNull View itemView) {
                super(itemView);
                faceIcon = itemView.findViewById(R.id.group_manager_face);
                managerName = itemView.findViewById(R.id.group_manage_name);
            }
        }
    }

    private String getDisplayName(GroupMemberInfo groupMemberInfo) {
        String displayName = groupMemberInfo.getNameCard();
        if (TextUtils.isEmpty(displayName)) {
            displayName = groupMemberInfo.getNickName();
        }
        if (TextUtils.isEmpty(displayName)) {
            displayName = groupMemberInfo.getAccount();
        }
        return displayName;
    }

    interface OnItemLongClickListener {
        void onClick(View view, GroupMemberInfo groupMemberInfo, int position);
    }
}