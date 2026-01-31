package com.dylanbeebe.chorebuddy.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "chores")
public class Chore {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private long createdAt; // apparently Room handles long natively for timestamp
    private long startAt;
    private long endAt;
    private boolean isRepeat;
    private int repeatDays;
    private boolean isAlert;
    private boolean isActive; // this required some considerable thought



    /**
     * Constructs a minimal {@code Chore} instance.
     *
     * <p>Required inputs:</p>
     * <ul>
     *     <li>{@code name} and {@code endAt} are provided by the user</li>
     *     <li>{@code createdAt} and {@code startAt} are initialized to the
     *         current system time at insertion</li>
     * </ul>
     *
     * <p>Default values applied:</p>
     * <ul>
     *     <li>{@code isRepeat = false}</li>
     *     <li>{@code repeatDays = 0}</li>
     *     <li>{@code isAlert = false}</li>
     *     <li>{@code isActive = true}</li>
     * </ul>
     *
     * @param id        {@code 0} for auto-generation
     * @param name      chore name
     * @param createdAt creation timestamp (epoch millis)
     * @param startAt   scheduled start time (epoch millis)
     * @param endAt     scheduled end time (epoch millis)
     */
    public Chore(long id, String name, long createdAt, long startAt, long endAt) {

        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.startAt = startAt;
        this.endAt = endAt;

        this.isRepeat = false;
        this.repeatDays = 0;

        this.isAlert = false;

        this.isActive = true;
    }

    @Ignore
    public Chore() {}

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
        this.name = name;
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

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    public int getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(int repeatDays) {
        this.repeatDays = repeatDays;
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
}
