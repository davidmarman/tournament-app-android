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

    public String fetchAuthToken(){
        return prefs.getString("USER_TOKEN",null);
    }

    public void logout(){
        editor.clear();
        editor.apply();
    }
}
