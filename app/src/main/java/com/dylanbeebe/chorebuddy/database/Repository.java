package com.dylanbeebe.chorebuddy.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.dylanbeebe.chorebuddy.dao.ChoreDAO;
import com.dylanbeebe.chorebuddy.dao.CompletedChoreDAO;
import com.dylanbeebe.chorebuddy.entities.Chore;
import com.dylanbeebe.chorebuddy.entities.CompletedChore;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {

    private final ChoreDAO choreDAO;
    private final CompletedChoreDAO completedChoreDAO;

    private static final ExecutorService executor =
            Executors.newFixedThreadPool(3); // or BuildConfig.DB_THREADS

    public Repository(Application application) {
        ChoreBuddyDatabaseBuilder db =
                ChoreBuddyDatabaseBuilder.getINSTANCE(application);

        choreDAO = db.choreDAO();
        completedChoreDAO = db.completedChoreDAO();
    }

    /* -------------------- Chores -------------------- */

    public LiveData<List<Chore>> getAllChores() {
        return choreDAO.getAllChores();
    }

    public LiveData<Chore> getChoreById(long choreId) {
        return choreDAO.getChoreById(choreId);
    }

    public void insertChore(Chore chore) {
        executor.execute(() -> choreDAO.insert(chore));
    }

    public void updateChore(Chore chore) {
        executor.execute(() -> choreDAO.update(chore));
    }

    public void deleteChore(Chore chore) {
        executor.execute(() -> choreDAO.delete(chore));
    }

    /* ---------------- Completed Chores ---------------- */

    public LiveData<List<CompletedChore>> getCompletedChoresForChore(long choreId) {
        return completedChoreDAO.getAllCompletedChores(choreId);
    }

    public LiveData<CompletedChore> getCompletedChoreById(long completedChoreId) {
        return completedChoreDAO.getCompletedChoreById(completedChoreId);
    }

    public void insertCompletedChore(CompletedChore completedChore) {
        executor.execute(() -> completedChoreDAO.insert(completedChore));
    }

    public void updateCompletedChore(CompletedChore completedChore) {
        executor.execute(() -> completedChoreDAO.update(completedChore));
    }

    public void deleteCompletedChore(CompletedChore completedChore) {
        executor.execute(() -> completedChoreDAO.delete(completedChore));
    }
}
