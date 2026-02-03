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
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class ChoreAdapter extends RecyclerView.Adapter<ChoreAdapter.ChoreViewHolder> {
    private static final long TICK_MS = 1000;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable tickRunnable = new Runnable() {
        @Override
        public void run() {
            if (chores != null && !chores.isEmpty()) {
                notifyItemRangeChanged(0, chores.size(), "TICK");
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

    private List<Chore> chores;
    private Context context;
    private LayoutInflater inflater;

    public ChoreAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public class ChoreViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView choreListItem_titleTextView;
        private final MaterialTextView choreListItem_remainingTimeTextView;
        private final CircularProgressIndicator choreListItem_circularProgressIndicator;

        private ChoreViewHolder(@NonNull View itemView) {
            super(itemView);
            choreListItem_titleTextView = itemView.findViewById(R.id.choreListItem_titleTextView);
            choreListItem_remainingTimeTextView = itemView.findViewById(R.id.choreListItem_remainingTimeTextView);
            choreListItem_circularProgressIndicator = itemView.findViewById(R.id.choreListItem_circularProgressIndicator);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;

                    Chore current = chores.get(position); // not final?


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
        Chore current = chores.get(position);

        holder.choreListItem_titleTextView.setText(current.getName());

        holder.choreListItem_circularProgressIndicator.setProgress(current.getProgressInt());

        long remainingMillis = Math.max(
                0,
                current.getEndAt() - System.currentTimeMillis()
        );

        holder.choreListItem_remainingTimeTextView
                .setText(FTime.formatDuration(remainingMillis));
    }


    @Override
    public int getItemCount() {
        return chores == null ? 0 : chores.size();
    }

    public void setChores(List<Chore> chores) {
        this.chores = chores;
        notifyDataSetChanged();
    }

}
