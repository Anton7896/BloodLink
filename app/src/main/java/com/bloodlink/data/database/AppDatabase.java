package com.bloodlink.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.bloodlink.data.database.dao.*;
import com.bloodlink.data.database.entities.*;

@Database(
    entities = { User.class, BloodRequest.class, DonorResponse.class, Rating.class },
    version  = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao          userDao();
    public abstract BloodRequestDao  bloodRequestDao();
    public abstract DonorResponseDao donorResponseDao();
    public abstract RatingDao        ratingDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "bloodlink_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
