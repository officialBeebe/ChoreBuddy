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
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@RunWith(AndroidJUnit4.class)
public class SimpleEntityReadWriteTest {

    private static final String TAG = "SimpleEntityRWTest";

    private ChoreDAO choreDAO;
    private ChoreBuddyDatabaseBuilder db;

    @Before
    public void createDb() {
        Log.i(TAG, "START writeChoreAndReadInChore");

        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, ChoreBuddyDatabaseBuilder.class).build();
        choreDAO = db.getChoreDAO();
    }

    @After
    public void closeDb() throws IOException {
        if (db != null) db.close();
        Log.i(TAG, "END writeChoreAndReadInChore");
    }

    @Test
    public void writeChoreAndReadInChore() {
        // Arrange
        final String expectedName = "Wash the dog";
        final long expectedEndAt = System.currentTimeMillis() + (24L * 60L * 60L * 1000L);

        Chore chore = new Chore();
        chore.setName(expectedName);
        chore.setEndAt(expectedEndAt);

        Log.i(TAG, "Arrange: expectedName=\"" + expectedName + "\"");
        Log.i(TAG, "Arrange: expectedEndAt=" + expectedEndAt + " (" + fmtMillis(expectedEndAt) + ")");

        // Act: insert
        long insertId = choreDAO.insert(chore);
        Log.i(TAG, "Insert: returnedId=" + insertId);

        // Assert: insert ID
        assertTrue("Insert should return id > 0, but was " + insertId, insertId > 0);

        // Act: read
        Chore read = choreDAO.getChoreByIdSync(insertId);
        assertNotNull("Read returned null for id=" + insertId, read);

        Log.i(TAG, "Read: name=\"" + read.getName() + "\"");
        Log.i(TAG, "Read: endAt=" + read.getEndAt() + " (" + fmtMillis(read.getEndAt()) + ")");

        // Assert: persisted values
        assertEquals(
                "Chore.name mismatch after persistence. expected=\"" + expectedName + "\" actual=\"" + read.getName() + "\"",
                expectedName,
                read.getName()
        );

        assertEquals(
                "Chore.endAt mismatch after persistence. expected="
                        + expectedEndAt + " (" + fmtMillis(expectedEndAt) + ")"
                        + " actual=" + read.getEndAt() + " (" + fmtMillis(read.getEndAt()) + ")",
                expectedEndAt,
                read.getEndAt()
        );

        // ------------------------------------------------------------
        // OPTIONAL FAILURE ASSERTIONS (left here commented on purpose)
        // Uncomment ONE of the following to intentionally generate a failing
        // test run for screenshot/output evidence, then re-comment.
        // ------------------------------------------------------------

        // 1) Name typo (intentional failure)
//         assertEquals(
//                 "Intentional failure (name typo). expected=\"Wash the dogg\" actual=\"" + read.getName() + "\"",
//                 "Wash the dogg",
//                 read.getName()
//         );

        // 2) Wrong endAt (intentional failure)
//         long wrongEndAt = System.currentTimeMillis();
//         assertEquals(
//                 "Intentional failure (endAt mismatch). expected="
//                         + wrongEndAt + " (" + fmtMillis(wrongEndAt) + ")"
//                         + " actual=" + read.getEndAt() + " (" + fmtMillis(read.getEndAt()) + ")",
//                 wrongEndAt,
//                 read.getEndAt()
//         );
    }

    private static String fmtMillis(long ms) {
        // Human-readable timestamp for logs/assertion messages
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.US).format(new Date(ms));
    }
}

// Android developer documentation: JUnit Test for Room entities
// https://developer.android.com/training/data-storage/room/testing-db#java
