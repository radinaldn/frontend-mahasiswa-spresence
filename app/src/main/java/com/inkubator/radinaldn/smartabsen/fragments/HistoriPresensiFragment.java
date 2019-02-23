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

import com.inkubator.radinaldn.smartabsen.R;
import com.inkubator.radinaldn.smartabsen.activities.HistoriPresensiActivity;
import com.inkubator.radinaldn.smartabsen.adapters.HistoriPresensiAdapter;
import com.inkubator.radinaldn.smartabsen.models.PresensiDetail;
import com.inkubator.radinaldn.smartabsen.responses.ResponsePresensiDetail;
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

public class HistoriPresensiFragment extends Fragment {
    private RecyclerView recyclerView;
    private HistoriPresensiAdapter adapter;
    private ArrayList<PresensiDetail> presensiArrayList;
    private static final String ARG_STATUS = "status";
    private static final String TAG_NIM = "nim";
    public static String ID_PRESENSI;

    ApiInterface apiService;
    SwipeRefreshLayout swipeRefreshLayout;

    public static final String TAG = HistoriPresensiFragment.class.getSimpleName();
    SessionManager sessionManager;
    private String status;
    private static long scrollPosition;
    private int tabPosition = 0;

    public static HistoriPresensiFragment newInstance(String id_presensi, String status_kehadiran, long lngScrollPosition) {
        ID_PRESENSI = id_presensi;
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status_kehadiran);

        HistoriPresensiFragment fragment = new HistoriPresensiFragment();
        fragment.setArguments(args);
        scrollPosition = lngScrollPosition;
        return fragment;
    }

    public HistoriPresensiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        if (extras != null) {
            status = extras.getString(ARG_STATUS);
        }

        sessionManager = new SessionManager(getContext());

        apiService = ApiClient.getClient().create(ApiInterface.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_histori_presensi, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        // get tabposition
        if (status.equalsIgnoreCase("Hadir")) {
            tabPosition = 0;
        } else {
            tabPosition = 1;
        }

        swipeRefreshLayout = view.findViewById(R.id.swipe_activity_histori_presensi);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh, R.color.refresh1, R.color.refresh2);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(0, tabPosition);
            }
        });

        getHistoriPresensi(ID_PRESENSI, status);

        return view;
    }

    // this method call activity method
    public void refreshData(long scrollPosition, int tabPosition) {
//        Toast.makeText(getContext(), "Calling refreshData() from adapter", Toast.LENGTH_SHORT).show();
//        recyclerView.setAdapter(null);
//        recyclerView.setLayoutManager(null);
//
//        getHistoriPresensi(ID_PRESENSI, status);
        if (getContext() instanceof HistoriPresensiActivity) {
            ((HistoriPresensiActivity) getActivity()).refreshFragment(scrollPosition, tabPosition);
            System.out.println("scrollPosition : " + scrollPosition);
            System.out.println("intStatus : " + tabPosition);
        }


    }

    private void getHistoriPresensi(String idPresensi, final String status_kehadiran) {
        Call<ResponsePresensiDetail> call = apiService.presensiDetailFindAllMahasiswaByIdPresensiAndStatusKehadiran(idPresensi, status_kehadiran);

        call.enqueue(new Callback<ResponsePresensiDetail>() {
            @Override
            public void onResponse(Call<ResponsePresensiDetail> call, Response<ResponsePresensiDetail> response) {
                if (response.isSuccessful()) {
                    if (response.body().getPresensiDetail().size() > 0) {
                        presensiArrayList = new ArrayList<>();
                        for (int i = 0; i < response.body().getPresensiDetail().size(); i++) {
                            Log.i(TAG, "onResponse: Mahasiswa " + status + ". " + response.body().getPresensiDetail().get(i).getNamaMahasiswa());

                            String id_presensi = response.body().getPresensiDetail().get(i).getIdPresensi();
                            String nim = response.body().getPresensiDetail().get(i).getNim();
                            String nama_mahasiswa = response.body().getPresensiDetail().get(i).getNamaMahasiswa();
                            String status = response.body().getPresensiDetail().get(i).getStatus();
                            String lat = response.body().getPresensiDetail().get(i).getLat();
                            String lng = response.body().getPresensiDetail().get(i).getLng();
                            String waktu = response.body().getPresensiDetail().get(i).getWaktu();
                            String jarak = response.body().getPresensiDetail().get(i).getJarak();
                            String proses = response.body().getPresensiDetail().get(i).getProses();
                            String foto_mahasiswa = response.body().getPresensiDetail().get(i).getFoto_mahasiswa();

                            presensiArrayList.add(new PresensiDetail(id_presensi, nim, nama_mahasiswa, status, lat, lng, waktu, jarak, proses, foto_mahasiswa));


                            adapter = new HistoriPresensiAdapter(presensiArrayList, getContext());

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

                            recyclerView.setLayoutManager(layoutManager);

                            recyclerView.setAdapter(adapter);

                            swipeRefreshLayout.setRefreshing(false);

                        }
                    } else {
//                        Toast.makeText(getContext(), "Data mahasiswa "+status_kehadiran+" kosong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.terjadi_kesalahan), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponsePresensiDetail> call, Throwable t) {
                Toast.makeText(getContext(), getResources().getString(R.string.gagal_terhubung_ke_server), Toast.LENGTH_LONG).show();
            }
        });
    }
}
