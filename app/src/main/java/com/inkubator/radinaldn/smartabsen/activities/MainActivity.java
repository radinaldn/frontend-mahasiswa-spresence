package com.inkubator.radinaldn.smartabsen.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.inkubator.radinaldn.smartabsen.config.ServerConfig;
import com.inkubator.radinaldn.smartabsen.R;
import com.inkubator.radinaldn.smartabsen.models.Mengambil;
import com.inkubator.radinaldn.smartabsen.utils.SessionManager;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageView imageView;
    TextView tvname, tvjurusan;
    private ProgressDialog pDialog;

    String nim, imei, nama, foto, id_fakultas, id_jurusan, nama_jurusan;
    SharedPreferences sharedPreferences;

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String TAG_NIM = "nim";
    public static final String TAG_IMEI = "imei";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_FOTO = "foto";
    public static final String ID_TELEGRAM = "idTelegram";

    ServerConfig serverConfig;

    SessionManager sessionManager;
    LocationManager manager;
    boolean isGPSEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Stetho.initializeWithDefaults(this);

        sessionManager = new SessionManager(this);

        serverConfig = new ServerConfig();

        if(!sessionManager.isLoggedIn()){
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            // agar tidak balik ke activity ini lagi
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(i);
            finish();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check apakah GPS aktif?
                isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!isGPSEnabled) {

                    AlertDialog.Builder confirmBox = new android.app.AlertDialog.Builder(MainActivity.this);
                    confirmBox.setTitle("GPS belum diaktifkan");
                    confirmBox.setIcon(R.drawable.ic_gps_off_black_24dp);
                    confirmBox.setMessage("Apakah anda ingin mengaktifkan GPS ?\nMengaktifkan GPS akan membantu aplikasi memperoleh lokasi yang akurat.");
                    confirmBox.setCancelable(false);

                    confirmBox.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "Aktifkan GPS terlebih dahulu dan coba lagi", Toast.LENGTH_SHORT).show();
                        }
                    });
                    confirmBox.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do scan qr code here
                            // sebelum masuk ke ScanningQRCodeActivity.class lakukan pengecekkan apakah user menghidupkan lokasi palsu?
                            Intent i = new Intent(getApplicationContext(), ScanQRCodeActivity.class);
                            startActivity(i);
                        }
                    });

                    AlertDialog alertDialogKonfirmasi = confirmBox.create();
                    alertDialogKonfirmasi.show();

                } else {
                    Intent i = new Intent(getApplicationContext(), ScanQRCodeActivity.class);
                    startActivity(i);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Log.i(TAG, "onCreate: "+sessionManager.getMahasiswaDetail().get(TAG_NAMA));

        View header = navigationView.getHeaderView(0);

        imageView = header.findViewById(R.id.imageView);

        tvname = header.findViewById(R.id.tvName);
        tvjurusan = header.findViewById(R.id.tvJurusan);

        Picasso.with(getApplicationContext()).load(serverConfig.IMAGE_PATH+"/mahasiswa/"+sessionManager.getMahasiswaDetail().get(TAG_FOTO)).resize(100, 100).into(imageView);
        tvname.setText(sessionManager.getMahasiswaDetail().get(TAG_NAMA));
        tvjurusan.setText("nama_jurusan"+" (" +sessionManager.getMahasiswaDetail().get(TAG_NIM)+ ")");

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.log_out) {
            // Handling for logout action
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);

            // redirect to login page
            sessionManager.logoutMahasiswa();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.absen) {
            // Handle the camera action
        } else if (id == R.id.jadwal_kuliah) {
            Intent intent = new Intent(MainActivity.this, MengambilActivity.class);
            startActivity(intent);
        } else if (id == R.id.kehadiran_dosen) {
            Intent intent = new Intent(MainActivity.this, KehadiranDosenActivity.class);
            startActivity(intent);
        } else if (id == R.id.histori_saya) {

        } else if (id == R.id.nav_setting){
            Intent intent = new Intent(MainActivity.this, ProfileSayaActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_about) {
            AlertDialog alertDialog = new AlertDialog.Builder(
                    MainActivity.this).create();

            // Setting Dialog Title
            alertDialog.setTitle(R.string.license_title);

            // Setting Dialog Message
            alertDialog.setMessage(getString(R.string.about_message));

            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.attend_logo);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
