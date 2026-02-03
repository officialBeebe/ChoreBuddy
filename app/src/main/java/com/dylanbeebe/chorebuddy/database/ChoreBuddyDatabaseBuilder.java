package com.dylanbeebe.chorebuddy.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.dylanbeebe.chorebuddy.dao.ChoreDAO;
import com.dylanbeebe.chorebuddy.dao.CompletedChoreDAO;
import com.dylanbeebe.chorebuddy.entities.Chore;
import com.dylanbeebe.chorebuddy.entities.CompletedChore;

@Database(entities = {Chore.class, CompletedChore.class}, version = 6, exportSchema = false)
public abstract class ChoreBuddyDatabaseBuilder extends RoomDatabase {
    public abstract ChoreDAO choreDAO();
    public abstract CompletedChoreDAO completedChoreDAO();

    private static volatile ChoreBuddyDatabaseBuilder INSTANCE;

    public static ChoreBuddyDatabaseBuilder getINSTANCE(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ChoreBuddyDatabaseBuilder.class, "ChoreBuddyDatabase.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
