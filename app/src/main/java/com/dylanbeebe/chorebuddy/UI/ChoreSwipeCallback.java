package com.dylanbeebe.chorebuddy.UI;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.view.View;

import com.google.android.material.color.MaterialColors;
import com.dylanbeebe.chorebuddy.R;


public class ChoreSwipeCallback extends ItemTouchHelper.SimpleCallback {

    private final ChoreAdapter adapter;

    public ChoreSwipeCallback(ChoreAdapter adapter) {
        super(0, ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(
            @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder,
            @NonNull RecyclerView.ViewHolder target
    ) {
        return false; // drag & drop not supported
    }

    @Override
    public void onSwiped(
            @NonNull RecyclerView.ViewHolder viewHolder,
            int direction
    ) {
        int position = viewHolder.getBindingAdapterPosition();
        if (position == RecyclerView.NO_POSITION) return;

        if (direction == ItemTouchHelper.LEFT) {
            // TODO: revisit when can figure out how to keep from dismissing cards
            //adapter.onSwipeLeft(position);

        } else if (direction == ItemTouchHelper.RIGHT) {
            adapter.onSwipeRight(position);
        }
    }


}


