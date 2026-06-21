package com.bless.task.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.bless.task.data.Task;
import com.bless.task.data.TaskDao;
import com.bless.task.data.TaskDatabase;

import java.util.List;

/**
 * Repository class to abstract access to multiple data sources.
 */
public class TaskRepository {

    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTasks;

    public TaskRepository(Application application) {
        TaskDatabase db = TaskDatabase.getDatabase(application);
        taskDao = db.taskDao();
        allTasks = taskDao.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public void insert(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.insert(task));
    }

    public void update(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.delete(task));
    }
}
