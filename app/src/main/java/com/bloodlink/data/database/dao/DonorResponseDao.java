package com.bloodlink.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.bloodlink.data.database.entities.DonorResponse;
import java.util.List;

@Dao
public interface DonorResponseDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(DonorResponse response);

    @Update
    void update(DonorResponse response);

    @Query("SELECT * FROM donor_responses WHERE id = :id")
    DonorResponse getById(int id);

    // Всички отклики за дадена заявка (за публикувалия заявката)
    @Query("SELECT * FROM donor_responses WHERE requestId = :requestId ORDER BY respondedAt ASC")
    LiveData<List<DonorResponse>> observeResponsesForRequest(int requestId);

    // Откликите на текущия донор — сортирани по дата
    @Query("SELECT * FROM donor_responses WHERE donorId = :donorId ORDER BY respondedAt DESC")
    LiveData<List<DonorResponse>> observeMyResponses(int donorId);

    // Проверка дали донорът вече е откликнал на тази заявка
    @Query("SELECT COUNT(*) FROM donor_responses WHERE requestId = :reqId AND donorId = :donorId " +
           "AND status != 'REJECTED'")
    int hasResponded(int reqId, int donorId);

    @Query("UPDATE donor_responses SET status = :status WHERE id = :id")
    void updateStatus(int id, String status);

    // Брой на всички отклики на потребителя (за статистика в профила)
    @Query("SELECT COUNT(*) FROM donor_responses WHERE donorId = :userId")
    LiveData<Integer> getResponseCountForUser(int userId);

    // Брой завършени дарявания (status = DONATED) на потребителя
    @Query("SELECT COUNT(*) FROM donor_responses WHERE donorId = :userId AND status = 'DONATED'")
    LiveData<Integer> getCompletedDonationsForUser(int userId);
}
