package com.inkubator.radinaldn.smartabsen.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.inkubator.radinaldn.smartabsen.R;
import com.inkubator.radinaldn.smartabsen.adapters.MengambilAdapter;
import com.inkubator.radinaldn.smartabsen.adapters.MengambilViewPagerAdapter;
import com.inkubator.radinaldn.smartabsen.fragments.MengambilFragment;
import com.inkubator.radinaldn.smartabsen.models.Mengambil;
import com.inkubator.radinaldn.smartabsen.rests.ApiInterface;

import java.util.ArrayList;

public class MengambilActivity extends AppCompatActivity {

    private static final String TAG_ID_MENGAJAR = "id_mengajar";
    private static final String TAG = MengambilActivity.class.getSimpleName();
    private static String ID_MENGAJAR;

    SwipeRefreshLayout swipeRefreshLayout;
    ApiInterface apiService;

    private RecyclerView recyclerView;
    private MengambilAdapter adapter;
    private ArrayList<Mengambil> mengambilArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mengambil);

        Toolbar toolbar = findViewById(R.id.toolbar_mengambil);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        

    }

    private void setupViewPager(ViewPager viewPager) {
        MengambilViewPagerAdapter adapter = new MengambilViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(MengambilFragment.newInstance("Monday"), "Senin");
        adapter.addFragment(MengambilFragment.newInstance("Tuesday"), "Selasa");
        adapter.addFragment(MengambilFragment.newInstance("Wednesday"), "Rabu");
        adapter.addFragment(MengambilFragment.newInstance("Thursday"), "Kamis");
        adapter.addFragment(MengambilFragment.newInstance("Friday"), "Jumat");
        viewPager.setAdapter(adapter);
    }
}
