package com.dylanbeebe.chorebuddy.UI;

import android.content.Intent;
import android.os.Bundle;

import com.dylanbeebe.chorebuddy.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setEdgeToEdgeContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, ChoreList.class);
        startActivity(intent);
        finish();
    }
}

// https://github.com/material-components/material-components-android/tree/master/docs/components

// https://fonts.google.com/icons

