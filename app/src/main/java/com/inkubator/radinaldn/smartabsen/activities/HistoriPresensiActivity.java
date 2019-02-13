package com.inkubator.radinaldn.smartabsen.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.inkubator.radinaldn.smartabsen.R;
import com.inkubator.radinaldn.smartabsen.adapters.HistoriPresensiViewPagerAdapter;
import com.inkubator.radinaldn.smartabsen.fragments.HistoriPresensiFragment;
import com.inkubator.radinaldn.smartabsen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsen.rests.ApiInterface;

public class HistoriPresensiActivity extends AppCompatActivity {
    public String ID_PRESENSI, NAMA_MATAKULIAH, PERTEMUAN;
    private static final String TAG_ID_PRESENSI = "id_presensi";
    private static final String TAG_NAMA_MATAKULIAH = "nama_matakuliah";
    private static final String TAG_PERTEMUAN = "pertemuan";

    ViewPager viewPager;
    TabLayout tabLayout;

    private static final String TAG = HistoriPresensiActivity.class.getSimpleName();
    ApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histori_presensi);

        //get Extra from previous Activity
        ID_PRESENSI = getIntent().getStringExtra(TAG_ID_PRESENSI);
        NAMA_MATAKULIAH = getIntent().getStringExtra(TAG_NAMA_MATAKULIAH);
        PERTEMUAN = getIntent().getStringExtra(TAG_PERTEMUAN);

        apiService = ApiClient.getClient().create(ApiInterface.class);

        Toolbar toolbar = findViewById(R.id.toolbar_histori_presensi);
        toolbar.setTitle(PERTEMUAN);
        toolbar.setSubtitle(NAMA_MATAKULIAH);
        setSupportActionBar(toolbar);


        // action click tombol back
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.viewpager_histori_presensi);
        tabLayout = findViewById(R.id.tabs_histori_presensi);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);


    }

    private void setupViewPager(ViewPager viewPager) {
        HistoriPresensiViewPagerAdapter adapter = new HistoriPresensiViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(HistoriPresensiFragment.newInstance(ID_PRESENSI, "Hadir"), getResources().getString(R.string.hadir));
        adapter.addFragment(HistoriPresensiFragment.newInstance(ID_PRESENSI, "Tidak Hadir"), getResources().getString(R.string.tidak_hadir));
        viewPager.setAdapter(adapter);
    }
}
