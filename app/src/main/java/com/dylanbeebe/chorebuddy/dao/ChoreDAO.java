package com.dylanbeebe.chorebuddy.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.dylanbeebe.chorebuddy.entities.Chore;

import java.util.List;

@Dao
public interface ChoreDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Chore chore);

    @Update
    void update(Chore chore);

    @Delete
    void delete(Chore chore);

    @Query("SELECT * FROM chores ORDER BY id ASC")
    LiveData<List<Chore>> getAllChores(); // Required LiveData for asynchronous operations

    @Query("SELECT * FROM chores WHERE id = :choreId")
    LiveData<Chore> getChoreById(long choreId);
}
