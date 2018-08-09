package com.inkubator.radinaldn.smartabsen.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by radinaldn on 25/07/18.
 */

public class HistoriPresensiViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmenTitleList = new ArrayList<>();

    public HistoriPresensiViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmenTitleList.size();
    }

    public void addFragment(Fragment fragment, String title){
        mFragmentList.add(fragment);
        mFragmenTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int posistion){
        return mFragmenTitleList.get(posistion);
    }
}
