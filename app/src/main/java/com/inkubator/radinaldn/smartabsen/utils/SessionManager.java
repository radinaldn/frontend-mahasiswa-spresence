package com.inkubator.radinaldn.smartabsen.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.inkubator.radinaldn.smartabsen.models.Mahasiswa;

import java.util.HashMap;

/**
 * Created by radinaldn on 03/07/18.
 */

public class SessionManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context _context;

    public static final String IS_LOGGED_IN = "isLoggedIn";
    public static final String NIM = "nim";
    public static final String PASSWORD = "password";
    public static final String IMEI = "imei";
    public static final String ID_TELEGRAM = "idTelegram";
    public static final String NAMA = "nama";
    public static final String JK = "jk";
    public static final String FOTO = "foto";
    public static final String ID_JURUSAN = "idJurusan";

    public Context get_context(){
        return _context;
    }

    // constructor
    public SessionManager(Context context){
        this._context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(String nim, String imei, String password, String id_telegram, String nama, String jk, String foto, String id_jurusan){
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(NIM, nim);
        editor.putString(PASSWORD, password);
        editor.putString(IMEI, imei);
        editor.putString(ID_TELEGRAM, id_telegram);
        editor.putString(NAMA, nama);
        editor.putString(JK, jk);
        editor.putString(FOTO, foto);
        editor.putString(ID_JURUSAN, id_jurusan);
        editor.commit();
    }

    public void updateLoginSession(String id_telegram){
        editor.putString(ID_TELEGRAM, id_telegram);
        editor.commit();
    }



    public HashMap<String, String> getMahasiswaDetail(){
        HashMap<String,String> mahasiswa = new HashMap<>();
        mahasiswa.put(NIM, sharedPreferences.getString(NIM,null));
        mahasiswa.put(PASSWORD, sharedPreferences.getString(PASSWORD, null));
        mahasiswa.put(IMEI, sharedPreferences.getString(IMEI,null));
        mahasiswa.put(ID_TELEGRAM, sharedPreferences.getString(ID_TELEGRAM,null));
        mahasiswa.put(NAMA, sharedPreferences.getString(NAMA,null));
        mahasiswa.put(JK, sharedPreferences.getString(JK,null));
        mahasiswa.put(FOTO, sharedPreferences.getString(FOTO,null));
        mahasiswa.put(ID_JURUSAN, sharedPreferences.getString(ID_JURUSAN,null));
        return mahasiswa;
    }

    public void logoutMahasiswa(){
        editor.clear();
        editor.commit();
    }

    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }
}
