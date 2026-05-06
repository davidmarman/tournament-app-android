package com.example.tournamentapp.data.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context){
        prefs = context.getSharedPreferences("TournamentAppPrefs",Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveAuthToken(String token, String rol, int idUsuario){
        editor.putString("USER_TOKEN", token);
        editor.putString("USER_ROLE", rol);
        editor.putInt("USER_ID",idUsuario);
        editor.apply();
    }

    public String getUserRole() {
        return prefs.getString("USER_ROLE", "User"); // Si no encuentra nada, asume "User" por seguridad
    }

    public String fetchAuthToken(){
        return prefs.getString("USER_TOKEN",null);
    }

    public int getUserId() {
        return prefs.getInt("USER_ID", 0);
    }

    public void logout(){
        editor.clear();
        editor.apply();
    }
}