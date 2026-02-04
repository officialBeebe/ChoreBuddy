package com.dylanbeebe.chorebuddy.UI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;

import com.dylanbeebe.chorebuddy.R;
import com.dylanbeebe.chorebuddy.database.Repository;
import com.dylanbeebe.chorebuddy.entities.Chore;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ChoreDetails extends BaseActivity {
    // Intent
    long intentChoreId;

    // Chore state
    private Chore currentChore;
    private LiveData<Chore> currentChoreLiveData;

    // UI state
    ConstraintLayout choreDetailsLayout;
    LinearLayout choreDetails_heroLayout;
    MaterialTextView choreHeroNameTextView;
    CircularProgressIndicator choreHeroProgressIndicator;
    MaterialTextView choreHeroTimerTextView;
    TextInputEditText choreNameEditText;
    TextInputLayout choreEndAtLayout;
    TextInputEditText choreEndAtEditText;
    MaterialSwitch choreIsRepeatSwitch;
    TextInputLayout choreRepeatDaysLayout;
    TextInputEditText choreRepeatDaysEditText;
    MaterialSwitch choreIsAlertSwitch;
    MaterialSwitch choreIsActiveSwitch;

    // Buttons
    FloatingActionButton saveChoreFAB;
    FloatingActionButton deleteChoreFAB;

    // Data
    private Repository repository;

    // Ticker
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable ticker;
    private static final Duration TICK_INTERVAL = Duration.ofSeconds(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setEdgeToEdgeContentView(R.layout.activity_chore_details);

        // Layout
        choreDetailsLayout = findViewById(R.id.choreDetailsLayout);

        // Hero
        choreDetails_heroLayout = findViewById(R.id.choreDetails_heroLayout);
        choreHeroNameTextView = findViewById(R.id.choreDetails_choreHeroNameTextView);
        choreHeroProgressIndicator = findViewById(R.id.choreDetails_choreHeroProgressIndicator);
        choreHeroTimerTextView = findViewById(R.id.choreDetails_choreHeroTimerTextView);

        // Details Form
        choreIsActiveSwitch = findViewById(R.id.choreDetails_choreIsActiveSwitch);
        choreNameEditText = findViewById(R.id.choreDetails_choreNameEditText);
        choreEndAtLayout = findViewById(R.id.choreDetails_choreEndAtLayout);
        choreEndAtEditText = findViewById(R.id.choreDetails_choreEndAtEditText);
        choreIsRepeatSwitch = findViewById(R.id.choreDetails_choreIsRepeatSwitch);
        choreRepeatDaysLayout = findViewById(R.id.choreDetails_choreRepeatDaysLayout);
        choreRepeatDaysEditText = findViewById(R.id.choreDetails_choreRepeatDaysEditText);
        //choreIsAlertSwitch = findViewById(R.id.choreDetails_choreIsAlertSwitch);

        // Bind FABs
        saveChoreFAB = findViewById(R.id.choreDetails_save);
        deleteChoreFAB = findViewById(R.id.choreDetails_delete);


        // Intent
        Intent intent = getIntent();
        intentChoreId = intent.getLongExtra("choreId", 0);

        // Repo
        repository = new Repository(getApplication());

        // Current chore
        currentChoreLiveData = repository.getChoreById(intentChoreId);
        currentChoreLiveData.observe(this, chore -> {
            if (chore == null) {
                chore = new Chore();
                chore.setCreatedAt(System.currentTimeMillis());
                chore.setStartAt(chore.getCreatedAt());
            }
            currentChore = chore;
            hydrateUI(chore);
        });

        // Listeners after UI hydration
        // Open date picker from icon or text field
        choreEndAtLayout.setEndIconOnClickListener(v -> showChoreEndDatePicker());
        choreEndAtEditText.setOnClickListener(v -> showChoreEndDatePicker());

        // Repeat counter listeners
        choreRepeatDaysLayout.setStartIconOnClickListener(v -> {
            if (!canEditRepeatDays()) return;

            int current = parseIntOrZero(choreRepeatDaysEditText.getText());
            if (current > 0) {
                choreRepeatDaysEditText.setText(String.valueOf(current - 1));
            }
        });
        choreRepeatDaysLayout.setEndIconOnClickListener(v -> {
            if (!canEditRepeatDays()) return;

            int current = parseIntOrZero(choreRepeatDaysEditText.getText());
            choreRepeatDaysEditText.setText(String.valueOf(current + 1));
        });


        // Switches
        // Disable fields if !isActive, disable repeatDays field if !isRepeat
        // TODO: Remove. Users can add inactive chores. Only gate isRepeat, repeatDays, and isAlert fields with the name and endAt date fields
        choreIsActiveSwitch.setOnCheckedChangeListener((button, isActive) -> {
            choreNameEditText.setEnabled(isActive);
            choreEndAtEditText.setEnabled(isActive);
            choreIsRepeatSwitch.setEnabled(isActive);
            choreRepeatDaysEditText.setEnabled(isActive);
            //choreIsAlertSwitch.setEnabled(isActive);

            if (!isActive) {
                choreRepeatDaysEditText.setEnabled(false);
            } else {
                choreRepeatDaysEditText.setEnabled(choreIsRepeatSwitch.isChecked());
            }
        });

        choreIsRepeatSwitch.setOnCheckedChangeListener((button, choreIsRepeat) -> {
            choreRepeatDaysEditText.setEnabled(choreIsRepeat);
        });

        choreNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                choreHeroNameTextView.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        saveChoreFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChoreDetails.this, "Save FAB tapped.", Toast.LENGTH_LONG).show();

                if (choreNameEditText.getText().toString().isEmpty() || choreEndAtEditText.getText().toString().isEmpty()) {
                    Toast.makeText(ChoreDetails.this, "Minimum chore needs name and due date.", Toast.LENGTH_LONG).show();

                }


                currentChore.setName(choreNameEditText.getText().toString().trim());
                currentChore.setCreatedAt(System.currentTimeMillis());

                ZoneId zone = ZoneId.systemDefault();

                Instant startAt = LocalDate.now(zone)
                        .atStartOfDay(zone)
                        .toInstant();
                currentChore.setStartAt(startAt.toEpochMilli()); // Midnight today

                LocalDate endDate = LocalDate.parse(
                        choreEndAtEditText.getText().toString().trim()
                );
                Instant endAt = endDate
                        .plusDays(1)
                        .atStartOfDay(zone)
                        .toInstant()
                        .minusMillis(1);

                currentChore.setEndAt(endAt.toEpochMilli()); // 23:59:59:999 of the end date

                currentChore.setRepeat(choreIsRepeatSwitch.isChecked());
                //currentChore.setAlert(choreIsAlertSwitch.isChecked());
                currentChore.setActive(choreIsActiveSwitch.isChecked());

                if (currentChore.getId() == 0) {
                    repository.insertChore(currentChore);
                    Toast.makeText(ChoreDetails.this, "Chore was successfully added.", Toast.LENGTH_LONG).show();

                } else {
                    repository.updateChore(currentChore);
                    Toast.makeText(ChoreDetails.this, "Chore was successfully updated.", Toast.LENGTH_LONG).show();

                }

                finish();

                //recalcHero(currentChore);
                //updateHeroForEndDateChange();
            }
        });

        deleteChoreFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentChore.getId() == 0) {
                    Toast.makeText(ChoreDetails.this, "Save chore first.", Toast.LENGTH_LONG).show();
                    return;

                }

                new AlertDialog.Builder(ChoreDetails.this).setTitle("Delete Chore").setMessage("Are you sure you want to delete this chore?").setPositiveButton("Delete", (dialog, which) -> {
                    repository.deleteChore(new Chore(currentChore.getId()));
                    Toast.makeText(ChoreDetails.this, currentChore.getName() + " was deleted", Toast.LENGTH_LONG).show();
                    finish();
                }).setNegativeButton("Cancel", null).show();

            }

        });


    }

    private void hydrateUI(Chore chore) {
        if (chore.getId() == 0) {

        }

        // Hero
        choreHeroNameTextView.setText(chore.getName());

        applyVisualState(chore);
        recalcHero(chore);

        Instant now = Instant.now();
        Instant end = Instant.ofEpochMilli(chore.getEndAt());
        Duration remaining = Duration.between(now, end);
        String formattedTRemaining = FTime.formatDuration(remaining.toMillis());
        choreHeroTimerTextView.setText(formattedTRemaining); // TODO: That something weird happening here with the time? idk

        // Name
        choreNameEditText.setText(chore.getName());
        choreNameEditText.setEnabled(chore.isActive());

        // End Date
        if (chore.getId() != 0 && chore.getEndAt() > 0) {
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            choreEndAtEditText.setText(
                    dateFormat.format(new Date(chore.getEndAt()))
            );
        } else {
            choreEndAtEditText.setText(""); // <-- empty for new chore
        }


        // isRepeat
        choreIsRepeatSwitch.setChecked(chore.isRepeat());
        choreIsRepeatSwitch.setEnabled(chore.isActive());

        // repeatDays
        choreRepeatDaysEditText.setText(String.valueOf(chore.getRepeatDays())); // int --> String
        choreRepeatDaysEditText.setEnabled(chore.isRepeat()); // now check isRepeat
        //choreRepeatDaysEditText.setEnabled(chore.isActive()); // check active

        // isAlert
        //choreIsAlertSwitch.setChecked(chore.isAlert());
        //choreIsAlertSwitch.setEnabled(chore.isActive());

        choreIsActiveSwitch.setChecked(chore.isActive());

        applyVisualState(chore);
        recalcHero(chore);
    }

    private int parseIntOrZero(Editable text) {
        try {
            return Integer.parseInt(text.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private void showChoreEndDatePicker() {
        MaterialDatePicker.Builder<Long> choreEndAtBuilder = MaterialDatePicker.Builder.datePicker();

        choreEndAtBuilder.setTitleText("Select Chore End Date");

        String dateText = choreEndAtEditText.getText().toString();
        if (!dateText.isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                Date parsedDate = dateFormat.parse(dateText);
                if (parsedDate != null) {
                    choreEndAtBuilder.setSelection(parsedDate.getTime());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        MaterialDatePicker<Long> datePicker = choreEndAtBuilder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            long endAt = normalizeToEndOfDay(selection);

            //long now = System.currentTimeMillis();
            //currentChore.setStartAt(now);     // 🔴 RESET START
            currentChore.setEndAt(endAt);

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            choreEndAtEditText.setText(df.format(new Date(endAt)));

            updateHeroForEndDateChange();
            recalcHero(currentChore);
        });


        datePicker.addOnNegativeButtonClickListener(dialog -> choreEndAtEditText.clearFocus());

        datePicker.addOnDismissListener(dialog -> choreEndAtEditText.clearFocus());

        datePicker.show(getSupportFragmentManager(), "CHORE_END_AT_PICKER");
    }

    @Override
    protected void onStart() {
        super.onStart();


        this.startHeroTicker();

    }

    @Override
    protected void onStop() {
        super.onStop();


        this.stopHeroTicker();

    }

    private boolean tickerRunning = false;

    private void startHeroTicker() {
        if (tickerRunning) return;

        tickerRunning = true;
        ticker = () -> {
            if (currentChore != null) {
                recalcHero(currentChore);
            }
            handler.postDelayed(ticker, TICK_INTERVAL.toMillis());
        };
        handler.post(ticker);
    }

    private void stopHeroTicker() {
        tickerRunning = false;
        handler.removeCallbacks(ticker);
    }

    private boolean canEditRepeatDays() {
        return choreIsActiveSwitch.isChecked()
                && choreIsRepeatSwitch.isChecked();
    }

    private long normalizeToEndOfDay(long millisUtc) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(millisUtc);

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);

        return cal.getTimeInMillis();
    }

    private void updateHeroForEndDateChange() {
        if (currentChore == null) return;
        recalcHero(currentChore);
    }

    private void recalcHero(Chore chore) {
        Instant now;
        Instant start;
        Instant end;
        Duration total;
        Duration elapsed;
        Duration remaining;

        // NEW chore only (unsaved)
        if (chore.getId() == 0 && choreEndAtEditText.getText().toString().isEmpty()) {
            choreHeroTimerTextView.setText("00:00:00:00");
            choreHeroProgressIndicator.setProgress(0);
            applyVisualState(chore);
            return;
        }

        // ----- PROGRESS -----
        now = Instant.now();
        start = Instant.ofEpochMilli(chore.getStartAt());
        end = Instant.ofEpochMilli(chore.getEndAt());

        total = Duration.between(start, end);
        elapsed = Duration.between(start, now);
        remaining = Duration.between(now, end);

        int progressPct;

        // ----- TIMER (allow negative) -----
        choreHeroTimerTextView.setText(
                FTime.formatDuration(remaining.toMillis())
        );

        if (total.isZero() || total.isNegative()) {
            progressPct = 100;

        } else {
            double ratio = (double) elapsed.toMillis() / total.toMillis();
            ratio = Math.max(0.0, Math.min(1.0, ratio));

            progressPct = (int) Math.round(ratio * 100);
            progressPct = Math.max(1, progressPct);
        }

        choreHeroProgressIndicator.setProgress(progressPct);

        applyVisualState(chore);
    }

    private boolean isOverdue(Chore chore) {
        if (chore == null) return false;
        if (chore.getId() == 0) return false;          // not saved
        if (!chore.isActive()) return false;           // inactive chores don't warn
        if (chore.getEndAt() <= 0) return false;       // no date yet

        return System.currentTimeMillis() > chore.getEndAt();
    }

    private void applyVisualState(Chore chore) {
        boolean overdue = isOverdue(chore);

        if (overdue) {
            applyErrorColors();
        } else {
            applyPrimaryColors();
        }
    }

    private void applyErrorColors() {
        int errorContainer = MaterialColors.getColor(
                choreDetailsLayout, com.google.android.material.R.attr.colorErrorContainer
        );
        int onErrorContainer = MaterialColors.getColor(
                choreDetailsLayout, com.google.android.material.R.attr.colorOnErrorContainer
        );


        //choreDetailsLayout.setBackgroundColor(errorContainer);
        //choreDetails_heroLayout.setBackgroundColor(errorContainer);

        choreHeroProgressIndicator.setIndicatorColor(onErrorContainer);


    }

    private void applyPrimaryColors() {
        int surface = MaterialColors.getColor(
                choreDetailsLayout, com.google.android.material.R.attr.colorSurface
        );
        int primaryContainer = MaterialColors.getColor(choreDetailsLayout, com.google.android.material.R.attr.colorPrimaryContainer);
        int primary = MaterialColors.getColor(
                choreDetailsLayout, androidx.appcompat.R.attr.colorPrimary
        );


        choreDetailsLayout.setBackgroundColor(surface);
        //choreDetails_heroLayout.setBackgroundColor(primaryContainer);
        choreHeroProgressIndicator.setIndicatorColor(primary);


    }


}
