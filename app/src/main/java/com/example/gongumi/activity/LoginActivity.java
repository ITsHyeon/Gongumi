package com.example.gongumi.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    // layout
    private EditText mEtUserId, mEtUserPw;
    private TextView mTvSignUp;
    private Button mBtLogin;

    // firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // data
    private String userId, userPw, etUserId, etUserPw;
    private ArrayList<User> users;

    private User user;

    //permission
    private final int PERMISSIONS_REQUEST_CODE = 100;
    private String REQUIRED_PERMISSIONS[] = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO : 로그인 Activity, firebase에 넣기

        // 액션바 없애기
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_login);

        checkPermission();

        // init
        mBtLogin = findViewById(R.id.btnLogin);
        mEtUserId = findViewById(R.id.etUserId);
        mEtUserPw = findViewById(R.id.etUserPw);
        mTvSignUp = findViewById(R.id.textSignUp);

        mAuth = FirebaseAuth.getInstance();
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

            signIn(etUserId, etUserPw);

//            for(User user : users) {
//                if (etUserId.equals(user.getId()) && etUserPw.equals(user.getPw())) {
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    intent.putExtra("user", user);
//                    startActivity(intent);
//                    Toast.makeText(getApplicationContext(),"환영합니다. " + user.getName() + "님!", Toast.LENGTH_LONG).show();
//                    finish();
//                    return;
//                } else if (etUserId.equals(user.getId()) && !etUserPw.equals(user.getPw())) {
//                    Toast.makeText(getApplicationContext(), "비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//            }
//            Toast.makeText(getApplicationContext(), "존재하지 않는 회원입니다.", Toast.LENGTH_SHORT).show();

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

    public void checkUsers(String email, boolean isSuccess) {
        for(User user : users) {
            if (isSuccess && user.getId().equals(email)) {
                this.user = user;
                Toast.makeText(getApplicationContext(),"환영합니다. " + user.getName() + "님!", Toast.LENGTH_LONG).show();
                return;
            }
            else if(user.getId().equals(email)) {
                Toast.makeText(getApplicationContext(), "비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(getApplicationContext(), "존재하지 않는 회원입니다.", Toast.LENGTH_SHORT).show();
    }

    public void checkPermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int hasWriteExternalStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // 퍼미션이 있는 경우
        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED && hasWriteExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else {
            // 퍼미션을 거부한 적이 있는 경우 설명 후 권한 요청
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[2])) {
                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LoginActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                    }
                });
                alert.setMessage("이 앱을 실행하려면 GPS와 외부 저장소 접근 권한이 필요합니다.");
                alert.setCancelable(false);
                alert.show();
            }
            // 퍼미션을 거부한 적 없는 경우 바로 권한 요청
            else {
                ActivityCompat.requestPermissions(LoginActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    } // checkPermission

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) {
            boolean check_result = true;

            for(int result : grantResults) {
                if(result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            // 모든 퍼미션을 허용한 경우
            if(check_result) {
                return;
            }
            else {
                // 거부한 경우
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[2])) {
                    Toast.makeText(this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_SHORT).show();
                }
                // 다시 묻지 않음을 체크한 경우 -> 설정(앱 정보)에서 퍼미션을 허용해야 앱을 실행할 수 있음
                else {
                    Toast.makeText(this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해주세요", Toast.LENGTH_LONG).show();

                }
                finishAffinity();
            }

        }
    }

    public void signIn(final String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("firebaseAuthSignIn", "signInWithEmail:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            checkUsers(email.substring(0, email.indexOf("@")), true);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("user", user);
                            intent.putExtra("firebaseUser", firebaseUser);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("firebaseAuthSignIn", "signInWithEmail:failure", task.getException());
                            checkUsers(email.substring(0, email.indexOf("@")), false);
                        }
                    }
                });
    }
}