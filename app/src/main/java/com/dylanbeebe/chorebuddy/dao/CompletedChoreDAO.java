package com.dylanbeebe.chorebuddy.dao;

import androidx.room.Insert;
import androidx.room.Query;

import com.dylanbeebe.chorebuddy.entities.CompletedChore;

import java.util.List;

public interface CompletedChoreDAO {

    @Insert
    long insert(CompletedChore completedChore);

    @Query("SELECT * FROM completed_chores WHERE associatedChoreId = :choreId ORDER BY completedAt DESC;")
    List<CompletedChore> getAllChoreCompletions(long choreId);

    @Query("SELECT * FROM completed_chores WHERE completedAt BETWEEN :rangeStartAt AND :rangeEndAt ORDER BY completedAt ASC;")
    List<CompletedChore> getAllChoreCompletionsInRange(long rangeStartAt, long rangeEndAt);

    @Query("SELECT COUNT(*) FROM completed_chores WHERE associatedChoreId = :choreId;")
    int getChoreCompletionCount(long choreId);

    @Query("SELECT MAX(completedAt) FROM completed_chores WHERE associatedChoreId = :choreId;")
    Long getChoreLastCompletedAt(long choreId);
}
