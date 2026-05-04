package com.bloodlink.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.bloodlink.data.database.entities.Rating;
import java.util.List;

@Dao
public interface RatingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Rating rating);

    @Query("SELECT * FROM ratings WHERE ratedUserId = :userId ORDER BY createdAt DESC")
    LiveData<List<Rating>> observeRatingsForUser(int userId);

    @Query("SELECT AVG(stars) FROM ratings WHERE ratedUserId = :userId")
    float getAverageRating(int userId);

    @Query("SELECT COUNT(*) FROM ratings WHERE ratedUserId = :userId")
    int getRatingCount(int userId);

    @Query("SELECT COUNT(*) FROM ratings WHERE ratedUserId = :rated AND raterUserId = :rater AND requestId = :reqId")
    int hasRated(int rated, int rater, int reqId);
}
