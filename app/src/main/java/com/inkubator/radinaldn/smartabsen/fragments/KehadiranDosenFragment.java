package com.inkubator.radinaldn.smartabsen.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import com.inkubator.radinaldn.smartabsen.R;

import com.inkubator.radinaldn.smartabsen.adapters.KehadiranDosenAdapter;
import com.inkubator.radinaldn.smartabsen.models.KehadiranDosen;
import com.inkubator.radinaldn.smartabsen.responses.ResponseKehadiranDosen;

import com.inkubator.radinaldn.smartabsen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsen.rests.ApiInterface;
import com.inkubator.radinaldn.smartabsen.utils.SessionManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by radinaldn on 25/07/18.
 */

public class KehadiranDosenFragment extends Fragment {
    private RecyclerView recyclerView;
    private KehadiranDosenAdapter adapter;
    private ArrayList<KehadiranDosen> kehadiranDosenArrayList;
    private static final String ARG_STATUS= "status";

    ApiInterface apiService;
    SwipeRefreshLayout swipeRefreshLayout;

    public static final String TAG = KehadiranDosenFragment.class.getSimpleName();
    SessionManager sessionManager;
    private String status;

    public static KehadiranDosenFragment newInstance(String status_kehadiran){
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status_kehadiran);

        KehadiranDosenFragment fragment = new KehadiranDosenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public KehadiranDosenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        if( extras != null){
            status = extras.getString(ARG_STATUS);
        }

        sessionManager = new SessionManager(getContext());

        apiService = ApiClient.getClient().create(ApiInterface.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kehadiran_dosen, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);



        swipeRefreshLayout = view.findViewById(R.id.swipe_activity_kehadiran_dosen);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh, R.color.refresh1, R.color.refresh2);
        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                getActivity().startActivity(intent);

            }
        });

        getKehadiranDosen(status);

        return view;
    }

    private void getKehadiranDosen(final String status_kehadiran) {
        Call<ResponseKehadiranDosen> call = apiService.dosenFindAllByStatusKehadiran(status_kehadiran);

        call.enqueue(new Callback<ResponseKehadiranDosen>() {
            @Override
            public void onResponse(Call<ResponseKehadiranDosen> call, Response<ResponseKehadiranDosen> response) {
                if (response.isSuccessful()){
                    if(response.body().getKehadiranDosen().size()>0){
                        kehadiranDosenArrayList = new ArrayList<>();

                        for (int i = 0; i < response.body().getKehadiranDosen().size(); i++) {
                            Log.i(TAG, "onResponse: dosen "+status_kehadiran+ ": "+response.body().getKehadiranDosen().get(i).getNama_dosen());

                            String str_nip = response.body().getKehadiranDosen().get(i).getNip();
                            String str_nama_dosen = response.body().getKehadiranDosen().get(i).getNama_dosen();
                            String str_foto = response.body().getKehadiranDosen().get(i).getFoto();
                            String str_status = response.body().getKehadiranDosen().get(i).getStatus_kehadiran();
                            String str_nama_kota = response.body().getKehadiranDosen().get(i).getNama_kota();
                            String str_last_update = response.body().getKehadiranDosen().get(i).getLast_update();

                            kehadiranDosenArrayList.add(new KehadiranDosen(str_nip, str_nama_dosen, str_foto, str_status, str_nama_kota, str_last_update));

                            adapter = new KehadiranDosenAdapter(getContext(), kehadiranDosenArrayList);

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);
                            swipeRefreshLayout.setRefreshing(false);

                        }
                    } else {
                        Toast.makeText(getContext(), "Data dosen yang "+status_kehadiran+" kosong.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "onResponse error: " +response.errorBody(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseKehadiranDosen> call, Throwable t) {
            Toast.makeText(getContext(), "onFailure : "+t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
