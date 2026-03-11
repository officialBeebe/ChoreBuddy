package com.dylanbeebe.chorebuddy.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dylanbeebe.chorebuddy.R;
import com.dylanbeebe.chorebuddy.entities.Chore;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ChoreAdapter extends RecyclerView.Adapter<ChoreAdapter.ChoreViewHolder> {
    private static final long TICK_MS = 1000;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final List<Chore> chores = new ArrayList<>();
    private final List<Chore> visibleChores = new ArrayList<>();

    private Context context;
    private LayoutInflater inflater;
    private final OnChoreSwipeListener swipeListener;

    // Swipe listener interface
    public interface OnChoreSwipeListener {
        void onSwipeLeft(int position);

        void onSwipeRight(int position);
    }

    public ChoreAdapter(@NonNull Context context, @NonNull OnChoreSwipeListener swipeListener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.swipeListener = swipeListener;
    }

    public class ChoreViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView choreListItem;
        private final MaterialTextView choreListItem_titleTextView;
        private final MaterialTextView choreListItem_remainingTimeTextView;
        private final CircularProgressIndicator choreListItem_circularProgressIndicator;

        private ChoreViewHolder(@NonNull View itemView) {
            super(itemView);
            choreListItem = itemView.findViewById(R.id.choreListItem);
            ;
            choreListItem_titleTextView = itemView.findViewById(R.id.choreListItem_titleTextView);
            choreListItem_remainingTimeTextView = itemView.findViewById(R.id.choreListItem_remainingTimeTextView);
            choreListItem_circularProgressIndicator = itemView.findViewById(R.id.choreListItem_circularProgressIndicator);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;

                    //Chore current = chores.get(position); // not final?
                    Chore current = visibleChores.get(position);



                    Intent intent = new Intent(context, ChoreDetails.class);
                    intent.putExtra("choreId", current.getId());
                    context.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public ChoreAdapter.ChoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.chore_list_item, parent, false);
        return new ChoreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChoreViewHolder holder, int position) {
        int surfaceColor = MaterialColors.getColor(
                holder.choreListItem,
                com.google.android.material.R.attr.colorSurface
        );

        int outlineColor = MaterialColors.getColor(
                holder.choreListItem,
                com.google.android.material.R.attr.colorOutline
        );

        holder.choreListItem.setCardBackgroundColor(surfaceColor);
        holder.choreListItem_circularProgressIndicator.setIndicatorColor(outlineColor);


        //Chore current = chores.get(position);
        Chore current = visibleChores.get(position);


        holder.choreListItem_titleTextView.setText(current.getName());

        Instant now = Instant.now();
        Instant start = Instant.ofEpochMilli(current.getStartAt());
        Instant end = Instant.ofEpochMilli(current.getEndAt());

        Duration total = Duration.between(start, end);
        Duration elapsed = Duration.between(start, now);
        Duration remaining = Duration.between(now, end);
        int progressPct;

        // TODO: progressPct must be a number between 1 and 100

        if (total.isZero() || total.isNegative()) {
            progressPct = 100;

            int colorErrorContainer = MaterialColors.getColor(
                    holder.choreListItem,
                    com.google.android.material.R.attr.colorErrorContainer
            );

            int colorOnErrorContainer = MaterialColors.getColor(
                    holder.choreListItem,
                    com.google.android.material.R.attr.colorOnErrorContainer
            );

            holder.choreListItem.setCardBackgroundColor(colorErrorContainer);
            holder.choreListItem_circularProgressIndicator
                    .setIndicatorColor(colorOnErrorContainer);
        } else {
            double ratio = (double) elapsed.toMillis() / (double) total.toMillis();
            ratio = Math.max(0.0, Math.min(1.0, ratio));

            progressPct = (int) Math.round(ratio * 100);
            progressPct = Math.max(1, progressPct);
        }

        boolean isOverdue = total.isZero() || total.isNegative();



        if (isOverdue) {
            int errorContainer = MaterialColors.getColor(
                    holder.choreListItem,
                    com.google.android.material.R.attr.colorErrorContainer
            );

            int onErrorContainer = MaterialColors.getColor(
                    holder.choreListItem,
                    com.google.android.material.R.attr.colorOnErrorContainer
            );

            holder.choreListItem.setCardBackgroundColor(errorContainer);
            holder.choreListItem_circularProgressIndicator
                    .setIndicatorColor(onErrorContainer);

        } else if (current.isActive()) {
            int primaryContainer = MaterialColors.getColor(
                    holder.choreListItem,
                    com.google.android.material.R.attr.colorPrimaryContainer
            );

            int onPrimaryContainer = MaterialColors.getColor(
                    holder.choreListItem,
                    com.google.android.material.R.attr.colorOnPrimaryContainer
            );

            holder.choreListItem.setCardBackgroundColor(primaryContainer);
            holder.choreListItem_circularProgressIndicator
                    .setIndicatorColor(onPrimaryContainer);

        } else {
            // inactive → surface (already reset, but explicit for clarity)
            holder.choreListItem.setCardBackgroundColor(surfaceColor);
            holder.choreListItem_circularProgressIndicator
                    .setIndicatorColor(outlineColor);
        }

        if (current.isActive()) {
            holder.choreListItem_circularProgressIndicator.setProgress(progressPct);
            holder.choreListItem_remainingTimeTextView
                    .setText(FTime.formatDuration(remaining.toMillis()));
        } else {
            holder.choreListItem_circularProgressIndicator.setProgress(5);
            holder.choreListItem_remainingTimeTextView.setText("00:00:00:00");
        }


    }


    public void setChores(List<Chore> newChores) {
        this.chores.clear();
        visibleChores.clear();

        if (newChores != null) {
            this.chores.addAll(newChores);
            visibleChores.addAll(newChores);
        }

        notifyDataSetChanged();
    }


    public Chore getChoreAt(int position) {
        return visibleChores.get(position);
    }

    public List<Chore> getVisibleChores() {
        return visibleChores;
    }

    @Override
    public int getItemCount() {
        return visibleChores.size();
    }

    // for search
    public void filter(String query) {
        visibleChores.clear();

        if (query == null || query.trim().isEmpty()) {
            visibleChores.addAll(chores);
        } else {
            String lowerQuery = query.toLowerCase();

            for (Chore chore : chores) {
                if (chore.getName().toLowerCase().contains(lowerQuery)) {
                    visibleChores.add(chore);
                }
            }
        }

        notifyDataSetChanged();
    }


    // Swipe entry points
    public void onSwipeLeft(int position) {
        //Chore current = chores.get(position);

        // Prompt for chore deactivation, or deletion (make deactivation default)

        swipeListener.onSwipeLeft(position);
    }

    public void onSwipeRight(int position) {
        //Chore current = chores.get(position);

        // Set chore.startAt = System.currentTimeMillis()

        // If chore.isRepeat, set chore.endAt = chore.startAt + toMillis(chore.repeatDays)

        // Else, chore.isActive = false

        swipeListener.onSwipeRight(position);
    }

    // Ticker
    private final Runnable tickRunnable = new Runnable() {
        @Override
        public void run() {
//            if (chores != null && !chores.isEmpty()) {
//                notifyItemRangeChanged(0, chores.size(), "TICK");
//            }
            if (!visibleChores.isEmpty()) {
                notifyItemRangeChanged(0, visibleChores.size(), "TICK");
            }

            handler.postDelayed(this, TICK_MS);
        }
    };

    public void startTicker() {
        handler.removeCallbacks(tickRunnable);
        handler.post(tickRunnable);
    }

    public void stopTicker() {
        handler.removeCallbacks(tickRunnable);
    }
}
