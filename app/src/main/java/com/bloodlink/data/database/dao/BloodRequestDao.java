package com.bloodlink.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.bloodlink.data.database.entities.BloodRequest;
import java.util.List;

@Dao
public interface BloodRequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(BloodRequest request);

    @Update
    void update(BloodRequest request);

    @Query("SELECT * FROM blood_requests WHERE id = :id")
    BloodRequest getById(int id);

    @Query("SELECT * FROM blood_requests WHERE id = :id")
    LiveData<BloodRequest> observeById(int id);

    // Всички отворени заявки, сортирани по спешност и дата
    @Query("SELECT * FROM blood_requests WHERE status = 'OPEN' " +
           "ORDER BY CASE urgencyLevel WHEN 'CRITICAL' THEN 1 WHEN 'HIGH' THEN 2 ELSE 3 END, " +
           "createdAt DESC")
    LiveData<List<BloodRequest>> observeOpenRequests();

    // Търсене по кръвна група
    @Query("SELECT * FROM blood_requests WHERE status = 'OPEN' AND bloodType = :bloodType " +
           "ORDER BY CASE urgencyLevel WHEN 'CRITICAL' THEN 1 WHEN 'HIGH' THEN 2 ELSE 3 END, " +
           "createdAt DESC")
    LiveData<List<BloodRequest>> searchByBloodType(String bloodType);

    // Търсене по град
    @Query("SELECT * FROM blood_requests WHERE status = 'OPEN' " +
           "AND LOWER(city) LIKE '%' || LOWER(:city) || '%' " +
           "ORDER BY CASE urgencyLevel WHEN 'CRITICAL' THEN 1 WHEN 'HIGH' THEN 2 ELSE 3 END")
    LiveData<List<BloodRequest>> searchByCity(String city);

    // Заявките, публикувани от текущия потребител
    @Query("SELECT * FROM blood_requests WHERE requesterId = :userId ORDER BY createdAt DESC")
    LiveData<List<BloodRequest>> observeMyRequests(int userId);

    @Query("SELECT * FROM blood_requests WHERE status = 'OPEN' ORDER BY CASE urgencyLevel WHEN 'CRITICAL' THEN 1 WHEN 'HIGH' THEN 2 ELSE 3 END, createdAt DESC")
    List<BloodRequest> getAllOpenSync();

    @Query("UPDATE blood_requests SET status = :status WHERE id = :id")
    void updateStatus(int id, String status);
}
// Добавено за AR компас — синхронно четене без LiveData
