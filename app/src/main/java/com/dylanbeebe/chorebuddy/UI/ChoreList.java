package com.dylanbeebe.chorebuddy.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dylanbeebe.chorebuddy.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChoreList extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setEdgeToEdgeContentView(R.layout.activity_chore_list);

        FloatingActionButton fab = findViewById(R.id.choreList_add);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChoreList.this, ChoreDetails.class);
                startActivity(intent);
            }

        });

        FloatingActionButton reportFab = findViewById(R.id.choreList_report);

    }

    void report() {
        // start share intent with generated pdf

        // get sorted current and completed chores from repository and generate a tabular csv report then convert to pdf and return


    }


}
