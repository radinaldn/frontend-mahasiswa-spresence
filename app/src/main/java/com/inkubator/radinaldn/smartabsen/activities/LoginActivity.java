package com.inkubator.radinaldn.smartabsen.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inkubator.radinaldn.smartabsen.R;
import com.inkubator.radinaldn.smartabsen.models.Mahasiswa;
import com.inkubator.radinaldn.smartabsen.responses.ResponseLogin;
import com.inkubator.radinaldn.smartabsen.responses.ResponseUpdateImei;
import com.inkubator.radinaldn.smartabsen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsen.rests.ApiInterface;
import com.inkubator.radinaldn.smartabsen.utils.AbsRuntimePermission;
import com.inkubator.radinaldn.smartabsen.utils.ConnectionDetector;
import com.inkubator.radinaldn.smartabsen.utils.SessionManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by radinaldn on 17/03/18.
 */

public class LoginActivity extends AbsRuntimePermission {
    @BindView(R.id.etnim)
    EditText etnim;

    @BindView(R.id.etpassword)
    EditText etpassword;

    @BindView(R.id.btlogin)
    Button btlogin;

    SessionManager sessionManager;
    ApiInterface apiService;

    String nim, password, imei;
    public final String TAG = LoginActivity.class.getSimpleName();

    ConnectionDetector connectionDetector;
    private static final int REQUEST_PERMISSION = 10;
    private TelephonyManager telephonyManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // do runtime permission
        //request permission here
        requestAppPermissions(new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.RECORD_AUDIO},
                R.string.msg, REQUEST_PERMISSION);

        connectionDetector = new ConnectionDetector(LoginActivity.this);

        //init
        ButterKnife.bind(this);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        sessionManager = new SessionManager(this);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionDetector.isConnectingToInternet()) {
                    loginUser();
                } else {
                    connectionDetector.showNoConnectionDialog(LoginActivity.this);
                }
            }
        });
    }

    @Override
    public void onPermissionGranted(int requestcode) {
        Toast.makeText(getApplicationContext(), R.string.permission_granted, Toast.LENGTH_LONG).show();
    }

    private void loginUser() {
        nim = etnim.getText().toString();
        password = etpassword.getText().toString();
        //imei = "356876057383575";
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            requestAppPermissions(new String[]{
                            Manifest.permission.READ_PHONE_STATE},
                    R.string.msg, REQUEST_PERMISSION);
        }
        imei = telephonyManager.getDeviceId();

        Log.d(TAG, "loginUser: " + nim + " " + password + " " + imei);

        apiService.loginNimAndPassword(nim, password).enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        if (response.body().getData().get(0).getImei().equalsIgnoreCase("0")) {

                            Toast.makeText(getApplicationContext(), R.string.anda_terdeteksi_pertama_kali_menggunakan_app, Toast.LENGTH_SHORT).show();
                            String myNim = response.body().getData().get(0).getNim();

                            apiService.mahasiswaUpdateImei(myNim, imei).enqueue(new Callback<ResponseUpdateImei>() {
                                @Override
                                public void onResponse(Call<ResponseUpdateImei> call, Response<ResponseUpdateImei> response) {
                                    if (response.isSuccessful()) {
                                        if (response.body().getCode().equalsIgnoreCase("200")) {
                                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(), R.string.anda_hanya_bisa_login_menggunakan_device_ini, Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.terjadi_kesalahan), Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseUpdateImei> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.gagal_terhubung_ke_server), Toast.LENGTH_SHORT).show();
                                    t.getLocalizedMessage();
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                t.getLocalizedMessage();
            }
        });

        apiService.login(nim, password, imei).enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Dapat terhubung ke server");
                    Log.d(TAG, "onResponse: " + response.body().getStatus());

                    List<Mahasiswa> mahasiswa = response.body().getData();

                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        sessionManager.createLoginSession(mahasiswa.get(0).getNim(),
                                mahasiswa.get(0).getImei(),
                                mahasiswa.get(0).getPassword(),
                                mahasiswa.get(0).getIdTelegram(),
                                mahasiswa.get(0).getNama(),
                                mahasiswa.get(0).getJk(),
                                mahasiswa.get(0).getFoto(),
                                mahasiswa.get(0).getIdJurusan());

                        Log.d(TAG, "onResponse: Dapat data mahasiswa");

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.berhasil_login), Toast.LENGTH_SHORT).show();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.gagal_login) + response.body().getStatus(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.terjadi_kesalahan), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.gagal_terhubung_ke_server), Toast.LENGTH_LONG).show();
                Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });
    }


}
