package com.dylanbeebe.chorebuddy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.dylanbeebe.chorebuddy.dao.ChoreDAO;
import com.dylanbeebe.chorebuddy.database.ChoreBuddyDatabaseBuilder;
import com.dylanbeebe.chorebuddy.entities.Chore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class SimpleEntityReadWriteTest {

    private ChoreDAO choreDAO;
    private ChoreBuddyDatabaseBuilder db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, ChoreBuddyDatabaseBuilder.class).build();
        choreDAO = db.getChoreDAO();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void writeChoreAndReadInChore() throws Exception {
        // Init new Chore
        Chore test_chore_1 = new Chore();

        // Chore Name
        test_chore_1.setName("Wash the dog");

        // Chore End At Millis Since Epoch Timestamp
        long one_day_in_future_millis_since_epoch = System.currentTimeMillis()
                + (24L * 60L * 60L * 1000L);
        test_chore_1.setEndAt(one_day_in_future_millis_since_epoch);

        // Assertions
        long test_chore_1_insert_id = choreDAO.insert(test_chore_1);
        assertTrue(test_chore_1_insert_id > 0);

        Chore read = choreDAO.getChoreByIdSync(test_chore_1_insert_id);
        assertNotNull(read);

        assertEquals("Wash the dog", read.getName());
        assertEquals(one_day_in_future_millis_since_epoch, read.getEndAt());

        // Example Failure Assertions
        //assertEquals("Wash the dogg", read.getName()); // typo in name
        //assertEquals(System.currentTimeMillis(), read.getEndAt()); // Current system time

    }
}

// Android developer documentation: JUnit Test for Room entities
// https://developer.android.com/training/data-storage/room/testing-db#java