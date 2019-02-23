package com.inkubator.radinaldn.smartabsen.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.inkubator.radinaldn.smartabsen.BuildConfig;
import com.inkubator.radinaldn.smartabsen.R;
import com.inkubator.radinaldn.smartabsen.responses.ResponseIsiPresensi;
import com.inkubator.radinaldn.smartabsen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsen.rests.ApiInterface;
import com.inkubator.radinaldn.smartabsen.utils.ConnectionDetector;
import com.inkubator.radinaldn.smartabsen.utils.SessionManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.ankit.gpslibrary.MyTracker;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by radinaldn on 24/07/18.
 */

public class ScanQRCodeActivity extends AppCompatActivity {

    private static final long BATAS_MAKS_SESSION_LOCATION = 5; // in minutes
    MyTracker tracker;
    Double myLat, myLng;
    private ProgressDialog pDialog;

    private static final String TAG_ID_PRESENSI = "id_presensi";
    MaterialBarcodeScanner materialBarcodeScanner;
    private Barcode barcodeResult;
    ApiInterface apiService;

    String latittude, longitude, altitude, bestProvider;
    Location lastLocation;
    Criteria criteria = new Criteria();

    LocationManager locationManager;
    LocationListener locationListener;

    public boolean alreadyStartScanning = false;
    ArrayList scanResultSub;
    SessionManager sessionManager;
    private static final String TAG_NIM = "nim";
    ConnectionDetector connectionDetector;
    View parentLayout;
    TextView tvClickMe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_empty_scan);

        tvClickMe = findViewById(R.id.tvClickMe);

        tvClickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartActivity();
            }
        });

        parentLayout = findViewById(android.R.id.content);
        tracker = new MyTracker(this);
        connectionDetector = new ConnectionDetector(ScanQRCodeActivity.this);

        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        scanResultSub = new ArrayList();

        // for location service

        // 1. get camera permission from user
        // 2. check if user turn on on the mock location
        if (isMockSettingsON(this)) {
            //   a. if (mockLocation()) onBackPressed() with message "You wanna try to cheating dude ?
            // turn off your fake gps and be honest"
            showSnacMockLoc();
        }

        // Dapatkan lokasi dan nyalakan scanner
        long selisihMenit = 0;

        if (sessionManager.hasLastLocation() && sessionManager.getMyLocationDetail().get(SessionManager.LAST_LOCATED) != null) {
            // hitung apakah last update == sudah basi
            String sessLastLocated = sessionManager.getMyLocationDetail().get(SessionManager.LAST_LOCATED);

            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String saatIni = mdformat.format(currentTime);
            Date firstDate, secondDate;

            try {
                firstDate = mdformat.parse(sessLastLocated);
                secondDate = mdformat.parse(saatIni);

                selisihMenit = secondDate.getTime() - firstDate.getTime();

//                long diffSeconds = selisih / 1000 % 60;
//                long diffMinutes = selisih / (60 * 1000) % 60;
//                long diffHours = selisih / (60 * 60 * 1000) % 24;
//                long diffDays = selisih / (24 * 60 * 60 * 1000);

//                Toast.makeText(getApplicationContext(), diffDays+" hari, "+diffHours+" jam, "+diffMinutes+" menit, "+diffSeconds+" detik", Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();
            }


            selisihMenit = selisihMenit / (60 * 1000);
        }

        //Toast.makeText(getApplicationContext(), "selisihMenit : "+selisihMenit, Toast.LENGTH_LONG).show();

        if (selisihMenit < BATAS_MAKS_SESSION_LOCATION && sessionManager.getMyLocationDetail().get(SessionManager.LAST_LOCATED) != null) {

            String sessionLat = sessionManager.getMyLocationDetail().get(SessionManager.LATITUDE);
            String sessionLng = sessionManager.getMyLocationDetail().get(SessionManager.LONGITUDE);

            nyalakanScanner(sessionLat, sessionLng);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.info));
            builder.setCancelable(false);
            builder.setIcon(R.drawable.ic_my_location);
            builder.setMessage(R.string.maaf_riwayat_lokasi_yang_anda_simpan_tdk_berlaku_lagi_ingin_ke_menu_dimana_saya);
            builder.setPositiveButton(getResources().getString(R.string.ya), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(ScanQRCodeActivity.this, DimanaSayaActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish();
                }
            });

            builder.setNegativeButton(getResources().getString(R.string.tidak), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    goToMainActivity();
                }
            });

            builder.create().show();
        }


    }

    private void nyalakanScanner(final String myLat, final String myLng) {
//        Toast.makeText(getApplicationContext(), "myLat : " + myLat + "\nmyLng : " + myLng, Toast.LENGTH_LONG).show();
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
//                        Toast.makeText(getApplicationContext(), "Camera permissions are granted!", Toast.LENGTH_SHORT).show();
                        // permission is granted
                        materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                                .withActivity(ScanQRCodeActivity.this)
                                .withEnableAutoFocus(true)
                                .withBleepEnabled(true)
                                .withBackfacingCamera()
                                .withText(getString(R.string.arahkan_kamera_qr_code))
                                .withCenterTracker()
                                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                                    @Override
                                    public void onResult(Barcode barcode) {
                                        barcodeResult = barcode;
                                        //                        Toast.makeText(getApplicationContext(), "latitude saya : "+latittude+"\nHasil Scan : " + barcode.rawValue, Toast.LENGTH_LONG).show();
                                        final String scanResult = barcode.rawValue;
                                        Log.i("TAG", "onLocationChanged: scanResult = " + scanResult);

                                        for (String retval : scanResult.split("#")) {
                                            scanResultSub.add(retval);
                                            Log.i("TAG", "onLocationChanged: scanResultSub = " + retval);
                                        }

                                        double doubleDosenLat = Double.parseDouble(scanResultSub.get(1).toString());
                                        double doubleDosenLng = Double.parseDouble(scanResultSub.get(2).toString());
                                        double doubleMyLat = Double.parseDouble(myLat);
                                        double doubleMyLng = Double.parseDouble(myLng);

                                        // hitung jarak
                                        int jarak = getDistance(doubleDosenLat, doubleMyLat, doubleDosenLng, doubleMyLng, 0, 0);
                                        Log.i("ScanQRCo", "Jarak : " + jarak);

                                        String id_presensi = scanResultSub.get(0).toString();
                                        String nim = sessionManager.getMahasiswaDetail().get(TAG_NIM);
                                        String lat = myLat;
                                        String lng = myLng;
                                        String alt = altitude;

                                        if (connectionDetector.isConnectingToInternet()) {

                                            // jika jarak <= 100meter
                                            System.out.println("jarak : "+jarak);
                                            if (jarak<= BuildConfig.BATAS_JARAK_SCANNING){
                                                isiPresensi(id_presensi, nim, lat, lng, jarak);
                                            } else {
                                                Intent intent = new Intent(ScanQRCodeActivity.this, DimanaSayaActivity.class);
                                                intent.putExtra(DimanaSayaActivity.TAG_JARAK_TERLALU_JAUH, jarak);
                                                finish();
                                                startActivity(intent);
                                            }

                                        } else {

                                            finish();
                                            Toast.makeText(getApplicationContext(), R.string.terjadi_kesalahan_pastikan_koneksi_internet_anda_menyala_dan_coba_lagi, Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                })
                                .build();
                        materialBarcodeScanner.startScan();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    // method isi Presensi
    private void isiPresensi(final String id_presensi, String nim, String lat, String lng, int jarak) {
        apiService.isiPresensi(id_presensi, nim, lat, lng, jarak).enqueue(new Callback<ResponseIsiPresensi>() {
            @Override
            public void onResponse(Call<ResponseIsiPresensi> call, Response<ResponseIsiPresensi> response) {
                Log.d("isiPresensi", "onResponse: " + response.body().toString());
                if (response.isSuccessful()) {

                    /*
                     Pada proses memindai QR Code terjadi beberapa case flow.
                     1. Sukses (200), data berhasil masuk
                     2. Forbidden (403), presensi closed/tidak terdaftar di matakuliah tsb/presensi sudah dilakukan
                     3. Not Found (404)
                     4. Error (500)
                     */

                    // jika Response 200 dan 403 Already
                    if (response.body().getCode().equalsIgnoreCase("200") && response.body().getStatus().equalsIgnoreCase("OK") || response.body().getCode().equalsIgnoreCase("403") && response.body().getStatus().equalsIgnoreCase("Already")) { // jika berhasil isi presensi
                        Intent intent = new Intent(ScanQRCodeActivity.this, HistoriPresensiActivity.class);
                        Toast.makeText(ScanQRCodeActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        intent.putExtra(TAG_ID_PRESENSI, id_presensi);
                        startActivity(intent);
                        finish();
                    } else {

                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ScanQRCodeActivity.this, MainActivity.class);
                        Toast.makeText(ScanQRCodeActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        //intent.putExtra(TAG_ID_PRESENSI, id_presensi);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.terjadi_kesalahan), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseIsiPresensi> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.gagal_terhubung_ke_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    // method menghitung jarak antara titik lat, lng dan alt
    private int getDistance(double lat1, double lat2, double lon1,
                               double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        double result = Math.sqrt(distance);

        int intResult = (int) result;
        // convert decimal into 0.00

        return intResult;
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScanQRCodeActivity.this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(ScanQRCodeActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    private void restartActivity() {
        Intent intent = new Intent(getIntent());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    public static boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }


    private void showSnacMockLoc() {
        Snackbar.make(parentLayout, "Mohon non-aktifkan Fitur Lokasi Tiruan", Snackbar.LENGTH_LONG).setAction("Buka Pengaturan", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
            }
        }).show();
    }

}
