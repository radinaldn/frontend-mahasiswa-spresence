package com.inkubator.radinaldn.smartabsen.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inkubator.radinaldn.smartabsen.R;
import com.inkubator.radinaldn.smartabsen.config.ServerConfig;
import com.inkubator.radinaldn.smartabsen.utils.SessionManager;
import com.squareup.picasso.Picasso;

public class ProfileSayaActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private ImageView iv_foto;
    public static final String TAG_NIM = "nim";
    public static final String TAG_IMEI = "imei";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_FOTO = "foto";
    public static final String TAG_JK = "jk";
    public static final String ID_TELEGRAM = "idTelegram";
    private String nim, imei, nama, foto, id_telegram, jk;
    private TextView tv_nama, tv_nim, tv_imei, tv_id_telegram, tv_jk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_saya);

        sessionManager = new SessionManager(this);

        nim = sessionManager.getMahasiswaDetail().get(TAG_NIM);
        imei = sessionManager.getMahasiswaDetail().get(TAG_IMEI);
        nama = sessionManager.getMahasiswaDetail().get(TAG_NAMA);
        foto = sessionManager.getMahasiswaDetail().get(TAG_FOTO);
        jk = sessionManager.getMahasiswaDetail().get(TAG_JK);

        id_telegram = sessionManager.getMahasiswaDetail().get(ID_TELEGRAM);

        iv_foto = findViewById(R.id.iv_foto);
        tv_nama = findViewById(R.id.tv_nama);
        tv_nim = findViewById(R.id.tv_nim);
        tv_imei = findViewById(R.id.tv_imei);
        tv_id_telegram = findViewById(R.id.tv_id_telegram);
        tv_jk = findViewById(R.id.tv_jk);

        Picasso.with(getApplicationContext()).load(ServerConfig.IMAGE_PATH+"/mahasiswa/"+foto).into(iv_foto);
        tv_nim.setText(nim);
        tv_nama.setText(nama);
        tv_imei.setText(imei);
        tv_id_telegram.setText(id_telegram);
        tv_jk.setText(jk);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.toolbar_layout);

        collapsingToolbar.setTitle(nama);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.edit){
            Toast.makeText(getApplicationContext(), "Anda menekan edit", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
