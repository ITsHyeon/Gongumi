package com.example.gongumi.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.gongumi.R;
import com.example.gongumi.model.User;
import com.example.gongumi.service.GpsTracker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    // layout
    private RelativeLayout layout;
    private EditText currentEdit;
    private EditText edit_email, edit_pw, edit_name;
    private CircleImageView circleImageView_profile;
    private Button btn_profile, btn_back, btn_gps, btn_gps_cancel, btn_login;

    // firebase
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;

    private User user;
    private ArrayList<User> list_user;

    // profile
    private Uri photoUri = null;
    private static final int PROFILE_PHOTO_REQUEST_CODE = 10;

    // gps
    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] GPS_REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO : 회원가입 Activity

        // 액션바 없애기
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_sign_up);

        // firebase
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User");
        list_user = new ArrayList<>();
        getDatabase();

        // TODO : 채팅

        // layout
        layout = findViewById(R.id.layout_sign_up);
        edit_email = findViewById(R.id.edit_email);
        edit_pw = findViewById(R.id.edit_pw);
        edit_name = findViewById(R.id.edit_name);
        circleImageView_profile = findViewById(R.id.circleviewimage_profile);
        btn_profile = findViewById(R.id.btn_profile);
        btn_back = findViewById(R.id.btn_back);
        btn_gps = findViewById(R.id.btn_gps);
        btn_gps_cancel = findViewById(R.id.btn_gps_cancel);
        btn_login = findViewById(R.id.btn_login);

        currentEdit = edit_email;

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidekeyboard(currentEdit);
            }
        });

        edit_email.setOnFocusChangeListener(EditFocusChangeListener);
        edit_email.setOnKeyListener(EditKeyListener);

        edit_pw.setOnFocusChangeListener(EditFocusChangeListener);
        edit_pw.setOnKeyListener(EditKeyListener);

        edit_name.setOnFocusChangeListener(EditFocusChangeListener);
        edit_name.setOnKeyListener(EditKeyListener);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        circleImageView_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProfile();
            }
        });

        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProfile();
            }
        });

        btn_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkLocationServicesStatus()) {
                    showDialogForLocationServiceSetting();
                }else {
                    checkRunTimePermission();
                }

                getGPS();
            }
        });

        btn_gps_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SignUpActivity.this);
                alert.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getGPS();
                    }
                });
                alert.setMessage("GPS 정보를 다시 받아오시겠습니까?");
                alert.show();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edit_email.getText().toString().trim();
                String pw = edit_pw.getText().toString().trim();
                String name = edit_name.getText().toString().trim();
                String location = btn_gps.getText().toString().trim();

                checkingUser(email, pw, name, location);
            }
        });

    }

    public void hidekeyboard(EditText e) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(e.getWindowToken(), 0);
        changeEditBackground(e);
    }

    public void changeEditBackground(EditText e) {
        if(!(e.getText().toString().trim().equals("")))
            e.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.custom_input_checked_button_gray));
    }

    View.OnFocusChangeListener EditFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus) {
                switch (v.getId()) {
                    case R.id.edit_email:
                        Log.d("current", "edit_email");
                        if(currentEdit != edit_email)
                            changeEditBackground(currentEdit);
                        edit_email.setBackground(getApplication().getResources().getDrawable(R.drawable.custom_input_button_gray));
                        currentEdit = edit_email;
                        break;
                    case R.id.edit_pw:
                        Log.d("current", "edit_pw");
                        if(currentEdit != edit_pw)
                            changeEditBackground(currentEdit);
                        edit_pw.setBackground(getApplication().getResources().getDrawable(R.drawable.custom_input_button_gray));
                        currentEdit = edit_pw;
                        break;
                    case R.id.edit_name:
                        Log.d("current", "edit_name");
                        if(currentEdit != edit_name)
                            changeEditBackground(currentEdit);
                        edit_name.setBackground(getApplication().getResources().getDrawable(R.drawable.custom_input_button_gray));
                        currentEdit = edit_name;
                        break;
                }
            }
        }
    };

    View.OnKeyListener EditKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                switch (v.getId()) {
                    case R.id.edit_email:
                        hidekeyboard(edit_email);
                        break;
                    case R.id.edit_pw:
                        hidekeyboard(edit_pw);
                        break;
                    case R.id.edit_name:
                        hidekeyboard(edit_name);
                        edit_name.clearFocus();
                        break;
                }
                return true;
            } else {
                switch (v.getId()) {
                    case R.id.edit_email:
                        edit_email.setBackground(getApplication().getResources().getDrawable(R.drawable.custom_input_button_gray));
                        break;
                    case R.id.edit_pw:
                        edit_pw.setBackground(getApplication().getResources().getDrawable(R.drawable.custom_input_button_gray));
                        break;
                    case R.id.edit_name:
                        edit_name.setBackground(getApplication().getResources().getDrawable(R.drawable.custom_input_button_gray));
                        break;
                }
            }
            return false;
        }
    };


    public void getProfile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Get Album"), PROFILE_PHOTO_REQUEST_CODE);
    }

    // 프로필 사진 스토리지에 올리기
    public void uploadProfilePhoto() {
        mStorageRef = FirebaseStorage.getInstance().getReference().child("user_profile/" + user.getId() + ".jpg");
        if(photoUri != null) {
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .build();

            UploadTask uploadTask = mStorageRef.putFile(photoUri, metadata);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Toast.makeText(SignUpActivity.this, "성공", Toast.LENGTH_SHORT).show();
                    Log.d("프로필 사진 업로드", "성공");
                }
            });
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(SignUpActivity.this, "실패", Toast.LENGTH_SHORT).show();
                    Log.e("프로필 사진 업로드", "실패");
                }
            });
        }
    } // uploadProfilePhoto()

    public static int dpToPx(Context context, int dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float)dpValue * density);
    }

    // GPS 가져오기
    public void getGPS() {
        gpsTracker = new GpsTracker(SignUpActivity.this);

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        String address = getCurrentAddress(latitude, longitude);

        if(!address.equals("GPS")) {
            btn_gps.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            btn_gps.setPadding(dpToPx(SignUpActivity.this, 15), 0, 0, 0);
            btn_gps.setBackground(SignUpActivity.this.getResources().getDrawable(R.drawable.custom_click_checked_button_yellow));
            btn_gps_cancel.setVisibility(View.VISIBLE);
        }
        btn_gps.setText(address);

//        Toast.makeText(SignUpActivity.this, "현재위치 \n위도 " + latitude + "\n경도 " + longitude, Toast.LENGTH_LONG).show();
    } // getGPS()


    // 퍼미션 결과
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == GPS_REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if(!check_result) { // 퍼미션을 거부한 경우
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, GPS_REQUIRED_PERMISSIONS[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this, GPS_REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(SignUpActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finishAffinity();

                }else {
                    Toast.makeText(SignUpActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                    finishAffinity();
                }
            } else {

            }
        } else {
            boolean check_result = true;

            if (grandResults[0] != PackageManager.PERMISSION_GRANTED) {
                check_result = false;
            }

            if(!check_result) { // 퍼미션을 거부한 경우
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, GPS_REQUIRED_PERMISSIONS[0])) {
                    Toast.makeText(SignUpActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finishAffinity();

                }else {
                    Toast.makeText(SignUpActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                    finishAffinity();
                }
            }
        }
    } // onRequestPermissionsResult()

    // GPS 퍼미션 확인
    void checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

        // 2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
        if (!(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED)) {
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivity.this, GPS_REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(SignUpActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(SignUpActivity.this, GPS_REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(SignUpActivity.this, GPS_REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    } // checkRunTimePermission()

    // GPS 주소 변환
    public String getCurrentAddress( double latitude, double longitude) {
        // Geocoder : GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "네트워크가 있는 곳에서 다시 실행해주세요", Toast.LENGTH_LONG).show();
            return "GPS";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잠시 후 다시 실행해주세요", Toast.LENGTH_LONG).show();
            return "GPS";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "GPS를 인식하지 못했습니다. 잠시 후 다시 실행해주세요.", Toast.LENGTH_LONG).show();
            return "GPS";
        }

//        Toast.makeText(SignUpActivity.this, addresses.get(0).toString(), Toast.LENGTH_LONG).show();

        Address address = addresses.get(0);
        return address.getAddressLine(0).substring(address.getAddressLine(0).indexOf("시") + 1, address.getAddressLine(0).indexOf("구") + 1).trim() + " " + address.getThoroughfare();
    } // getCurrentAddress()


    // 여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 GPS 서비스가 필요합니다.\n" + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    } // showDialogForLocationServiceSetting()


    // GPS 활성화 결과
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
            case PROFILE_PHOTO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        // 선택한 이미지에서 비트맵 생성
                        photoUri = data.getData();
                        Log.d("photoUri", photoUri.toString());
//                        Toast.makeText(SignUpActivity.this, photoUri.toString(), Toast.LENGTH_LONG).show();
                        Bitmap img = MediaStore.Images.Media.getBitmap(SignUpActivity.this.getContentResolver(), photoUri);

                        // 이미지 표시
                        Glide.with(getApplicationContext()).asBitmap().load(img)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                        circleImageView_profile.setImageBitmap(resource);
                                        circleImageView_profile.setBackground(SignUpActivity.this.getResources().getDrawable(R.drawable.custom_profile_photo_gray));
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(SignUpActivity.this, "사진을 가져오지 못했습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                    }

                }
        }
    } // onActivityResult()

    // GPS 서비스 상태 확인
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    } // checkLocationServicesStatus()


    // 데이터베이스 가져오기
    public void getDatabase() {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                list_user.add(user);
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
        }; // ChildEventListener

        mDatabaseRef.addChildEventListener(childEventListener);
    } // getDatabase()

    // 유효성 검사
    public void checkingUser(String email, String pw, String name, String loc) {
        String toastText = "";
        int count = 0;

        if(email.equals("EMAIL") || email.equals("")) {
            toastText += "이메일";
            count++;
        } else {
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(SignUpActivity.this, "이메일 형식이 맞지 않습니다. 형식을 맞춰서 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            for(User user : list_user) {
                if(user.getId().equals(email.substring(0, email.indexOf("@")))) {
                    Toast.makeText(SignUpActivity.this, "이미 등록된 이메일입니다. 다른 이메일을 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
            }


        }
        if(pw.equals("PW") || pw.equals("")) {
            toastText += (count > 0) ? ", " : " ";
            toastText += "비밀번호";
        }
        if(name.equals("NICKNAME") || name.equals("")) {
            toastText += (count > 0) ? ", " : " ";
            toastText += "닉네임";
        } else {
            for(User user : list_user) {
                if(user.getName().equals(name)) {
                    Toast.makeText(SignUpActivity.this, "중복된 닉네임입니다. 다른 닉네임을 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        if(loc.equals("GPS") || loc.equals("")) {
            toastText += (count > 0) ? ", " : " ";
            toastText += "지역";
        }

        if(count > 0) {
            toastText += "을/를 입력해주세요";
            Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
        }
        else {
            createUser(email, pw, name, loc);
        }
    } // checkingUser()

    public void createUser(final String email, final String password, final String name, final String loc) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("firebaseAuthSuccess", email + " " + password);
                            signUp(email.substring(0, email.indexOf("@")), password, name, loc);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("firebaseAuth", email + " " + password);
                            Toast.makeText(SignUpActivity.this, "회원가입에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signUp(String id, String pw, String name, String loc) {
        user = new User(id, pw, name, loc);
        mDatabaseRef.child(id).setValue(user);
        uploadProfilePhoto();
        Toast.makeText(SignUpActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
