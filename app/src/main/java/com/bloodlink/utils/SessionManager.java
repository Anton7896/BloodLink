package com.bloodlink.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF    = "bloodlink_session";
    private static final String KEY_ID  = "user_id";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_BLOOD = "blood_type";
    private static final int NO_USER = -1;

    private final SharedPreferences prefs;

    public SessionManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void save(int id, String name, String bloodType) {
        prefs.edit().putInt(KEY_ID, id).putString(KEY_NAME, name)
             .putString(KEY_BLOOD, bloodType).apply();
    }

    public int    getUserId()    { return prefs.getInt(KEY_ID, NO_USER); }
    public String getUserName()  { return prefs.getString(KEY_NAME, ""); }
    public String getBloodType() { return prefs.getString(KEY_BLOOD, ""); }
    public boolean isLoggedIn()  { return getUserId() != NO_USER; }
    public void logout()         { prefs.edit().clear().apply(); }
}
