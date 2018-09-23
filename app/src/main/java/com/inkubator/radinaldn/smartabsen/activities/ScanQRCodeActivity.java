package com.inkubator.radinaldn.smartabsen.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.inkubator.radinaldn.smartabsen.R;
import com.inkubator.radinaldn.smartabsen.responses.ResponseIsiPresensi;
import com.inkubator.radinaldn.smartabsen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsen.rests.ApiInterface;
import com.inkubator.radinaldn.smartabsen.utils.ConnectionDetector;
import com.inkubator.radinaldn.smartabsen.utils.SessionManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import org.ankit.gpslibrary.MyTracker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by radinaldn on 24/07/18.
 */

public class ScanQRCodeActivity extends AppCompatActivity  {

    MyTracker tracker;
    Double myLat, myLng;
    private ProgressDialog pDialog;

    private static final String TAG_ID_PRESENSI = "id_presensi";
    MaterialBarcodeScanner materialBarcodeScanner;
    private Barcode barcodeResult;
    ApiInterface apiService;

    ProgressDialog progressDialog;
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


    private void nyalakanScanner(final String myLat, final String myLng){

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(getApplicationContext(), "Camera permissions are granted!", Toast.LENGTH_SHORT).show();
                        // permission is granted
                        materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                                .withActivity(ScanQRCodeActivity.this)
                                .withEnableAutoFocus(true)
                                .withBleepEnabled(true)
                                .withBackfacingCamera()
                                .withText("Arahkan kamera ke QR Code ...")
                                .withCenterTracker()
                                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                                    @Override
                                    public void onResult(Barcode barcode) {
                                        barcodeResult = barcode;
//                        Toast.makeText(getApplicationContext(), "latitude saya : "+latittude+"\nHasil Scan : " + barcode.rawValue, Toast.LENGTH_LONG).show();
                                        final String scanResult = barcode.rawValue;
                                        Log.i("TAG", "onLocationChanged: scanResult = "+scanResult);

                                        for (String retval: scanResult.split("#")){
                                            scanResultSub.add(retval);
                                            Log.i("TAG", "onLocationChanged: scanResultSub = "+retval);
                                        }

                                        double doubleDosenLat = Double.parseDouble(scanResultSub.get(1).toString());
                                        double doubleDosenLng = Double.parseDouble(scanResultSub.get(2).toString());
                                        double doubleMyLat = Double.parseDouble(myLat);
                                        double doubleMyLng = Double.parseDouble(myLng);

                                        // hitung jarak
                                        String jarak = getDistance(doubleDosenLat, doubleMyLat, doubleDosenLng, doubleMyLng, 0, 0);
                                        Log.i("ScanQRCo", "Jarak : "+jarak);

                                        String id_presensi = scanResultSub.get(0).toString();
                                        String nim = sessionManager.getMahasiswaDetail().get(TAG_NIM);
                                        String lat = myLat;
                                        String lng = myLng;
                                        String alt = altitude;

                                        if (connectionDetector.isConnectingToInternet()) {
                                            isiPresensi(id_presensi, nim, lat, lng, jarak);
                                        } else {

                                            finish();
                                            Toast.makeText(getApplicationContext(), "Gagal melakukan presensi, pastikan koneksi internet menyala dan coba lagi.", Toast.LENGTH_SHORT).show();
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
    private void isiPresensi(final String id_presensi, String nim, String lat, String lng, String jarak) {
        apiService.isiPresensi(id_presensi, nim, lat, lng, jarak).enqueue(new Callback<ResponseIsiPresensi>() {
            @Override
            public void onResponse(Call<ResponseIsiPresensi> call, Response<ResponseIsiPresensi> response) {
                Log.d("isiPresensi", "onResponse: "+response.body().toString());
                if(response.isSuccessful()){

                    /*
                     Pada proses memindai QR Code terjadi beberapa case flow.
                     1. Sukses (200), data berhasil masuk
                     2. Forbidden (403), presensi closed/tidak terdaftar di matakuliah tsb/presensi sudah dilakukan
                     3. Not Found (404)
                     4. Error (500)
                     */

                    // jika Response 200 dan 403 Already
                    if(response.body().getCode().equalsIgnoreCase("200")&&response.body().getStatus().equalsIgnoreCase("OK") || response.body().getCode().equalsIgnoreCase("403") && response.body().getStatus().equalsIgnoreCase("Already")){ // jika berhasil isi presensi
                        Intent intent = new Intent(ScanQRCodeActivity.this, HistoriPresensiActivity.class);
                        Toast.makeText(ScanQRCodeActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        intent.putExtra(TAG_ID_PRESENSI, id_presensi);
                        startActivity(intent);
                        finish();
                    } else {

                        Toast.makeText(getApplicationContext(), "onResponse error\n['code'] : "+response.body().getStatus()+"\n['message'] : "+response.body().getMessage(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ScanQRCodeActivity.this, MainActivity.class);
                        Toast.makeText(ScanQRCodeActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        //intent.putExtra(TAG_ID_PRESENSI, id_presensi);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "onResponse isNotSuccesful : "+response.errorBody(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseIsiPresensi> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "onFailure : "+t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // method menghitung jarak antara titik lat, lng dan alt
    private String getDistance(double lat1, double lat2, double lon1,
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
        // convert decimal into 0.00
        DecimalFormat format = new DecimalFormat("0.00");
        String formattedResult = format.format(result);

        return formattedResult;
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tracker=new MyTracker(this);
        connectionDetector = new ConnectionDetector(ScanQRCodeActivity.this);

        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        scanResultSub = new ArrayList();

        // for location service

        // 1. get camera permission from user
        // 2. check if user turn on on the mock location
        if (isMockSettingsON(this)){
            //   a. if (mockLocation()) onBackPressed() with message "You wanna try to cheating dude ? turn off your fake gps and be honest"
            onBackPressed();
            Toast.makeText(getApplicationContext(), "Harap matikan fitur mock location anda.\nAplikasi tidak mengizinkan pemindaian qr-code bila terdeteksi menggunakan lokasi palsu.", Toast.LENGTH_LONG).show();
            finish();
        }

        // Dapatkan lokasi dan nyalakan scanner
        new GetMyLocation().execute();
    }

    void getLocation(){

        myLat = tracker.getLatitude();
        myLng = tracker.getLongitude();

        System.out.println(tracker.getLatitude());

        System.out.println(tracker.getLongitude());
        System.out.println(tracker.getLocation());

        String latlng = myLat+","+myLng;
        System.out.println("myLat : "+myLat);
        System.out.println("myLng : "+myLng);

        nyalakanScanner(String.valueOf(myLat), String.valueOf(myLng));
    }

    public static boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }

    class GetMyLocation extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(ScanQRCodeActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage("Mohon menunggu, sedang mengambil lokasi..");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            if (tracker==null){
                tracker = new MyTracker(ScanQRCodeActivity.this);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            getLocation();
            Toast.makeText(getApplicationContext(), "Selesai mendapatkan lokasi ", Toast.LENGTH_LONG).show();
            // selesaikan activity

        }

    }

}
