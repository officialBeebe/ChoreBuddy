package com.dylanbeebe.chorebuddy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.dylanbeebe.chorebuddy.dao.ChoreDAO;
import com.dylanbeebe.chorebuddy.database.ChoreBuddyDatabaseBuilder;
import com.dylanbeebe.chorebuddy.entities.Chore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;

@RunWith(AndroidJUnit4.class)
public class SimpleEntityReadWriteTest {

    private static final String TAG = "SimpleEntityRWTest";

    @Rule
    public TestName testName = new TestName();

    private ChoreDAO choreDAO;
    private ChoreBuddyDatabaseBuilder db;

    @Before
    public void createDb() {
        Log.i(TAG, "START " + testName.getMethodName());

        Context context = ApplicationProvider.getApplicationContext();

        // allowMainThreadQueries() keeps the test simple/deterministic for DAO calls
        db = Room.inMemoryDatabaseBuilder(context, ChoreBuddyDatabaseBuilder.class)
                .allowMainThreadQueries()
                .build();

        choreDAO = db.getChoreDAO();
    }

    @After
    public void closeDb() throws IOException {
        if (db != null) db.close();
        Log.i(TAG, "END " + testName.getMethodName());
    }

    @Test
    public void writeChoreAndReadInChore() {
        // Arrange (expected values)
        final String expectedName = "Wash the dog";
        final long expectedEndAt = System.currentTimeMillis() + (24L * 60L * 60L * 1000L);

        // Arrange (entity)
        Chore chore = new Chore();
        chore.setName(expectedName);
        chore.setEndAt(expectedEndAt);

        // Act (insert)
        long insertId = choreDAO.insert(chore);
        Log.i(TAG, "Inserted chore. id=" + insertId
                + " name=\"" + expectedName + "\""
                + " endAt=" + expectedEndAt + " (" + fmtMillis(expectedEndAt) + ")");

        // Assert (insert id)
        assertTrue("Insert should return id > 0, but was " + insertId, insertId > 0);

        // Act (read)
        Chore read = choreDAO.getChoreByIdSync(insertId);
        assertNotNull("DAO returned null for id=" + insertId, read);

        Log.i(TAG, "Read chore. id=" + insertId
                + " name=\"" + read.getName() + "\""
                + " endAt=" + read.getEndAt() + " (" + fmtMillis(read.getEndAt()) + ")");

        // Assert (field equality)
        assertEquals("Chore.name mismatch after persistence", expectedName, read.getName());
        assertEquals(
                "Chore.endAt mismatch after persistence. expected="
                        + expectedEndAt + " (" + fmtMillis(expectedEndAt) + ")"
                        + " actual=" + read.getEndAt() + " (" + fmtMillis(read.getEndAt()) + ")",
                expectedEndAt,
                read.getEndAt()
        );
    }

    private static String fmtMillis(long ms) {
        return Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toString();
    }
}

// Android developer documentation: JUnit Test for Room entities
// https://developer.android.com/training/data-storage/room/testing-db#java
