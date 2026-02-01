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

    private String name;

    // Stored as epoch millis; Room supports long natively
    private long createdAt;

    // Start of chore lifecycle (epoch millis)
    private long startAt;

    // Scheduled completion time (epoch millis)
    private long endAt;

    // Repetition configuration
    private boolean isRepeat;
    private int repeatDays;

    // Optional user alert flag
    private boolean isAlert;

    // Soft-enable flag (allows deactivation without deletion)
    private boolean isActive;

    public Chore(long id, String name, long createdAt, long startAt, long endAt, boolean isRepeat, int repeatDays, boolean isAlert, boolean isActive) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.startAt = startAt;
        this.endAt = endAt;
        this.isRepeat = isRepeat;
        this.repeatDays = repeatDays;
        this.isAlert = isAlert;
        this.isActive = isActive;
    }

    //    /**
//     * Constructs a minimal {@code Chore}.
//     *
//     * <ul>
//     *     <li>Non-repeating</li>
//     *     <li>No alert</li>
//     *     <li>Active by default</li>
//     * </ul>
//     *
//     * <p>Note: {@code id} should be {@code 0} to allow Room auto-generation.</p>
//     */
//    public Chore(long id, String name, long createdAt, long startAt, long endAt) {
//        this(id, name, createdAt, startAt, endAt, false, 0, false, true);
//    }
//
//    /**
//     * Full constructor.
//     */
//    public Chore(long id, String name, long createdAt, long startAt, long endAt,
//                 boolean isRepeat, int repeatDays, boolean isAlert, boolean isActive) {
//
//        this.id = id;
//        this.name = name;
//        this.createdAt = createdAt;
//        this.startAt = startAt;
//        this.endAt = endAt;
//        this.isRepeat = isRepeat;
//        this.repeatDays = repeatDays;
//        this.isAlert = isAlert;
//        this.isActive = isActive;
//    }

    /**
     * Allegedly required by Room for reflective instantiation.
     */
    @Ignore
    public Chore() {}

    /* ---- Accessors ---- */

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getStartAt() { return startAt; }
    public void setStartAt(long startAt) { this.startAt = startAt; }

    public long getEndAt() { return endAt; }
    public void setEndAt(long endAt) { this.endAt = endAt; }

    public boolean isRepeat() { return isRepeat; }
    public void setRepeat(boolean repeat) { isRepeat = repeat; }

    public int getRepeatDays() { return repeatDays; }
    public void setRepeatDays(int repeatDays) { this.repeatDays = repeatDays; }

    public boolean isAlert() { return isAlert; }
    public void setAlert(boolean alert) { isAlert = alert; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
