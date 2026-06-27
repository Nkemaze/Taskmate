package com.bless.task.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocalAlertDao {
    @Insert
    void insert(LocalAlert alert);

    @Query("SELECT * FROM local_alerts ORDER BY id DESC")
    LiveData<List<LocalAlert>> getAllAlerts();

    @Query("DELETE FROM local_alerts")
    void deleteAll();
}
