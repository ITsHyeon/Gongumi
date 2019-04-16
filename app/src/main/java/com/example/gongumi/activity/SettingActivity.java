package com.example.gongumi.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gongumi.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private TextView mTvName, mTvAdress;
    private LinearLayout mLiPostList, mLiJoinList, mLiLogOut;
    private CircleImageView mCiChangeProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // TODO : 설정 Activity
        mTvName = findViewById(R.id.tvName);
        mTvAdress = findViewById(R.id.tvAdress);
        mLiPostList = findViewById(R.id.liPostList);
        mLiJoinList = findViewById(R.id.liJoinList);
        mLiLogOut = findViewById(R.id.liLogOut);
        mCiChangeProfile = findViewById(R.id.ciChangeProfile);



    }
}
