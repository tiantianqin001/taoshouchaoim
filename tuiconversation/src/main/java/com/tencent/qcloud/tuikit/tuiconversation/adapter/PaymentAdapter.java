package com.tencent.qcloud.tuikit.tuiconversation.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tuikit.tuiconversation.R;
import com.tencent.qcloud.tuikit.tuiconversation.bean.SystemMessageBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter {
    private List<V2TIMMessage> systemMessage;
    private TextView tv_system_set_pm;
    private TextView tv_system_create_time;

    private  TextView tv_paylent_title;

    public TextView tv_money_title;
    private  TextView tv_money;
    private  LinearLayout ll_withdrawal_method;
    private  LinearLayout ll_card_number;
    private  TextView tv_card;

    public PaymentAdapter(List<V2TIMMessage> systemMessage) {
        this.systemMessage = systemMessage;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //将我们自定义的item布局R.layout.item_one转换为View
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_paylent_set, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        V2TIMMessage v2TIMMessage = systemMessage.get(position);
        byte[] data = v2TIMMessage.getCustomElem().getData();
        String message = new String(data);
        if (!TextUtils.isEmpty(message)){
            try {
                JSONObject jsonObject = new JSONObject(message);
                String createTime = jsonObject.optString("createTime");
                if (!TextUtils.isEmpty(createTime)){
                    tv_system_create_time.setText(createTime);
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
                String title = jsonObject.optString("title");
                if (!TextUtils.isEmpty(title)){
                    tv_paylent_title.setText(title);
                }
                String type = jsonObject.optString("type");
                if (type.equals("2")){
                    //体现通知
                   tv_money_title.setVisibility(View.VISIBLE);
                    tv_money_title.setText("提现金额");
                    tv_money.setVisibility(View.VISIBLE);
                    ll_withdrawal_method.setVisibility(View.VISIBLE);
                    ll_card_number.setVisibility(View.VISIBLE);
                    String money = jsonObject.optString("money");
                    if (!TextUtils.isEmpty(money)){
                        tv_money.setText("￥"+money+".00");
                    }
                    String card = jsonObject.optString("card");
                    if (!TextUtils.isEmpty(card)){
                        tv_card.setText(card);
                    }
                }else if (type.equals("1")){
                    //充值通知
                    tv_money_title.setVisibility(View.VISIBLE);
                    tv_money_title.setText("充值金额");
                    tv_money.setVisibility(View.VISIBLE);

                    String money = jsonObject.optString("money");
                    if (!TextUtils.isEmpty(money)){
                        tv_money.setText("￥"+money+".00");
                    }
                }else if (type.equals("3")){
                    //红包退回
                    tv_money_title.setVisibility(View.VISIBLE);
                    tv_money_title.setText("红包退回");
                    tv_money.setVisibility(View.VISIBLE);

                    String money = jsonObject.optString("money");
                    if (!TextUtils.isEmpty(money)){
                        tv_money.setText("￥"+money);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        return systemMessage.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_system_set_pm = itemView.findViewById(R.id.tv_system_set_pm);
            tv_paylent_title = itemView.findViewById(R.id.tv_paylent_title);
            tv_system_create_time = itemView.findViewById(R.id.tv_system_create_time);


            //体现
            tv_money_title = itemView.findViewById(R.id.tv_money_title);
            tv_money = itemView.findViewById(R.id.tv_money);
            tv_card = itemView.findViewById(R.id.tv_card);
            ll_withdrawal_method = itemView.findViewById(R.id.ll_withdrawal_method);
            ll_card_number = itemView.findViewById(R.id.ll_card_number);


        }
    }
}
