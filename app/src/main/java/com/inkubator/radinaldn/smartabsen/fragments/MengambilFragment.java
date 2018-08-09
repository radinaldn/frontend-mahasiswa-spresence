package com.inkubator.radinaldn.smartabsen.fragments;

import android.os.Bundle;
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
import com.inkubator.radinaldn.smartabsen.adapters.MengambilAdapter;
import com.inkubator.radinaldn.smartabsen.models.Mengambil;
import com.inkubator.radinaldn.smartabsen.responses.ResponseMengambil;
import com.inkubator.radinaldn.smartabsen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsen.rests.ApiInterface;
import com.inkubator.radinaldn.smartabsen.utils.SessionManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by radinaldn on 24/07/18.
 */


public class MengambilFragment extends Fragment{
    private RecyclerView recyclerView;
    private MengambilAdapter adapter;
    private ArrayList<Mengambil> mengambilArrayList;

    SwipeRefreshLayout swipeRefreshLayout;

    ApiInterface apiService;

    public static final String TAG_NIM = "nim";
    public static final String TAG = MengambilFragment.class.getSimpleName();
    SessionManager sessionManager;
    private String nim;
    private String dayname;
    private static final String ARG_DAYNAME = "dayname";

    public static MengambilFragment newInstance(String dayname) {
        Bundle args = new Bundle();
        args.putString(ARG_DAYNAME, dayname);

        MengambilFragment fragment = new MengambilFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MengambilFragment() {
        // Required empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        if (extras != null){
            dayname = extras.getString(ARG_DAYNAME);
        }

        sessionManager = new SessionManager(getContext());

        nim = sessionManager.getMahasiswaDetail().get(TAG_NIM);

        apiService = ApiClient.getClient().create(ApiInterface.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mengambil, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        swipeRefreshLayout = view.findViewById(R.id.swipe_fragmen_mengambil);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh, R.color.refresh1, R.color.refresh2);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMengambil(nim, dayname);
            }
        });

        getMengambil(nim, dayname);

        return view;
    }

    private void getMengambil(String nim, final String dayname) {
        Call<ResponseMengambil> call = apiService.mengambilFindAllByNimAndDayname(nim, dayname);

        call.enqueue(new Callback<ResponseMengambil>() {
            @Override
            public void onResponse(Call<ResponseMengambil> call, Response<ResponseMengambil> response) {
                if(response.isSuccessful()){
                    if (response.body().getMengambil().size()>0){
                        mengambilArrayList = new ArrayList<>();
                        for (int i = 0; i <response.body().getMengambil().size() ; i++) {
                            Log.i(TAG, "onResponse: ada matakuliah hari "+dayname+" : "+response.body().getMengambil().get(i).getNamaMatakuliah());

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

                            adapter = new MengambilAdapter(mengambilArrayList, getContext());

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);
                            swipeRefreshLayout.setRefreshing(false);

                        }
                    } else {
                        Log.i(TAG, "onResponse: tidak ada matakuliah hari "+dayname );
                    }
                } else {
                    Log.e(TAG, "onResponse erro : "+response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseMengambil> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


}
