package com.dylanbeebe.chorebuddy.UI;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dylanbeebe.chorebuddy.R;
import com.dylanbeebe.chorebuddy.database.Repository;
import com.dylanbeebe.chorebuddy.entities.Chore;
import com.dylanbeebe.chorebuddy.entities.CompletedChore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
        addChoreFAB.setOnClickListener(view -> {
            Intent intent = new Intent(ChoreList.this, ChoreDetails.class);
            startActivity(intent);
        });

        // Report visible Chores FAB
        FloatingActionButton reportChoresFAB = findViewById(R.id.choreList_report);
        reportChoresFAB.setOnClickListener(v -> {
            // Debug data
            //Toast.makeText(ChoreList.this, "Report FAB tapped.", Toast.LENGTH_LONG).show();
            // insertSampleChores(repository);

            report();
        });

        TextInputEditText searchInput =
                findViewById(R.id.choreList_searchInput);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                choreAdapter.filter(s.toString());
            }

            @Override public void afterTextChanged(Editable s) {}
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
        // 1. Get the visible list from adapter immediately
        List<Chore> visibleChores = choreAdapter.getVisibleChores();

        if (visibleChores == null || visibleChores.isEmpty()) {
            Toast.makeText(this, "Nothing to print!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Switch to background thread for DB and File I/O
        // (Assuming your executor in Repository is accessible)
        new Thread(() -> {
            PdfDocument document = new PdfDocument();
            // A4 size: 595 x 842 points
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            android.graphics.Canvas canvas = page.getCanvas();
            Paint paint = new Paint();

            int x = 50;
            int y = 60;

            // Title
            paint.setTextSize(24);
            paint.setFakeBoldText(true);
            canvas.drawText("ChoreBuddy Report", x, y, paint);

            y += 25;
            paint.setTextSize(10);
            paint.setFakeBoldText(false);
            paint.setColor(Color.GRAY);
            canvas.drawText("Generated on: " + LocalDate.now().toString(), x, y, paint);

            y += 40;
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
                    .withZone(ZoneId.systemDefault());

            for (Chore chore : visibleChores) {
                // Basic Page Overflow Check
                if (y > 780) break;

                // Draw Chore Name
                paint.setColor(Color.BLACK);
                paint.setTextSize(14);
                paint.setFakeBoldText(true);
                canvas.drawText(chore.getName(), x, y, paint);

                // Draw Status tag
                paint.setTextSize(10);
                String status = chore.isActive() ? "ACTIVE" : "INACTIVE";
                canvas.drawText(status, 480, y, paint);

                y += 20;

                // Fetch completions for this chore using your new sync method
                List<CompletedChore> history = repository.getCompletedChoresSync(chore.getId());

                paint.setFakeBoldText(false);
                paint.setColor(Color.DKGRAY);

                if (history != null && !history.isEmpty()) {
                    canvas.drawText("Completion History:", x + 20, y, paint);
                    y += 15;

                    // Show up to 3 most recent completions
                    int limit = Math.min(history.size(), 3);
                    for (int i = 0; i < limit; i++) {
                        String date = timeFormatter.format(Instant.ofEpochMilli(history.get(i).getCompletedAt()));
                        canvas.drawText("• " + date, x + 35, y, paint);
                        y += 15;
                    }
                } else {
                    canvas.drawText("No history recorded.", x + 20, y, paint);
                    y += 15;
                }

                y += 10;
                paint.setAlpha(40); // Light divider
                canvas.drawLine(x, y, 545, y, paint);
                paint.setAlpha(255);
                y += 30;
            }

            document.finishPage(page);

            // 3. Save as ChoreBuddy_YYYY-MM-DD.pdf
            String fileName = "ChoreBuddy_" + LocalDate.now().toString() + ".pdf";
            File file = new File(getExternalCacheDir(), fileName);

            try {
                document.writeTo(new FileOutputStream(file));
                document.close();

                // 4. Share on UI Thread
                runOnUiThread(() -> sharePdf(file));
            } catch (Exception e) {
                document.close();
                runOnUiThread(() -> Toast.makeText(this, "Failed to create PDF", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void sharePdf(File file) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share Report"));
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
