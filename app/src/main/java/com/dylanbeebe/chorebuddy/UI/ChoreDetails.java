package com.dylanbeebe.chorebuddy.UI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.lifecycle.LiveData;

import com.dylanbeebe.chorebuddy.R;
import com.dylanbeebe.chorebuddy.database.Repository;
import com.dylanbeebe.chorebuddy.entities.Chore;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    // Data

    private Repository repository;

    // Ticker
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable ticker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setEdgeToEdgeContentView(R.layout.activity_chore_details);

        // Hero
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
        choreIsAlertSwitch = findViewById(R.id.choreDetails_choreIsAlertSwitch);


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
        choreIsActiveSwitch.setOnCheckedChangeListener((button, isActive) -> {
            choreNameEditText.setEnabled(isActive);
            choreEndAtEditText.setEnabled(isActive);
            choreIsRepeatSwitch.setEnabled(isActive);
            choreRepeatDaysEditText.setEnabled(isActive);
            choreIsAlertSwitch.setEnabled(isActive);

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                choreHeroNameTextView.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


    }

    private void hydrateUI(Chore chore) {
        if (chore.getId() == 0) {

        }

        // Hero
        choreHeroNameTextView.setText(chore.getName());

        // long now = System.currentTimeMillis();

        // long start = chore.getStartAt();
        // long end = chore.getEndAt();

        // long duration = Math.max(1, end - start);
        // long remaining = Math.max(0, end - now);

        // long tCurrent = System.currentTimeMillis();
        // long tStart = chore.getStartAt();
        // long tEnd = chore.getEndAt();
        // long tTotal = tEnd - tStart;
        // long tElapsed = tCurrent - tStart;

        // double tElapsedRatio = (double) tElapsed / tTotal;
        // int progressPct = (int) Math.floor(tElapsedRatio * 10000); // Figure out later why i have to x1000 instead of 100

        choreHeroProgressIndicator.setProgress(chore.getProgressInt());

        long tRemaining = chore.getRemainingTime();
        String formattedTRemaining = FTime.formatDuration(tRemaining);
        choreHeroTimerTextView.setText(formattedTRemaining); // TODO: That something weird happening here with the time? idk

        // Name
        choreNameEditText.setText(chore.getName());
        choreNameEditText.setEnabled(chore.isActive());

        // End Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // long millis to date format string
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        choreEndAtEditText.setText(dateFormat.format(new Date(chore.getEndAt())));
        choreEndAtEditText.setEnabled(chore.isActive());

        // isRepeat
        choreIsRepeatSwitch.setChecked(chore.isRepeat());
        choreIsRepeatSwitch.setEnabled(chore.isActive());

        // repeatDays
        choreRepeatDaysEditText.setText(String.valueOf(chore.getRepeatDays())); // int --> String
        choreRepeatDaysEditText.setEnabled(chore.isRepeat()); // now check isRepeat
        choreRepeatDaysEditText.setEnabled(chore.isActive()); // check active

        // isAlert
        choreIsAlertSwitch.setChecked(chore.isAlert());
        choreIsAlertSwitch.setEnabled(chore.isActive());

        choreIsActiveSwitch.setChecked(chore.isActive());


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

            currentChore.setEndAt(endAt);

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            choreEndAtEditText.setText(df.format(new Date(endAt)));

            updateHeroForEndDateChange();
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
                updateHeroTimer(currentChore);
            }
            handler.postDelayed(ticker, 1000);
        };
        handler.post(ticker);
    }

    private void stopHeroTicker() {
        tickerRunning = false;
        handler.removeCallbacks(ticker);
    }

    private void updateHeroTimer(Chore chore) {
        long remaining = Math.max(
                0,
                chore.getEndAt() - System.currentTimeMillis()
        );

        choreHeroTimerTextView.setText(
                FTime.formatDuration(remaining)
        );
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

        long now = System.currentTimeMillis();
        long endAt = currentChore.getEndAt();

        if (endAt > now) {
            startHeroTicker();
        } else {
            stopHeroTicker();
            choreHeroTimerTextView.setText("00:00:00");
            choreHeroProgressIndicator.setProgress(0);
        }
    }


}
