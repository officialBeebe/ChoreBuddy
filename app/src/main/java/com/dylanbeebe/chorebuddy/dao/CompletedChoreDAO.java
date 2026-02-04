package com.dylanbeebe.chorebuddy.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.dylanbeebe.chorebuddy.entities.CompletedChore;

import java.util.List;

@Dao
public interface CompletedChoreDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(CompletedChore completedChore);

    @Update
    void update(CompletedChore completedChore);

    @Delete
    void delete(CompletedChore completedChore);

    @Query("SELECT * FROM completed_chores " + "WHERE associatedChoreId = :associatedChoreId " + "ORDER BY completedAt DESC")
    LiveData<List<CompletedChore>> getAllCompletedChores(long associatedChoreId);

    @Query("SELECT * FROM completed_chores " + "WHERE id = :completedChoreId")
    LiveData<CompletedChore> getCompletedChoreById(long completedChoreId);

    @Query("SELECT * FROM completed_chores " + "WHERE completedAt BETWEEN :rangeStartAt AND :rangeEndAt " + "ORDER BY completedAt ASC")
    LiveData<List<CompletedChore>> getAllCompletedChoresInRange(long rangeStartAt, long rangeEndAt);

    @Query("SELECT COUNT(*) FROM completed_chores " + "WHERE associatedChoreId = :associatedChoreId")
    LiveData<Integer> getCompletedChoreCount(long associatedChoreId);

    @Query("SELECT MAX(completedAt) FROM completed_chores " + "WHERE associatedChoreId = :associatedChoreId")
    LiveData<Long> getCompletedChoreLastCompletedAt(long associatedChoreId);

    @Query("SELECT * FROM completed_chores WHERE associatedChoreId = :associatedChoreId ORDER BY completedAt DESC")
    List<CompletedChore> getCompletedChoresSync(long associatedChoreId);
}
