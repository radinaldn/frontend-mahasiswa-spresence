package com.inkubator.radinaldn.smartabsen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Calendar;

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
                goToMainActivity();
            }
        });

        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // set hari yg terpilih

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        System.out.println("hari ini hari index ke-" + day);

        // max index hari terakhir (jumat), jika fragment+=1 maka max+index_day += 1
        int max_index_day = 5;


        TabLayout.Tab tab = tabLayout.getTabAt(day - 2);
        if (day - 1 <= max_index_day && tab != null) {
            tab.select();
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(MengambilActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    private void setupViewPager(ViewPager viewPager) {
        MengambilViewPagerAdapter adapter = new MengambilViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(MengambilFragment.newInstance("Monday"), getResources().getString(R.string.senin));
        adapter.addFragment(MengambilFragment.newInstance("Tuesday"), getResources().getString(R.string.selasa));
        adapter.addFragment(MengambilFragment.newInstance("Wednesday"), getResources().getString(R.string.rabu));
        adapter.addFragment(MengambilFragment.newInstance("Thursday"), getResources().getString(R.string.kamis));
        adapter.addFragment(MengambilFragment.newInstance("Friday"), getResources().getString(R.string.jumat));
        viewPager.setAdapter(adapter);
    }
}
