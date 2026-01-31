package com.dylanbeebe.chorebuddy.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Represents a single historical completion of a {@link Chore}.
 *
 * <p>This entity is append-only. Each row records one completed occurrence.</p>
 */
@Entity(
        tableName = "completed_chores",
        foreignKeys = @ForeignKey(
                entity = Chore.class,
                parentColumns = "id",
                childColumns = "associatedChoreId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {
                @Index("associatedChoreId")
        }
)
public class CompletedChore {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long completedAt;
    public long associatedChoreId;

    /**
     * Creates a completion record for a {@link Chore}.
     *
     * @param completedAt           completion timestamp (epoch millis)
     * @param associatedChoreId     associated chore ID
     */
    public CompletedChore(long completedAt, long associatedChoreId) {
        this.completedAt = completedAt;
        this.associatedChoreId = associatedChoreId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(long completedAt) {
        this.completedAt = completedAt;
    }

    public long getAssociatedChoreId() {
        return associatedChoreId;
    }

    public void setAssociatedChoreId(long associatedChoreId) {
        this.associatedChoreId = associatedChoreId;
    }
}
