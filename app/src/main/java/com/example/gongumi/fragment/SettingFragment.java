package com.example.gongumi.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gongumi.R;
import com.example.gongumi.activity.LoginActivity;
import com.example.gongumi.custom.CustomDialog;
import com.example.gongumi.model.User;
import com.example.gongumi.service.GpsTracker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingFragment extends Fragment {
    // layout
    private TextView mTvName, mTvAddress;
    private LinearLayout mLiPostList, mLiJoinList, mLiLogOut;
    private CircleImageView mCiChangeProfile;

    // gps
    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] GPS_REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    // profile
    private Uri photoUri = null;
    private boolean check = false;
    private static final int PROFILE_PHOTO_REQUEST_CODE = 10;
    private static final int GALLERY_PERMISSIONS_REQUEST_CODE = 11;
    String[] GALLERY_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    // database
    private DatabaseReference mDatabase;
    private User user;

    // storage
    StorageReference storageRef;
    StorageReference pathRef;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_setting, container, false);

        mTvName = view.findViewById(R.id.tvName);
        mTvAddress = view.findViewById(R.id.tvAdress);
        mLiPostList = view.findViewById(R.id.liPostList);
        mLiJoinList = view.findViewById(R.id.liJoinList);
        mLiLogOut = view.findViewById(R.id.liLogout);

        mCiChangeProfile = view.findViewById(R.id.ciChangeProfile);
        Button mBtChangeName = view.findViewById(R.id.btChangeName);
        Button mBtChangeAdress = view.findViewById(R.id.btChangeAdress);

        // database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User");
        storageRef = FirebaseStorage.getInstance("gs://gongumi-6995f.appspot.com").getReference().child("user_profile");
        pathRef = storageRef;

        // TODO : 스토리지에 있는 프로필 가져와서 setting 하기기
        // 기존 값 setting
        Intent intent = getActivity().getIntent();
        user = (User) intent.getSerializableExtra("user");
        storageRef = pathRef.child(user.getId()+".jpg");


        mTvName.setText(user.getName()); // 이름(name)
        mTvAddress.setText(user.getLocation()); // 주소(address)
        Glide.with(getActivity()).load(storageRef).into(mCiChangeProfile);


        mBtChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 커스텀 다이얼로그를 생성한다.
                CustomDialog customDialog = new CustomDialog(getContext());

                // 커스텀 다이얼로그를 호출한다
                // 커스텀 다이얼로그의 결과를 출력할 Text
                customDialog.callFunction(mTvName);
                customDialog.text.setText("변경할 이름을 입력해주세요.");
                customDialog.message.setFilters(new InputFilter[]{new InputFilter.LengthFilter((10))});
                /*Map<String, Object> taskMap = new HashMap<String, Object>();
                taskMap.put("name", mTvName);*/
            }
        });

        mBtChangeAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkLocationServicesStatus()){
                    showDialogForLocationServiceSetting();
                } else {
                    checkRunTimePermission();
                }
                getGPS();
                Toast.makeText(getContext(),"위치정보가 변경되었습니다.", Toast.LENGTH_LONG).show();
            }
        });

        // TODO : 프로필 이미지 변경
        mCiChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // TODO : 로그아웃
        mLiLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog();
            }
        });

     /*   mTvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog();
            }
        });
*/
        return view;

    }
    // GPS 가져오기
    public void getGPS(){
        gpsTracker = new GpsTracker(getContext());

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        String address = getCurrentAddress(latitude, longitude);

        mTvAddress.setText(address);
    }
    // GPS 퍼미션 확인
    void checkRunTimePermission() {
        //  런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        // 2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
        if (!(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED)) {
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), GPS_REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(getContext(), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(getActivity(), GPS_REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(getActivity(), GPS_REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    } // checkRunTimePermission()

    // GPS 주소 변환
    public String getCurrentAddress(double latitude, double longitude) {
        // Geocoder : GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1);
        } catch (IOException ioException) {
            // 네트워크 문제
            Toast.makeText(getContext(), "네트워크가 있는 곳에서 다시 실행해주세요", Toast.LENGTH_LONG).show();
            return "GPS";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getContext(), "잠시 후 다시 실행해주세요", Toast.LENGTH_LONG).show();
            return "GPS";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getContext(), "GPS를 인식하지 못했습니다. 잠시 후 다시 실행해주세요", Toast.LENGTH_LONG).show();
            return "GPS";
        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).substring(address.getAddressLine(0).indexOf("시") + 1,
                address.getAddressLine(0).indexOf("구") + 1).trim() + " " + address.getThoroughfare();


    } // getCurrentAddress()

    // 여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 GPS 서비스가 필요합니다.\n" + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create().show();
    } // showDialogForLocationServiceSetting()

    // GPS 활성화 결과
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case GPS_ENABLE_REQUEST_CODE:
                // 사용자가 GPS 활성 시켰는지 검사
                if(checkLocationServicesStatus()){
                    if(checkLocationServicesStatus()){
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;

                // profile
//            case PROFILE_PHOTO_REQUEST_CODE:
//                if(resultCode == RESULT_OK){
//
//                }

        }
    }

    // GPS 서비스 상태 확인
    public boolean checkLocationServicesStatus(){
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void logoutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

}
