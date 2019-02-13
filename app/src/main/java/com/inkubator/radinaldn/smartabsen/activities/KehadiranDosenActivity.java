package com.inkubator.radinaldn.smartabsen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.inkubator.radinaldn.smartabsen.R;
import com.inkubator.radinaldn.smartabsen.adapters.KehadiranDosenViewPagerAdapter;
import com.inkubator.radinaldn.smartabsen.fragments.KehadiranDosenFragment;
import com.inkubator.radinaldn.smartabsen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsen.rests.ApiInterface;

public class KehadiranDosenActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;

    private static final String TAG = KehadiranDosenActivity.class.getSimpleName();
    ApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kehadiran_dosen);

        apiService = ApiClient.getClient().create(ApiInterface.class);

        Toolbar toolbar = findViewById(R.id.toolbar_kehadiran_dosen);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.viewpager_kehadiran_dosen);
        tabLayout = findViewById(R.id.tabs_kehadiran_dosen);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        KehadiranDosenViewPagerAdapter adapter = new KehadiranDosenViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(KehadiranDosenFragment.newInstance("Hadir"), getResources().getString(R.string.hadir));
        adapter.addFragment(KehadiranDosenFragment.newInstance("Tidak Hadir"), getResources().getString(R.string.tidak_hadir));
        viewPager.setAdapter(adapter);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(KehadiranDosenActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }
}
