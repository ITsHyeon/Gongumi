package com.example.gongumi.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.gongumi.fragment.FragmentInfo;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentPagerAdapter {
    private final List<FragmentInfo> mFragmentList = new ArrayList<>();
    private static int PAGE_NUMBER=4;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(int iconResId, Fragment fragment) {
        FragmentInfo info = new FragmentInfo(iconResId, fragment);
        mFragmentList.add(info);
    }

    public FragmentInfo getFragmentInfo(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return PAGE_NUMBER;
    }


}
