package com.dylanbeebe.chorebuddy.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dylanbeebe.chorebuddy.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setEdgeToEdgeContentView(R.layout.activity_main);


        // countdown 3, 2, 1, then start ChoreList activity
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Intent intent = new Intent(MainActivity.this, ChoreList.class);
        startActivity(intent);
    }
}

// https://github.com/material-components/material-components-android/tree/master/docs/components

// https://fonts.google.com/icons

