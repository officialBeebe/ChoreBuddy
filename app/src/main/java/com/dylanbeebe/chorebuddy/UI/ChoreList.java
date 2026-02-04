package com.dylanbeebe.chorebuddy.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dylanbeebe.chorebuddy.BuildConfig;
import com.dylanbeebe.chorebuddy.R;
import com.dylanbeebe.chorebuddy.database.Repository;
import com.dylanbeebe.chorebuddy.entities.Chore;
import com.dylanbeebe.chorebuddy.entities.CompletedChore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

public class ChoreList extends BaseActivity implements ChoreAdapter.OnChoreSwipeListener {
    private Repository repository;
    private ChoreAdapter choreAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setEdgeToEdgeContentView(R.layout.activity_chore_list);

        repository = new Repository(getApplication());

        // Add Chore FAB
        FloatingActionButton addChoreFAB = findViewById(R.id.choreList_add);
        addChoreFAB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChoreList.this, ChoreDetails.class);
                startActivity(intent);
            }

        });

        FloatingActionButton reportChoresFAB = findViewById(R.id.choreList_report);
        reportChoresFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Debug inserts
                // insertSampleChores(repository);

                // Report chores
                Toast.makeText(ChoreList.this, "Report FAB tapped.", Toast.LENGTH_LONG).show();
            }
        });

        // Bind RecyclerView to chore list
        RecyclerView recyclerView = findViewById(R.id.choreDetails_recyclerView);

        ChoreAdapter.OnChoreSwipeListener choreSwipeListener;

        // initialize the adapter
        choreAdapter = new ChoreAdapter(this, this);
        recyclerView.setAdapter(choreAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Swipe helper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ChoreSwipeCallback(choreAdapter));

        itemTouchHelper.attachToRecyclerView(recyclerView);


        // Subscribe to the data instead of asking for it when using async LiveData
        // asynchronous check for existing chores. you must observe the data, subscribe to it, learn from it, but never TAKE!
        repository.getAllChores().observe(this, chores -> {
            if (chores != null) {
                chores.sort(
                        Comparator
                                .comparing(Chore::isActive).reversed()
                                .thenComparing(Chore::getEndAt)
                );
            }

            choreAdapter.setChores(chores);
            // TODO: Uncomment below before production AND set activity_chore_list's report FAB to disabled by default. This is for the debug chore inserts.
            // reportFab.setEnabled(!chores.isEmpty());
        });

    }

    void report() {
        // start share intent with generated pdf

        // get sorted current and completed chores from repository and generate a tabular csv report then convert to pdf and return


    }

    private void insertSampleChores(Repository repository) {

        // Minimum
        String chore_1_name = "Wash the dog";
        long chore_1_endAt =
                LocalDate.of(2026, 2, 13)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli(); // Feb 13, 2026 midnight/start of day
        Chore chore_1 = new Chore(0, chore_1_name, System.currentTimeMillis(), System.currentTimeMillis(), chore_1_endAt, false, 0, false, true);
        repository.insertChore(chore_1);

        // Maximum
        String chore_2_name = "Balance checkbook";
        long chore_2_endAt =
                LocalDate.of(2026, 2, 3) // TODO: edit this for quicker progress indicator testing
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli(); // Feb 8, 2026 inclusive → store Feb 9, 2026 at 00:00
        boolean chore_2_is_repeat = true;
        int chore_2_repeat_days = 7;
        boolean chore_2_is_alert = true;
        boolean chore_2_is_active = true;

        Chore chore_2 = new Chore(0, chore_2_name, System.currentTimeMillis(), System.currentTimeMillis(), chore_2_endAt, chore_2_is_repeat, chore_2_repeat_days, chore_2_is_alert, chore_2_is_active);
        repository.insertChore(chore_2);

        // Toast.makeText(ChoreList.this, BuildConfig.DEBUG_MESSAGE_SAMPLE_CODE, Toast.LENGTH_LONG).show();
        Toast.makeText(ChoreList.this, "Sample chores added.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        choreAdapter.startTicker();
    }

    @Override
    protected void onStop() {
        super.onStop();
        choreAdapter.stopTicker();
    }


    @Override
    public void onSwipeLeft(int position) {
        Chore chore = choreAdapter.getChoreAt(position);

        if (!chore.isActive()) return;

        chore.setActive(false);
        repository.updateChore(chore);

        choreAdapter.notifyItemChanged(position);


    }


    @Override
    public void onSwipeRight(int position) {
        Chore chore = choreAdapter.getChoreAt(position);
        if (chore == null) return;

        long now = System.currentTimeMillis();

        // 1️⃣ Always record completion
        repository.insertCompletedChore(
                new CompletedChore(
                        now,
                        chore.getId()
                )
        );

        // 2️⃣ Update chore state
        if (chore.isRepeat()) {
            long durationMillis =
                    Duration.ofDays(chore.getRepeatDays()).toMillis();

            chore.setStartAt(now);
            chore.setEndAt(chore.getStartAt() + durationMillis);
            chore.setActive(true);
        } else {
            chore.setEndAt(now);
            chore.setActive(false);
        }

        repository.updateChore(chore);

        choreAdapter.notifyItemChanged(position);
    }

}
