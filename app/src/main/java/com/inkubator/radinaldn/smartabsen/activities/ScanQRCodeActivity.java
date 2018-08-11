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

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

                if (location != null) {

                    latittude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());
                    altitude = String.valueOf(location.getAltitude());

                    // jika belum pernah scan
                    if (!alreadyStartScanning) {
                        // status scan update menjadi true
                        alreadyStartScanning =true;
                    }
                }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            String strStatus = "";
            switch (status){
                case LocationProvider.AVAILABLE:
                    strStatus = "tersedia";
                case LocationProvider.OUT_OF_SERVICE:
                    strStatus = "sedang dalam perbaikan";
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    strStatus = "tidak tersedia untuk sementara";
            }

            Toast.makeText(getBaseContext(), provider + " " +strStatus, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getBaseContext(), "Provider: " +provider+ " di-aktifkan", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getBaseContext(), "Provider: " +provider+ " di-nonaktifkan", Toast.LENGTH_SHORT).show();
        }
    }

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

    @Override
    protected void onResume() {
        super.onResume();

        bestProvider = locationManager.getBestProvider(criteria, true);
        if (bestProvider!=null) {
            Log.d("LocationProviders", "Best provider is : " + bestProvider);
        }

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    //@SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "Location permissions are granted!", Toast.LENGTH_SHORT).show();
                            lastLocation = locationManager.getLastKnownLocation(bestProvider);
                            if (lastLocation != null) Log.d("LocationProviders", lastLocation.toString());
                            locationManager.requestLocationUpdates(
                                    // aktifkan ambil dari satelit
                                    // LocationManager.GPS_PROVIDER,
                                    // aktifkan ambil dari BTS
                                    // LocationManager.NETWORK_PROVIDER,
                                    //
                                                   bestProvider,
                                    0,
                                    0,
                                    locationListener
                            );
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
        // end
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
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectionDetector = new ConnectionDetector(ScanQRCodeActivity.this);

        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        scanResultSub = new ArrayList();

        // for location service
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        // start dialog
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang membaca lokasi ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();


        List<String> locationProviders = locationManager.getAllProviders();
        for (String provider : locationProviders){
            Log.d("LocationProviders", provider);
        }

        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);

        // 1. get camera permission from user
        // 2. check if user turn on on the mock location
        if (isMockSettingsON(this)){
            //   a. if (mockLocation()) onBackPressed() with message "You wanna try to cheating dude ? turn off your fake gps and be honest"
            onBackPressed();
            Toast.makeText(getApplicationContext(), "Mock location anda menyala", Toast.LENGTH_LONG).show();
            finish();
        }

        Toast.makeText(getApplicationContext(), "Mulai menunggu 10 detik ke depan", Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 10s
                Toast.makeText(getApplicationContext(), "10 detik berakhir", Toast.LENGTH_SHORT).show();

                //jika sudah menunggu dan tidak null
                if (latittude!=null){
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getApplicationContext(), "lat, lng, alt terbaru berhasil didapatkan, nyalakanScanner()", Toast.LENGTH_SHORT).show();
                    nyalakanScanner(latittude, longitude);
                } else if (latittude==null){
                    // cek apakah lokasi terakhir yg didapatkan != null
                    if (lastLocation != null){
                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getApplicationContext(), "lastLocation berhasil didapatkan, nyalakanScanner()", Toast.LENGTH_SHORT).show();
                        nyalakanScanner(String.valueOf(lastLocation.getLatitude()), String.valueOf(lastLocation.getLongitude()));
                        System.out.println("Latitude dari lastLocation : "+lastLocation.getLatitude());
                        System.out.println("Longitude dari lastLocation : "+lastLocation.getLongitude());
                    } else {
                        Toast.makeText(getApplicationContext(), "lastLocation juga null, coba lagi", Toast.LENGTH_LONG).show();
                     //   onBackPressed();




                    }
                }

            }
        }, 10000);
    }



    public static boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }

}
