package com.inkubator.radinaldn.smartabsen.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.inkubator.radinaldn.smartabsen.adapters.MengambilAdapter;
import com.inkubator.radinaldn.smartabsen.config.ServerConfig;
import com.inkubator.radinaldn.smartabsen.R;
import com.inkubator.radinaldn.smartabsen.models.KehadiranDosen;
import com.inkubator.radinaldn.smartabsen.models.Mahasiswa;
import com.inkubator.radinaldn.smartabsen.models.Mengambil;
import com.inkubator.radinaldn.smartabsen.responses.ResponseKehadiranDosen;
import com.inkubator.radinaldn.smartabsen.responses.ResponseMengambil;
import com.inkubator.radinaldn.smartabsen.responses.ResponseUpdateImei;
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
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TextToSpeech.OnInitListener {

    ImageView imageView;
    TextView tvname, tvjurusan, tv_libur;
    FloatingActionButton fab, fab_mic;
    private ProgressDialog pDialog;
    private TextToSpeech tts;
    String RESULT_STT; // for SpeechToText result
    String RESULT_TTS; // for TextToSPeech result

    String nim, imei, nama, foto, id_fakultas, id_jurusan, nama_jurusan;
    SharedPreferences sharedPreferences;

    private static final String TAG_Dimana = "Dimana";
    private static final String TAG_dimana = "dimana";
    private static final String TAG_Di_mana = "Di mana";
    private static final String TAG_di_mana = "di mana";
    private static final String TAG_Pak = "Pak";
    private static final String TAG_pak = "pak";
    private static final String TAG_Bu = "Bu";
    private static final String TAG_bu = "bu";


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

    AlertDialog.Builder alertDialogBuilder;
    LayoutInflater inflater;
    View dialogView;

    @SuppressLint("ClickableViewAccessibility")
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


        // check ID Telegram User
        if (sessionManager.getMahasiswaDetail().get(SessionManager.ID_TELEGRAM)!=null && sessionManager.getMahasiswaDetail().get(SessionManager.ID_TELEGRAM).equalsIgnoreCase("0")){
            // open tele smart presence bot
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/get_id_bot"));
            startActivity(browserIntent);
            showFormIdTelegram(sessionManager.getMahasiswaDetail().get(SessionManager.NIM));

        }

        apiService = ApiClient.getClient().create(ApiInterface.class);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Stetho.initializeWithDefaults(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab_mic = findViewById(R.id.fab_mic);
        tts = new TextToSpeech(this, this);

        // init SpeechRecognizer
        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                for (int i = 0; i < matches.size(); i++) {
                    Log.i(TAG, "onResults speechRecog (: " + i + 1 + ")" + matches.get(i));
                }

                // displaying the first match
                if (matches != null){

                    Toast.makeText(getApplicationContext(), matches.get(0), Toast.LENGTH_LONG).show();
                    RESULT_STT = matches.get(0);

                    /**
                     * Rules menerima inputan suara
                     */

                    //split result
                    String [] hasils = RESULT_STT.split(" ");

                    // jika hasils[0] == di || hasil[0] == dimana
                    if (hasils[0].equalsIgnoreCase("di") || RESULT_STT.equalsIgnoreCase(TAG_dimana)){

                        String nama_dosen = null;

                        if (hasils[0].equalsIgnoreCase("di")){
                            if (hasils[2].equals("pak")){
                                String str_new = RESULT_STT.replaceFirst("di mana pak ", "");
                                nama_dosen = str_new;

                            } if(hasils[2].equals("Pak")){
                                String str_new = RESULT_STT.replaceFirst("di mana Pak ", "");
                                nama_dosen = str_new;

                            } if (hasils[2].equalsIgnoreCase("bu")){
                                String str_new = RESULT_STT.replaceFirst("di mana bu ", "");
                                nama_dosen = str_new;
                            } if (hasils[2].equalsIgnoreCase("Bu")) {
                                String str_new = RESULT_STT.replaceFirst("di mana Bu ", "");
                                nama_dosen = str_new;
                            }

                        } else if (hasils[0].equalsIgnoreCase("dimana")){
                            if (hasils[1].equalsIgnoreCase("pak")){
                                String str_new = RESULT_STT.replaceFirst("dimana pak", "");
                                nama_dosen = str_new;

                            } if (hasils[1].equalsIgnoreCase("Pak")) {
                                String str_new = RESULT_STT.replaceFirst("dimana Pak", "");
                                nama_dosen = str_new;

                            } if (hasils[1].equalsIgnoreCase("bu")){
                                String str_new = RESULT_STT.replaceFirst("dimana bu", "");
                                nama_dosen = str_new;
                            } if (hasils[1].equalsIgnoreCase("Bu")){
                                String str_new = RESULT_STT.replaceFirst("dimana Bu", "");
                                nama_dosen = str_new;
                            }
                        }

                        if (nama_dosen!=null){
                            //Jika berhasil dan sesuai, cari data kehadiran dosen
                            final String finalNama_dosen = nama_dosen;
                            apiService.dosenKehadiranFindByName(nama_dosen).enqueue(new Callback<ResponseKehadiranDosen>() {
                                @Override
                                public void onResponse(Call<ResponseKehadiranDosen> call, Response<ResponseKehadiranDosen> response) {
                                    if (response.isSuccessful()) {

                                        System.out.println(response.toString());
                                        System.out.println(response.body().toString());
                                        if (response.body().getKehadiranDosen().size()==1){

                                            List<KehadiranDosen> hasil_req = response.body().getKehadiranDosen();
                                            String nama_dosen = hasil_req.get(0).getNama_dosen();
                                            String status_kehadiran = hasil_req.get(0).getStatus_kehadiran();
                                            String nama_kota = hasil_req.get(0).getNama_kota();
                                            String last_update = hasil_req.get(0).getLast_update();

                                            RESULT_TTS = finalNama_dosen+" yang ditemukan "+nama_dosen+" dengan status "+status_kehadiran+", berada di "+nama_kota+" terakhir update "+last_update;
                                            speakOut();
                                        } else if (response.body().getKehadiranDosen().size() > 1){
                                            Toast.makeText(getApplicationContext(), "Data " + finalNama_dosen +" ada "+response.body().getKehadiranDosen().size(), Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Tidak ada  Data " + finalNama_dosen, Toast.LENGTH_LONG).show();
                                        }


                                    } else {
                                        Toast.makeText(getApplicationContext(), "Tidak dapat memuat data "+finalNama_dosen, Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseKehadiranDosen> call, Throwable t) {
                                    t.getLocalizedMessage();
                                }
                            });
                        }



                    } else {
                        Toast.makeText(getApplicationContext(), "MainActivity.java line 219 :\n"+ RESULT_STT, Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(getApplicationContext(), matches.get(0), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check apakah GPS aktif?
                isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!isGPSEnabled) {

                    AlertDialog.Builder confirmBox = new AlertDialog.Builder(MainActivity.this);
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

        Picasso.with(getApplicationContext())
                .load(serverConfig.IMAGE_PATH+"/mahasiswa/"+sessionManager.getMahasiswaDetail().get(TAG_FOTO))
                .resize(100, 100)
                .placeholder(R.drawable.dummy_ava)
                .error(R.drawable.dummy_ava)
                .centerCrop()
                .into(imageView);

        tvname.setText(sessionManager.getMahasiswaDetail().get(TAG_NAMA));
        tvjurusan.setText("(" +sessionManager.getMahasiswaDetail().get(TAG_NIM)+ ")");

        tv_libur = findViewById(R.id.tv_libur);

        /*
        Isi data Kuliah hari ini
         */

        recyclerView = findViewById(R.id.recyclerView);

        NIM = sessionManager.getMahasiswaDetail().get(TAG_NIM);

        getKuliahHariIni(NIM);


        // Action click fab mic
        fab_mic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //  onPressed
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        fab_mic.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.textColorSecondary)));
                        Toast.makeText(getApplicationContext(), "Silahkan berbicara", Toast.LENGTH_SHORT).show();
                        return true;

                    case MotionEvent.ACTION_UP:
                        //onReleased
                        mSpeechRecognizer.stopListening();
                        fab_mic.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                        return true;
                }

                return false;
            }
        });
        // End of Action click fab mic

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
                        mengambilArrayList.addAll(response.body().getMengambil());
                        adapter = new MengambilAdapter(mengambilArrayList, getApplicationContext());

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);

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
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://goo.gl/forms/15nIADlcuGslPHrr2"));
            startActivity(browserIntent);

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

    // onInit for TextToSpeech
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS){
            int result = tts.setLanguage(new Locale("id", "ID"));

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "TTS: This language is not supported");
            } else {
                // if no error
//                speakOut();
            }
        } else {
            Log.e(TAG, "Initialization failed");
        }
    }

    private void speakOut() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(RESULT_TTS, TextToSpeech.QUEUE_FLUSH, null, "id1");
        } else {
            Toast.makeText(getApplicationContext(), RESULT_TTS, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        //shutdown tts
        if (tts!=null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();

    }

    private void showFormIdTelegram(final String str_nim) {
        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_id_telegram, null);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setIcon(R.drawable.attend_logo);
        alertDialogBuilder.setTitle(R.string.tambah_id_telegram);

        final EditText et_id_telegram = dialogView.findViewById(R.id.et_id_telegram);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String id_telegram = et_id_telegram.getText().toString();

                        apiService.mahasiswaUpdateIdTelegram(str_nim, id_telegram).enqueue(new Callback<ResponseUpdateImei>() {
                            @Override
                            public void onResponse(Call<ResponseUpdateImei> call, Response<ResponseUpdateImei> response) {
                                if (response.isSuccessful()){
                                    if (response.body().getCode().equalsIgnoreCase("200")){
                                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                                        // update session
                                        if (!id_telegram.isEmpty()){
                                            sessionManager.updateLoginSession(id_telegram);
                                            // open tele smart presence bot
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/smart_presence_bot"));
                                            startActivity(browserIntent);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Anda belum mengisi kolom ID Telegram", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseUpdateImei> call, Throwable t) {
                                t.getLocalizedMessage();
                            }
                        });

                    }
                })

                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
