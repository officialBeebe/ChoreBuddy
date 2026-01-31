package com.dylanbeebe.chorebuddy.database;

import android.app.Application;

import com.dylanbeebe.chorebuddy.dao.ChoreDAO;
import com.dylanbeebe.chorebuddy.dao.CompletedChoreDAO;
import com.dylanbeebe.chorebuddy.entities.Chore;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dylanbeebe.chorebuddy.BuildConfig;
import com.dylanbeebe.chorebuddy.entities.CompletedChore;

public class Repository {
    private ChoreDAO mChoreDAO;
    private CompletedChoreDAO mCompletedChoreDAO;

    private long mInsertedChoreId;
    private long mInsertedCompletedChoreId;

    // Select singles
    private Chore mSelectedChore;
    private CompletedChore mSelectedCompletedChore;

    // Select multiples
    private List<Chore> mAllChores;
    private List<CompletedChore> mAllCompletedChores;

    static final ExecutorService executor = Executors.newFixedThreadPool(BuildConfig.DB_THREADS);

    public Repository(Application application) {
        ChoreBuddyDatabaseBuilder db = ChoreBuddyDatabaseBuilder.getINSTANCE(application);
        mChoreDAO = db.choreDAO();
        mCompletedChoreDAO = db.completedChoreDAO();
    }

    // Executor perform database motions

    // Chores
    public List<Chore> getmAllChores() {
        executor.execute(() -> {
            mAllChores = mChoreDAO.getAllChores();
        });
        return mAllChores;
    }

    public long insert(Chore chore) {
        executor.execute(() -> {
            mInsertedChoreId = mChoreDAO.insert(chore);
        });
        return mInsertedChoreId;
    }

    public void update(Chore chore) {
        executor.execute(() -> {
            mChoreDAO.update(chore);
        });
    }

    public void delete(Chore chore) {
        executor.execute(() -> {
            mChoreDAO.delete(chore);
        });
    }

    public Chore getmSelectedChore(long choreId) {
        executor.execute(() -> {
            mSelectedChore = mChoreDAO.getChoreById(choreId);
        });

        return mSelectedChore;
    }

    // Completed Chores
    public List<CompletedChore> getmAllAssociatedCompletedChores(long choreId) {
        executor.execute(() -> {
            mAllCompletedChores = mCompletedChoreDAO.getAllCompletedChores(choreId);
        });
        return mAllCompletedChores;
    }

    public long insert(CompletedChore completedChore) {
        executor.execute(() -> {
            mInsertedChoreId = mCompletedChoreDAO.insert(completedChore);
        });
        return mInsertedChoreId;
    }

    public void update(CompletedChore completedChore) {
        executor.execute(() -> {
            mCompletedChoreDAO.update(completedChore);
        });
    }

    public void delete(CompletedChore completedChore) {
        executor.execute(() -> {
            mCompletedChoreDAO.delete(completedChore);
        });
    }

    public CompletedChore getmSelectedCompletedChore(long completedChoreId) {
        executor.execute(() -> {
            mSelectedCompletedChore = mCompletedChoreDAO.getCompletedChoreById(completedChoreId);
        });

        return mSelectedCompletedChore;
    }

}
