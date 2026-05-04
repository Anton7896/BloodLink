package com.bloodlink.ui.profile;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;

import com.bloodlink.data.database.AppDatabase;
import com.bloodlink.data.database.entities.*;
import com.bloodlink.utils.*;

import java.util.List;

public class ProfileViewModel extends AndroidViewModel {

    public final MutableLiveData<String> operationResult = new MutableLiveData<>();
    private final AppDatabase    db;
    private final SessionManager session;

    public ProfileViewModel(@NonNull Application app) {
        super(app);
        db      = AppDatabase.getInstance(app);
        session = new SessionManager(app);
    }

    public int    getCurrentUserId()  { return session.getUserId(); }
    public LiveData<User>          getCurrentUser() { return db.userDao().observeById(getCurrentUserId()); }
    public LiveData<List<Rating>>  getMyRatings()   { return db.ratingDao().observeRatingsForUser(getCurrentUserId()); }

    public void toggleAvailability(boolean available) {
        AppExecutors.getInstance().diskIO(() -> {
            db.userDao().updateAvailability(getCurrentUserId(), available);
            operationResult.postValue(available
                    ? "Статусът е: Готов за даряване ✅"
                    : "Статусът е: Не е наличен ⛔");
        });
    }

    public void updateProfile(String firstName, String lastName, String phone, String city) {
        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
            operationResult.setValue("Моля, попълнете всички полета"); return;
        }
        AppExecutors.getInstance().diskIO(() -> {
            try {
                User user = db.userDao().getById(getCurrentUserId());
                if (user == null) return;
                user.firstName = firstName.trim();
                user.lastName  = lastName.trim();
                user.phone     = phone.trim();
                user.city      = city.trim();
                db.userDao().update(user);
                session.save(user.id, user.getFullName(), user.bloodType);
                operationResult.postValue("Профилът е обновен ✅");
            } catch (Exception e) {
                operationResult.postValue("Грешка: " + e.getMessage());
            }
        });
    }

    public void logout() { session.logout(); }
}
