package com.example.gongumi.fragment;

import android.support.v4.app.Fragment;

public class FragmentInfo {
    private int iconResId;
    private String title;
    private Fragment fragment;

    public FragmentInfo(int iconResId, String title, Fragment fragment) {
        this.iconResId = iconResId;
        this.title = title;
        this.fragment = fragment;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getTitle() {return  title;}

    public Fragment getFragment() {
        return fragment;
    }

}
