package com.dylanbeebe.chorebuddy.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.dylanbeebe.chorebuddy.dao.ChoreDAO;
import com.dylanbeebe.chorebuddy.entities.Chore;

@Database(entities = {Chore.class}, version = 0, exportSchema = false)
public abstract class ChoreBuddyDatabaseBuilder extends RoomDatabase {
    public abstract ChoreDAO choreDAO();

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
