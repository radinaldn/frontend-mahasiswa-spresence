package com.inkubator.radinaldn.smartabsen.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inkubator.radinaldn.smartabsen.R;
import com.inkubator.radinaldn.smartabsen.models.Mahasiswa;
import com.inkubator.radinaldn.smartabsen.responses.ResponseLogin;
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

public class LoginActivity extends AbsRuntimePermission{
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
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                R.string.msg,REQUEST_PERMISSION);

        connectionDetector = new ConnectionDetector(LoginActivity.this);

        //init
        ButterKnife.bind(this);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        sessionManager = new SessionManager(this);

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
        Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_LONG).show();
    }

    private void loginUser() {
        nim = etnim.getText().toString();
        password = etpassword.getText().toString();
        imei = "356876057383575";

        Log.d(TAG, "loginUser: " +nim+" "+password+" "+imei);

        apiService.login(nim, password, imei).enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                if (response.isSuccessful()){
                    Log.d(TAG, "onResponse: Dapat terhubung ke server");
                    Log.d(TAG, "onResponse: " +response.body().getStatus());

                    List<Mahasiswa> mahasiswa = response.body().getData();

                    if(response.body().getStatus().equalsIgnoreCase("success")){
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
                        Toast.makeText(LoginActivity.this, "Berhasil login", Toast.LENGTH_LONG).show();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), "Gagal login mahasiswa :"+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Gagal konek ke server", Toast.LENGTH_LONG).show();
                Log.e(TAG, "onFailure: "+ t.getLocalizedMessage());
            }
        });
    }
}
