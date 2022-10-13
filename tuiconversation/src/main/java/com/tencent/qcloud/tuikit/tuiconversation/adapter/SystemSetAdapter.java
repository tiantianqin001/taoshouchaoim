package com.tencent.qcloud.tuikit.tuiconversation.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.qcloud.tuikit.tuiconversation.R;
import com.tencent.qcloud.tuikit.tuiconversation.bean.SystemMessageBean;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SystemSetAdapter extends RecyclerView.Adapter {
    private List<SystemMessageBean> systemMessage;
    private  TextView tv_system_set_pm;
    private  TextView tv_system_title;
    private  TextView tv_system_create_time;
    private  TextView tv_sysytem_content;

    public SystemSetAdapter(List<SystemMessageBean> systemMessage) {
        this.systemMessage = systemMessage;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //将我们自定义的item布局R.layout.item_one转换为View
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_system_set, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SystemMessageBean systemMessageBean = systemMessage.get(position);
        String createTime = systemMessageBean.getCreateTime();
        if (!TextUtils.isEmpty(createTime)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            DateFormat formatter12 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                Date date = (Date) sdf.parse(createTime);
                long time = date.getTime();
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.setTimeInMillis(time);
                int apm = mCalendar.get(Calendar.AM_PM);
               // apm=0 表示上午，apm=1表示下午。
                String format = formatter12.format(date);
                if (apm == 0){
                    tv_system_set_pm.setText("上午 " + format.substring(11, 16));
                }else if (apm == 1){
                    tv_system_set_pm.setText("下午 " + format.substring(11,16));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        tv_system_title.setText(systemMessageBean.getTitle());
        tv_system_create_time.setText(systemMessageBean.getCreateTime());
        tv_sysytem_content.setText(systemMessageBean.getContent());
    }

    @Override
    public int getItemCount() {
        return systemMessage.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_system_set_pm = itemView.findViewById(R.id.tv_system_set_pm);
            tv_system_title = itemView.findViewById(R.id.tv_system_title);
            tv_system_create_time = itemView.findViewById(R.id.tv_system_create_time);
            tv_sysytem_content = itemView.findViewById(R.id.tv_sysytem_content);
        }
    }
}
