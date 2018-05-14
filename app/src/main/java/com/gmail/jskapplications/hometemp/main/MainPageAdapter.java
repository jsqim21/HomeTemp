package com.gmail.jskapplications.hometemp.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.gmail.jskapplications.hometemp.main.home.HomeFragment;


/**
 * Created by admin05 on 2016-10-19.
 */

public class MainPageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public MainPageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Fragment tab1 = HomeFragment.newInstance("a","b");
                return tab1;
            case 1:
                Fragment tab2 = HomeFragment.newInstance("a","b");
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
