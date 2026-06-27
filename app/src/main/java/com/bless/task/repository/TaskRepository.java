package com.bless.task.repository;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import com.bless.task.data.Task;
import com.bless.task.data.TaskDao;
import com.bless.task.data.TaskDatabase;
import com.bless.task.notifications.NotificationScheduler;

import java.util.List;

/**
 * Repository class to abstract access to multiple data sources.
 */
public class TaskRepository {

    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTasks;
    private final Context context;

    public TaskRepository(Application application) {
        this.context = application.getApplicationContext();
        TaskDatabase db = TaskDatabase.getDatabase(application);
        taskDao = db.taskDao();
        allTasks = taskDao.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public void insert(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            long id = taskDao.insert(task);
            task.setId((int) id);
            NotificationScheduler.scheduleReminders(context, task);
        });
    }

    public void update(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.update(task);
            NotificationScheduler.scheduleReminders(context, task);
        });
    }

    public void delete(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            NotificationScheduler.cancelReminders(context, task);
            taskDao.delete(task);
        });
    }
}
