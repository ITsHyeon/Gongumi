package com.example.gongumi.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.gongumi.R;
import com.example.gongumi.fragment.CategoryFragment;
import com.example.gongumi.fragment.HomeFragment;
import com.example.gongumi.fragment.PostCategoryFragment;
import com.example.gongumi.fragment.PostFragment;
import com.example.gongumi.fragment.PostNumberFragment;
import com.example.gongumi.fragment.PostTermFragment;
import com.example.gongumi.fragment.SettingFragment;
import com.example.gongumi.adapter.PagerAdapter;
import com.example.gongumi.model.Post;

import static com.example.gongumi.fragment.PostFragment.post_pos;

public class MainActivity extends AppCompatActivity {
    private static RelativeLayout layout_toolbar, layout_toolbar_post;
    private Button btn_previous, btn_next;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private static int pos;

    public Post post = new Post(); // fragment와 통신을 해야하기 때문에 public

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO : 피드

        // 액션바 없애기
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        layout_toolbar = findViewById(R.id.layout_toolbar);
        layout_toolbar_post = findViewById(R.id.layout_toolbar_post);
        btn_previous = findViewById(R.id.btn_previous);
        btn_next = findViewById(R.id.btn_next);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

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
                pos = tab.getPosition();
                changeToolbar();
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

        btn_previous.setOnClickListener(PostClickListener);
        btn_next.setOnClickListener(PostClickListener);

    }

    public static void changeToolbar() {
        if(pos == 2) {
            layout_toolbar_post.setVisibility(View.VISIBLE);
            layout_toolbar.setVisibility(View.GONE);
        } else {
            layout_toolbar.setVisibility(View.VISIBLE);
            layout_toolbar_post.setVisibility(View.GONE);
        }
    }

    View.OnClickListener PostClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (v.getId() == R.id.btn_previous) {
                Log.d("test", post_pos + "");
                switch (post_pos) {
                    case 1:
                        mTabLayout.setScrollPosition(0, 0f, true);
                        mViewPager.setCurrentItem(0);
                        post_pos = 0;
                        post = new Post();
                        break;
                    case 2:
                        transaction.replace(R.id.frame_post, PostCategoryFragment.newInstance(post));
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case 3:
                        PostNumberFragment fragment = (PostNumberFragment) fragmentManager.findFragmentById(R.id.frame_post);
                        fragment.checkTextPrevious();
                        transaction.replace(R.id.frame_post, PostTermFragment.newInstance(post));
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case 4:
                        transaction.replace(R.id.frame_post, PostNumberFragment.newInstance(post));
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                }
            }
            else {
                switch (post_pos) {
                    case 1:
                        Log.d("test", post.getCategory() + "");
                        if(post.getCategory() == null) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alert.setMessage("카테고리를 선택해주세요");
                            alert.show();
                        }
                        else {
                            transaction.replace(R.id.frame_post, PostTermFragment.newInstance(post));
                            transaction.commit();
                        }
                        break;
                    case 2:
                        Log.d("test", post.getEndDay().toString());
                        transaction.replace(R.id.frame_post, PostNumberFragment.newInstance(post));
                        transaction.commit();
                        break;
                    case 3:
                        PostNumberFragment fragment = (PostNumberFragment) fragmentManager.findFragmentById(R.id.frame_post);
                        if(fragment.checkText()) {
                            transaction.replace(R.id.frame_post, PostFragment.newInstance(post));
                            transaction.commit();
                        }
                        break;
                    case 4:
                        mTabLayout.setScrollPosition(0, 0f, true);
                        mViewPager.setCurrentItem(0);
                        post_pos = 0;
                        post = new Post();
                        break;
                }
            }
        }
    };

}
