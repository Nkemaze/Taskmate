package com.bless.task.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.bless.task.data.Classroom;
import com.bless.task.repository.ClassroomRepository;

import java.util.List;

public class ClassroomViewModel extends AndroidViewModel {

    private final ClassroomRepository repository;
    private final LiveData<List<Classroom>> allClassrooms;

    public ClassroomViewModel(@NonNull Application application) {
        super(application);
        repository = new ClassroomRepository(application);
        allClassrooms = repository.getAllClassrooms();
    }

    public LiveData<List<Classroom>> getAllClassrooms() {
        return allClassrooms;
    }

    public void insert(Classroom classroom) {
        repository.insert(classroom);
    }

    public void update(Classroom classroom) {
        repository.update(classroom);
    }

    public void delete(Classroom classroom) {
        repository.delete(classroom);
    }
}
