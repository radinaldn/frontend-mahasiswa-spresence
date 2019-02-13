package com.inkubator.radinaldn.smartabsen.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.test.mock.MockPackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inkubator.radinaldn.smartabsen.R;
import com.inkubator.radinaldn.smartabsen.utils.SessionManager;
import com.instacart.library.truetime.TrueTime;

import org.ankit.gpslibrary.MyTracker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DimanaSayaActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final long BATAS_MAKS_SESSION_LOCATION = 5; // in minutes
    private GoogleMap mMap;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    MyTracker tracker;
    TextView tvLastLoc;
    Button btDimanaSaya, btSimpanLokasi;
    Marker myMarker;
    LatLng latLng;
    String saatIni;
    private ProgressDialog pDialog;
    SessionManager sessionManager;
    boolean canGetLocation = false;
    View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dimana_saya);

        // init true time

        sessionManager = new SessionManager(this);

        btDimanaSaya = findViewById(R.id.btDimanaSaya);
        btSimpanLokasi = findViewById(R.id.btSimpanLokasi);
        tvLastLoc = findViewById(R.id.tvLastLoc);

        btSimpanLokasi.setOnClickListener(this);
        btSimpanLokasi.setEnabled(TrueTime.isInitialized());
        btDimanaSaya.setOnClickListener(this);

        parentLayout = findViewById(android.R.id.content);

        // Do Runtime Permission
        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission, Manifest.permission.READ_PHONE_STATE},
                        REQUEST_CODE_PERMISSION);
            } else {
                // read location
                if (isMockSettingsON(DimanaSayaActivity.this)) {
                    showSnacMockLoc();
                } else {
                    new GetMyLocation().execute();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // print last loc into textview
        if (sessionManager.hasLastLocation()) {
            showSessionLocation();
        }

        // Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = findViewById(R.id.toolbar_dimana_saya);
        toolbar.setTitle(R.string.dimana_saya);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        // click button back pada title bar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });
    }

    private void showSnacMockLoc() {
        Snackbar.make(parentLayout, getResources().getString(R.string.mohon_non_aktifkan_fitur_lokasi_tiruan), Snackbar.LENGTH_LONG).setAction("Buka Pengaturan", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
            }
        }).show();
    }

    private void showSnackAutoDate() {
        Snackbar.make(parentLayout, getResources().getString(R.string.mohon_aktifkan_tanggal_dan_waktu_otomatis), Snackbar.LENGTH_LONG).setAction("Buka Pengaturan", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);

            }
        }).show();
    }

    private void showSessionLocation() {
//        tvLastLoc.setText("Lat : "+sessionManager.getMyLocationDetail().get(SessionManager.LATITUDE)+" Lng : "+sessionManager.getMyLocationDetail().get(SessionManager.LONGITUDE)+"\nUpdate : "+sessionManager.getMyLocationDetail().get(SessionManager.LAST_LOCATED));
        String sessDate = sessionManager.getMyLocationDetail().get(SessionManager.LAST_LOCATED);

        // get cur datetime
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String saatIni = mdformat.format(currentTime);

        long selisih = hitungSelisihMenit(saatIni, sessDate);

        if (selisih > 5) {
            tvLastLoc.setText(getResources().getString(R.string.update) + sessDate + getString(R.string.tidak_berlaku) + BATAS_MAKS_SESSION_LOCATION + getString(R.string.menit));
            tvLastLoc.setTextColor(getResources().getColor(R.color.RedBootstrap));
        } else {
            tvLastLoc.setText(getResources().getString(R.string.update) + sessDate + getString(R.string.berlaku) + BATAS_MAKS_SESSION_LOCATION + getResources().getString(R.string.menit));
            tvLastLoc.setTextColor(getResources().getColor(R.color.GreenBootstrap));
        }

    }

    private void goToMainActivity() {
        Intent intent = new Intent(DimanaSayaActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btDimanaSaya) {
            // located me
            if (isMockSettingsON(this)) {
                showSnacMockLoc();
            } else {
                new GetMyLocation().execute();
            }

        } else if (id == R.id.btSimpanLokasi) {
            // save my current position
            if (isMockSettingsON(this)) {
                showSnacMockLoc();
            } else if (canGetLocation && !isMockSettingsON(this)) {
                if (latLng != null && latLng.latitude != 0) {
                    String strMyLat = String.valueOf(latLng.latitude);
                    String strMyLng = String.valueOf(latLng.longitude);

                    // check apakah waktu di set manual oleh user yang terlalu kreatif

                    // utk android API 17 keatas
                    if (isAutoDateTimeSettingsON(this)) {
                        sessionManager.createMyLocationSession(strMyLat, strMyLng, saatIni);
                        showSessionLocation();
                    } else {
                        showSnackAutoDate();
                    }


                }
            }

        }
    }

    void getLocation() {

        latLng = new LatLng(tracker.getLatitude(), tracker.getLongitude());

        System.out.println(tracker.getLatitude());

        System.out.println(tracker.getLongitude());
        System.out.println(tracker.getLocation());


        if (tracker.canGetLocation() && tracker.getLatitude() != 0) {
            canGetLocation = true;

            // change button simpan state
            btSimpanLokasi.setEnabled(true);
            btSimpanLokasi.setBackgroundColor(getResources().getColor(R.color.HoloOrangeDark));
            btSimpanLokasi.setText(getResources().getString(R.string.simpan_lokasi));

            //Toast.makeText(getApplicationContext(), "Bisa dapat lokasi", Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), "Menggunakan provider : "+tracker.getLocation().getProvider(), Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), "Menggunakan provider : "+tracker.getLocation().getProvider()+"\nmyLat : "+latLng.latitude+"\nmyLng : "+latLng.longitude, Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), "Address : "+tracker.address+"\nCityname : "+tracker.cityName+"\nState : "+tracker.state+"\ncountryName : "+tracker.countryName+"\ncountryCode : "+tracker.countryCode+"\nipAddress : "+tracker.ipAddress+"\nmacAddress : "+tracker.macAddress, Toast.LENGTH_LONG).show();

            latLng = new LatLng(tracker.getLatitude(), tracker.getLongitude());

            if (isAutoDateTimeSettingsON(this)) {
                moveMarker(latLng.latitude, latLng.longitude);
//                Toast.makeText(getApplicationContext(), "Marker dipindahkan dengan : "+tracker.getLocation().getProvider()+"\nmyLat : "+tracker.getLatitude()+"\nmyLng : "+tracker.getLongitude(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.lokasi_ditemukan), Toast.LENGTH_LONG).show();
            } else {
                showSnackAutoDate();
            }


        } else {
            canGetLocation = false;

            // change button simpan state
            btSimpanLokasi.setEnabled(false);
            btSimpanLokasi.setBackgroundColor(getResources().getColor(R.color.buttonColorDisabled));
            btSimpanLokasi.setText(getResources().getString(R.string.membaca_lokasi));

            Toast.makeText(getApplicationContext(), getResources().getString(R.string.tidak_bisa_mendapatkan_lokasi), Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.info));
            builder.setMessage(getResources().getString(R.string.suruh_aplikasi_membaca_lokasi_lagi));
            builder.setPositiveButton(getResources().getString(R.string.ya), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (isMockSettingsON(DimanaSayaActivity.this)) {
                        showSnacMockLoc();
                    } else {
                        new GetMyLocation().execute();
                    }
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

    private void moveMarker(double latitude, double longitude) {

        if (myMarker != null) myMarker.remove();

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        saatIni = mdformat.format(currentTime);

        BitmapDescriptor customIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker);

        myMarker = mMap.addMarker(new
                MarkerOptions().position(new LatLng(latitude, longitude)).title(getResources().getString(R.string.posisi_saya)).snippet("Update : " + saatIni).icon(bitmapDescriptorFromVector(this, R.drawable.ic_map_marker)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

    }

    public static boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }

    public static boolean isAutoDateTimeSettingsON(Context context) {
        // returns true if auto date time settings enabled, false if not enabled.
        if (Build.VERSION.SDK_INT > 16) {
            if (android.provider.Settings.Global.getInt(context.getContentResolver(), android.provider.Settings.Global.AUTO_TIME, 0) == 1) {
                return true;
            } else {
                return false;
            }
        } else {
            if (android.provider.Settings.System.getInt(context.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1) {
                return true;
            } else {
                return false;
            }
        }
    }

    class GetMyLocation extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(DimanaSayaActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage(getResources().getString(R.string.mohon_menunggu_sedang_mengambil_lokasi));
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            if (tracker == null) {
                tracker = new MyTracker(DimanaSayaActivity.this);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            getLocation();
            //Toast.makeText(getApplicationContext(), "Selesai mendapatkan lokasi ", Toast.LENGTH_LONG).show();
            // selesaikan activity

        }

    }

    private long hitungSelisihMenit(String curDate, String sesDate) {
        String sessLastLocated = sessionManager.getMyLocationDetail().get(SessionManager.LAST_LOCATED);

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date firstDate, secondDate;

        long selisihMenit = 0;

        try {
            firstDate = mdformat.parse(sesDate);
            secondDate = mdformat.parse(curDate);

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

        return selisihMenit;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
