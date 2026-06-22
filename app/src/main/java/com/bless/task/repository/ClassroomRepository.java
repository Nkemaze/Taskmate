package com.bless.task.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.bless.task.data.Classroom;
import com.bless.task.data.ClassroomDao;
import com.bless.task.data.TaskDatabase;

import java.util.List;

public class ClassroomRepository {

    private final ClassroomDao classroomDao;
    private final LiveData<List<Classroom>> allClassrooms;

    public ClassroomRepository(Application application) {
        TaskDatabase db = TaskDatabase.getDatabase(application);
        classroomDao = db.classroomDao();
        allClassrooms = classroomDao.getAllClassrooms();
    }

    public LiveData<List<Classroom>> getAllClassrooms() {
        return allClassrooms;
    }

    public void insert(Classroom classroom) {
        TaskDatabase.databaseWriteExecutor.execute(() -> classroomDao.insert(classroom));
    }

    public void update(Classroom classroom) {
        TaskDatabase.databaseWriteExecutor.execute(() -> classroomDao.update(classroom));
    }

    public void delete(Classroom classroom) {
        TaskDatabase.databaseWriteExecutor.execute(() -> classroomDao.delete(classroom));
    }
}
