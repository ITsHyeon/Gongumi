package com.example.gongumi.activity;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.gongumi.R;
import com.example.gongumi.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    // layout
    private EditText mEtUserId, mEtUserPw;
    private TextView mTvSignUp;
    private Button mBtLogin;

    // firebase
    private DatabaseReference mDatabase;

    // data
    private String userId, userPw, etUserId, etUserPw;
    private ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO : 로그인 Activity, firebase에 넣기

        // 액션바 없애기
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_login);

        // init
        mBtLogin = findViewById(R.id.btnLogin);
        mEtUserId = findViewById(R.id.etUserId);
        mEtUserPw = findViewById(R.id.etUserPw);
        mTvSignUp = findViewById(R.id.textSignUp);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("User");
        users = new ArrayList<>();

        // handler
        mBtLogin.setOnClickListener(loginHandler);
        mTvSignUp.setOnClickListener(signUpHandler);

        mDatabase.addChildEventListener(loginListener);
    }

    View.OnClickListener loginHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            etUserId = mEtUserId.getText().toString().trim();
            etUserPw = mEtUserPw.getText().toString().trim();

            for(User user : users) {
                if (etUserId.equals(user.getId()) && etUserPw.equals(user.getPw())) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"환영합니다. " + user.getName() + "님!", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                } else if (etUserId.equals(user.getId()) && !etUserPw.equals(user.getPw())) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
            Toast.makeText(getApplicationContext(), "존재하지 않는 회원입니다.", Toast.LENGTH_SHORT).show();

        }
    };

    ChildEventListener loginListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            User user = dataSnapshot.getValue(User.class);
            users.add(user);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    View.OnClickListener signUpHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        }
    };
}
