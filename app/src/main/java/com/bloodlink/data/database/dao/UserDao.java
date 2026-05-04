package com.bloodlink.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.bloodlink.data.database.entities.User;
import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM users WHERE id = :id")
    User getById(int id);

    @Query("SELECT * FROM users WHERE id = :id")
    LiveData<User> observeById(int id);

    @Query("SELECT * FROM users WHERE email = :email AND passwordHash = :hash LIMIT 1")
    User login(String email, String hash);

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int emailExists(String email);

    // Всички налични донори в даден град — филтрираме съвместимост в Java с canDonateTo()
    @Query("SELECT * FROM users WHERE city = :city AND isAvailableToDonate = 1 AND id != :excludeId")
    List<User> findAvailableDonorsInCity(String city, int excludeId);

    // За AR компас — всички налични донори в радиус (груба GPS апроксимация)
    @Query("SELECT * FROM users WHERE isAvailableToDonate = 1 " +
            "AND bloodType = :bloodType " +
            "AND id != :excludeId " +
            "AND lat BETWEEN :minLat AND :maxLat " +
            "AND lng BETWEEN :minLng AND :maxLng")
    List<User> findDonorsNearby(String bloodType,
                                double minLat, double maxLat,
                                double minLng, double maxLng,
                                int excludeId);

    @Query("UPDATE users SET isAvailableToDonate = :available WHERE id = :id")
    void updateAvailability(int id, boolean available);

    @Query("UPDATE users SET rating = :rating, ratingCount = :count WHERE id = :id")
    void updateRating(int id, float rating, int count);

    @Query("UPDATE users SET donationCount = donationCount + 1 WHERE id = :id")
    void incrementDonationCount(int id);

    @Query("UPDATE users SET lat = :lat, lng = :lng, city = :city WHERE id = :id")
    void updateLocation(int id, double lat, double lng, String city);

    // ── Leaderboard ───────────────────────────────────────────────────────────
    @Query("SELECT * FROM users ORDER BY donationCount DESC LIMIT 20")
    LiveData<List<User>> getTopDonors();

    @Query("SELECT * FROM users ORDER BY donationCount DESC LIMIT 20")
    List<User> getTopDonorsSync();

    // ── Глобална статистика ───────────────────────────────────────────────────
    @Query("SELECT COUNT(*) FROM users WHERE isAvailableToDonate = 1")
    LiveData<Integer> getActiveDonorsCount();

    @Query("SELECT SUM(donationCount) FROM users")
    LiveData<Integer> getTotalDonations();
}
