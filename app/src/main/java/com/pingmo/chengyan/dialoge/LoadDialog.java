package com.pingmo.chengyan.dialoge;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pingmo.chengyan.R;


/**
 * Created by alany on 2017/4/12.
 */
public class LoadDialog extends Dialog {
    private Context context;
    private TextView tv_login_button;


    public LoadDialog(Context context){
        super(context, R.style.dialogForgetPwd);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setCancelable(true);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_load_layout, null);
        tv_login_button = view.findViewById(R.id.tv_login_button);
        setContentView(view);
    }


    public void setText(String data){
        if (tv_login_button!=null){
            tv_login_button.setText(data);
        }
    }

}