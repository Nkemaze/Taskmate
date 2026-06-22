package com.bless.task.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ClassroomDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Classroom classroom);

    @Update
    void update(Classroom classroom);

    @Delete
    void delete(Classroom classroom);

    @Query("DELETE FROM classrooms")
    void deleteAll();

    @Query("SELECT * FROM classrooms ORDER BY name ASC")
    LiveData<List<Classroom>> getAllClassrooms();

    @Query("SELECT * FROM classrooms WHERE code = :code LIMIT 1")
    Classroom getClassroomByCode(String code);
}
