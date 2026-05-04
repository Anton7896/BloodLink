package com.bloodlink.ui.auth;

import android.app.Application;
import android.util.Patterns;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.bloodlink.data.database.AppDatabase;
import com.bloodlink.data.database.entities.User;
import com.bloodlink.utils.AppExecutors;
import com.bloodlink.utils.PasswordUtils;
import com.bloodlink.utils.SessionManager;

public class AuthViewModel extends AndroidViewModel {

    public final MutableLiveData<AuthState> authState = new MutableLiveData<>();

    private final AppDatabase    db;
    private final SessionManager session;

    public static class AuthState {
        public enum Type { LOADING, SUCCESS, ERROR }
        public final Type type; public final String message; public final int userId;
        private AuthState(Type t, String m, int id) { type=t; message=m; userId=id; }
        public static AuthState loading()           { return new AuthState(Type.LOADING,"", -1); }
        public static AuthState success(int id, String name) { return new AuthState(Type.SUCCESS, name, id); }
        public static AuthState error(String m)     { return new AuthState(Type.ERROR, m, -1); }
    }

    public AuthViewModel(@NonNull Application app) {
        super(app);
        db      = AppDatabase.getInstance(app);
        session = new SessionManager(app);
    }

    public void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            authState.setValue(AuthState.error("Моля, попълнете всички полета")); return;
        }
        authState.setValue(AuthState.loading());
        AppExecutors.getInstance().diskIO(() -> {
            try {
                User user = db.userDao().login(email.trim().toLowerCase(), PasswordUtils.hash(password));
                if (user != null) {
                    session.save(user.id, user.getFullName(), user.bloodType);
                    authState.postValue(AuthState.success(user.id, user.getFullName()));
                } else {
                    authState.postValue(AuthState.error("Грешен имейл или парола"));
                }
            } catch (Exception e) {
                authState.postValue(AuthState.error("Грешка: " + e.getMessage()));
            }
        });
    }

    public void register(String firstName, String lastName, String email, String phone,
                         String password, String confirm, String bloodType, String city) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || phone.isEmpty() || password.isEmpty() || bloodType.isEmpty() || city.isEmpty()) {
            authState.setValue(AuthState.error("Моля, попълнете всички полета")); return;
        }
        if (!password.equals(confirm)) {
            authState.setValue(AuthState.error("Паролите не съвпадат")); return;
        }
        if (password.length() < 6) {
            authState.setValue(AuthState.error("Паролата трябва да е поне 6 символа")); return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            authState.setValue(AuthState.error("Невалиден имейл")); return;
        }
        authState.setValue(AuthState.loading());
        AppExecutors.getInstance().diskIO(() -> {
            try {
                if (db.userDao().emailExists(email.trim().toLowerCase()) > 0) {
                    authState.postValue(AuthState.error("Имейлът вече е регистриран")); return;
                }
                User user = new User(firstName.trim(), lastName.trim(),
                        email.trim().toLowerCase(), phone.trim(),
                        PasswordUtils.hash(password), bloodType, city.trim());
                long newId = db.userDao().insert(user);
                session.save((int) newId, firstName + " " + lastName, bloodType);
                authState.postValue(AuthState.success((int) newId, firstName + " " + lastName));
            } catch (Exception e) {
                authState.postValue(AuthState.error("Грешка: " + e.getMessage()));
            }
        });
    }
}
