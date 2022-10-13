package com.pingmo.chengyan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.persion.DeviceManagementActivity;
import com.pingmo.chengyan.bean.DeviceMessageBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DeviceManageAdapter  extends RecyclerView.Adapter<DeviceManageAdapter.ViewHolder> {


    private Context content;
    private List<DeviceMessageBean.DataDTO> data;
    private OnClickListener listener;

    public DeviceManageAdapter(Context content, List<DeviceMessageBean.DataDTO> data) {

        this.content = content;
        this.data = data;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        return new ViewHolder( LayoutInflater.from(parent.getContext()).inflate(R.layout.item_advice_message_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DeviceManageAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        DeviceMessageBean.DataDTO dataDTO = data.get(position);
        holder.tv_enterprise_name.setText(dataDTO.deviceName);
        holder.tv_enterprise_time.setText(dataDTO.loginTime);

        holder.tv_advice_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    listener.onClickListener(position);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv_enterprise_name;
        private final TextView tv_enterprise_time;
        private final TextView tv_advice_del;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tv_enterprise_name = itemView.findViewById(R.id.tv_enterprise_name);
            tv_enterprise_time = itemView.findViewById(R.id.tv_enterprise_time);
            //删除
            tv_advice_del = itemView.findViewById(R.id.tv_advice_del);
        }
    }


    public interface OnClickListener{
        void onClickListener(int pos);
    }
    public void setOnClickListener(OnClickListener listener){

        this.listener = listener;
    }
}
