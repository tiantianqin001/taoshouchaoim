package com.pingmo.chengyan.activity.zxing;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.pingmo.chengyan.R;

public class ScanResultsActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_result_layout);
    }
}
