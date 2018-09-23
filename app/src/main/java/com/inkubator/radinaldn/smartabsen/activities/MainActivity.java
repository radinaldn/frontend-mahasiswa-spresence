package com.inkubator.radinaldn.smartabsen.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.app.AlertDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.inkubator.radinaldn.smartabsen.adapters.MengambilAdapter;
import com.inkubator.radinaldn.smartabsen.config.ServerConfig;
import com.inkubator.radinaldn.smartabsen.R;
import com.inkubator.radinaldn.smartabsen.models.Mengambil;
import com.inkubator.radinaldn.smartabsen.responses.ResponseMengambil;
import com.inkubator.radinaldn.smartabsen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsen.rests.ApiInterface;
import com.inkubator.radinaldn.smartabsen.utils.SessionManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageView imageView;
    TextView tvname, tvjurusan, tv_libur;
    private ProgressDialog pDialog;

    String nim, imei, nama, foto, id_fakultas, id_jurusan, nama_jurusan;
    SharedPreferences sharedPreferences;

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String TAG_NIM = "nim";
    public static final String TAG_IMEI = "imei";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_FOTO = "foto";
    public static final String ID_TELEGRAM = "idTelegram";
    private static String NIM;

    ServerConfig serverConfig;

    SessionManager sessionManager;
    LocationManager manager;
    boolean isGPSEnabled;

    SwipeRefreshLayout swipeRefreshLayout;
    ApiInterface apiService;

    private RecyclerView recyclerView;
    private MengambilAdapter adapter;
    private ArrayList<Mengambil> mengambilArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do permission for all
        requestAllPermission();
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        if(!sessionManager.isLoggedIn()){
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            // agar tidak balik ke activity ini lagi
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(i);
            finish();
        }

        apiService = ApiClient.getClient().create(ApiInterface.class);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Stetho.initializeWithDefaults(this);

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
        tvjurusan.setText("(" +sessionManager.getMahasiswaDetail().get(TAG_NIM)+ ")");

        tv_libur = findViewById(R.id.tv_libur);

        /*
        Isi data Kuliah hari ini
         */

        recyclerView = findViewById(R.id.recyclerView);

        NIM = sessionManager.getMahasiswaDetail().get(TAG_NIM);

        getKuliahHariIni(NIM);



    }

    private void requestAllPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void getKuliahHariIni(String nim) {
        System.out.println("ambil data mengambil hari ini");
        apiService.mengambilFindAllTodayByNim(nim).enqueue(new Callback<ResponseMengambil>() {
            @Override
            public void onResponse(Call<ResponseMengambil> call, Response<ResponseMengambil> response) {
                System.out.println(response.toString());
                if (response.isSuccessful()){
                    System.out.println("ada data : "+response.body().getMengambil().size());
                    if (response.body().getMengambil().size() > 0){
                        // hilangkan pesan libur
                        tv_libur.setVisibility(View.GONE);
                        mengambilArrayList = new ArrayList<>();
                        for (int i = 0; i < response.body().getMengambil().size(); i++) {
                            Log.i(TAG, "onResponse: ada matakuliah hari ini : "+response.body().getMengambil().get(i).getNamaMatakuliah());
                            String id_mengambil = response.body().getMengambil().get(i).getIdMengambil();
                            String id_mengajar = response.body().getMengambil().get(i).getIdMengajar();
                            String sisa_jatah = response.body().getMengambil().get(i).getSisaJatah();
                            String nama_matakuliah = response.body().getMengambil().get(i).getNamaMatakuliah();
                            String nama_dosen = response.body().getMengambil().get(i).getNamaDosen();
                            String waktu_mulai = response.body().getMengambil().get(i).getWaktuMulai();
                            String hari = response.body().getMengambil().get(i).getHari();
                            String sks = response.body().getMengambil().get(i).getSks();
                            String nama_kelas = response.body().getMengambil().get(i).getNama_kelas();

                            mengambilArrayList.add(new Mengambil(id_mengambil, id_mengajar, sisa_jatah, nama_matakuliah, nama_dosen, waktu_mulai, hari, sks, nama_kelas));

                            adapter = new MengambilAdapter(mengambilArrayList, getApplicationContext());

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);

                        }

                    } else {
//                        Toast.makeText(getApplicationContext(), "Tidak ada kuliah hari ini hohoho", Toast.LENGTH_SHORT).show();
                        // munculkan pesan libur
                        tv_libur.setVisibility(View.VISIBLE);
                    }
                } else {
//                    Toast.makeText(getApplicationContext(), "Error : "+response.errorBody().toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseMengambil> call, Throwable t) {
                t.getLocalizedMessage();
            }
        });
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
            Intent intent = new Intent(MainActivity.this, HistoriPerkuliahanActivity.class);
            startActivity(intent);
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
