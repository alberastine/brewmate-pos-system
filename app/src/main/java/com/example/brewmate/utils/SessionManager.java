package com.example.brewmate.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_ROLE = "userRole";
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveUserRole(String role) {
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public String getUserRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
