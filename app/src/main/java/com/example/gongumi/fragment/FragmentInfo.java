package com.example.gongumi.fragment;

import android.support.v4.app.Fragment;

public class FragmentInfo {
    private int iconResId;
    private Fragment fragment;

    public FragmentInfo(int iconResId, Fragment fragment) {
        this.iconResId = iconResId;
        this.fragment = fragment;
    }

    public int getIconResId() {
        return iconResId;
    }

    public Fragment getFragment() {
        return fragment;
    }

}
