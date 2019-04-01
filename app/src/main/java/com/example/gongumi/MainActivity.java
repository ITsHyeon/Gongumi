package com.example.gongumi;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.gongumi.Fragment.CategoryFragment;
import com.example.gongumi.Fragment.HomeFragment;
import com.example.gongumi.Fragment.PostFragment;
import com.example.gongumi.Fragment.SettingFragment;
import com.example.gongumi.adapter.PagerAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TODO : 피드

        final TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.view_pager);

        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(R.drawable.tab_home_click,  new HomeFragment());
        adapter.addFragment(R.drawable.tab_category, new CategoryFragment());
        adapter.addFragment(R.drawable.tab_write, new PostFragment());
        adapter.addFragment(R.drawable.tab_setting, new SettingFragment());
        mViewPager.setAdapter(adapter);
         mTabLayout.setupWithViewPager(mViewPager);

        for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
            mTabLayout.getTabAt(i).setIcon(adapter.getFragmentInfo(i).getIconResId());
        }

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // TODO : tab의 상태가 선택 상태로 변경.
                int pos = tab.getPosition();
                switch (pos) {
                    case 0:
                        mTabLayout.getTabAt(0).setIcon(R.drawable.tab_home_click);
                        break;
                    case 1:
                        mTabLayout.getTabAt(1).setIcon(R.drawable.tab_category_click);
                        break;
                    case 2:
                        mTabLayout.getTabAt(2).setIcon(R.drawable.tab_write_click);
                        break;
                    case 3:
                        mTabLayout.getTabAt(3).setIcon(R.drawable.tab_setting_click);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // TODO : tab의 상태가 선택되지 않음으로 변경.
                int pos = tab.getPosition();
                switch (pos) {
                    case 0:
                        mTabLayout.getTabAt(0).setIcon(R.drawable.tab_home);
                        break;
                    case 1:
                        mTabLayout.getTabAt(1).setIcon(R.drawable.tab_category);
                        break;
                    case 2:
                        mTabLayout.getTabAt(2).setIcon(R.drawable.tab_write);
                        break;
                    case 3:
                        mTabLayout.getTabAt(3).setIcon(R.drawable.tab_setting);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // TODO : 이미 선택된 tab이 다시
            }
        });


    }


}
