package com.example.gongumi.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gongumi.R;
import com.example.gongumi.custom.CustomDialog;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    // layout
    private TextView mTvName, mTvAdress;
    private LinearLayout mLiPostList, mLiJoinList, mLiLogOut;
    private CircleImageView mCiChangeProfile;

    // firebase
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // TODO : 로그인 된 사람의 이름, 주소, 프로필 이미지 가져오기
        // TODO : Dialog에 입력된 값으로 변경해주기
        // TODO : 프로필 이미지 변경, 로그아웃
        mTvName = findViewById(R.id.tvName);
        mTvAdress = findViewById(R.id.tvAdress);
        mLiPostList = findViewById(R.id.liPostList);
        mLiJoinList = findViewById(R.id.liJoinList);
        mLiLogOut = findViewById(R.id.liLogOut);

        mCiChangeProfile = findViewById(R.id.ciChangeProfile);
        Button mBtChangeName = findViewById(R.id.btChangeName);
        Button mBtChangeAdress = findViewById(R.id.btChangeAdress);

        // TODO : 변경을 눌렀을 때 변경사항에 따라 다른 제목의 Dialog 띄우기
        mBtChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 커스텀 다이얼로그를 생성한다.
                CustomDialog customDialog = new CustomDialog(SettingActivity.this);

                // 커스텀 다이얼로그를 호출한다
                // 커스텀 다이얼로그의 결과를 출력할 Text
            }
        });


    }
}
