package com.pingmo.chengyan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.shop.activity.WeChatMallActivity;

public class FindFragment extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.rl_find_shop).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_find_shop:
                Intent intent = new Intent(getContext(), WeChatMallActivity.class);
                startActivity(intent);
                break;
        }
    }
}
