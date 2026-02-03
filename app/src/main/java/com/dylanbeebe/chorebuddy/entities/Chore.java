package com.dylanbeebe.chorebuddy.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Room entity representing a scheduled chore.
 *
 * <p>This class models both one-time and repeating chores using
 * epoch-millisecond timestamps and simple boolean flags.</p>
 */
@Entity(tableName = "chores")
public class Chore {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name = "";
    private long createdAt = System.currentTimeMillis(); // Stored as epoch millis; Room supports long natively
    private long startAt= System.currentTimeMillis(); // Start of chore lifecycle (epoch millis)
    private long endAt= System.currentTimeMillis(); // Scheduled completion time (epoch millis)
    private boolean isRepeat = false;
    private int repeatDays = 0;
    private boolean isAlert = false;
    private boolean isActive = true;

    public Chore(long id, String name, long createdAt, long startAt, long endAt, boolean isRepeat, int repeatDays, boolean isAlert, boolean isActive) {
        this.id = id;
        this.name = name != null ? name : "";
        this.createdAt = createdAt;
        this.startAt = startAt;
        this.endAt = endAt;
        this.isRepeat = isRepeat;
        this.repeatDays = isRepeat ? Math.max(0, repeatDays) : 0;
        this.isAlert = isAlert;
        this.isActive = isActive;
    }

    public Chore() {
    }

    /* ---- Accessors ---- */

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name : "";
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getStartAt() {
        return startAt;
    }

    public void setStartAt(long startAt) {
        this.startAt = startAt;
    }

    public long getEndAt() {
        return endAt;
    }

    public void setEndAt(long endAt) {
        this.endAt = endAt;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public int getRepeatDays() {
        return isRepeat ? repeatDays : 0;
    }

    public void setRepeat(boolean repeat) {
        this.isRepeat = repeat;
        if (!repeat) {
            this.repeatDays = 0;
        }
    }

    public void setRepeatDays(int repeatDays) {
        this.repeatDays = isRepeat ? Math.max(0, repeatDays) : 0;
    }


    public boolean isAlert() {
        return isAlert;
    }

    public void setAlert(boolean alert) {
        isAlert = alert;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getProgressInt() {
        long tCurrent = System.currentTimeMillis();
        long tStart = this.getStartAt();
        long tEnd = this.getEndAt();
        long tTotal = tEnd - tStart;
        long tElapsed = tCurrent - tStart;

        double tElapsedRatio = (double) tElapsed / tTotal;
        return (int) Math.floor(tElapsedRatio * 10000); // Figure out later why i have to x1000 instead of 100
    }

    public long getRemainingTime() {
        long tCurrent = System.currentTimeMillis();
        long tElapsed = tCurrent - this.getStartAt();
        return this.getEndAt() - tElapsed;
    }
}
