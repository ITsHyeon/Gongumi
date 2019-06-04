package com.example.gongumi.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.example.gongumi.R;
import com.example.gongumi.chat.ChatListActivity;
import com.example.gongumi.chat.MessageActivity;
import com.example.gongumi.fragment.CategoryFragment;
import com.example.gongumi.fragment.HomeFragment;
import com.example.gongumi.fragment.PostHashtagFragment;
import com.example.gongumi.fragment.PostFragment;
import com.example.gongumi.fragment.PostNumberFragment;
import com.example.gongumi.fragment.PostTermFragment;
import com.example.gongumi.fragment.SettingFragment;
import com.example.gongumi.adapter.PagerAdapter;
import com.example.gongumi.model.Chat;
import com.example.gongumi.model.Post;
import com.example.gongumi.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.gongumi.fragment.PostFragment.post_pos;
import static com.example.gongumi.fragment.SearchFragment.search_pos;

public class MainActivity extends AppCompatActivity {
    private static RelativeLayout layout_toolbar, layout_toolbar_post, layout_toolbar_cate;
    private Button btn_previous, btn_next, btn_chat,cate_previous, cate_chat;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private PagerAdapter adapter;

    private static int pos;
    public static final int THUMBNAIL_PHOTO_REQUEST_CODE = 10;

    private User user;
    public Post post = new Post(); // fragment와 통신을 해야하기 때문에 public

    // 채팅방 만들기
    private String uid;
    private String chatRoomUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO : 피드

        // 액션바 없애기
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        layout_toolbar = findViewById(R.id.layout_toolbar);
        layout_toolbar_cate = findViewById(R.id.layout_toolbar_cate);
        layout_toolbar_post = findViewById(R.id.layout_toolbar_post);
        btn_previous = findViewById(R.id.btn_previous);
        btn_next = findViewById(R.id.btn_next);
        btn_chat = findViewById(R.id.btn_chat);
        cate_previous = findViewById(R.id.cate_previous);
        cate_chat = findViewById(R.id.cate_chat);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this, MessageActivity.class);
                startActivity(intent1);
            }
        });
        cate_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this, ChatListActivity.class);
                startActivity(intent1);
            }
        });

        adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(R.drawable.tab_home_click, "홈", new HomeFragment());
        adapter.addFragment(R.drawable.tab_category, "카테고리", new CategoryFragment());
        adapter.addFragment(R.drawable.tab_write, "글쓰기", new PostFragment());
        adapter.addFragment(R.drawable.tab_setting, "설정", new SettingFragment());
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
        cate_previous.setOnClickListener(CateClickListener);

        // TODO : 푸시 알림
        passPushTokenToServer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        post_pos = 0;
    }

    public static void changeToolbar() {
        if (pos == 2) {
            layout_toolbar_post.setVisibility(View.VISIBLE);
            layout_toolbar.setVisibility(View.GONE);
            layout_toolbar_cate.setVisibility(View.GONE);
        } else if(pos == 1){
            layout_toolbar.setVisibility(View.GONE);
            layout_toolbar_post.setVisibility(View.GONE);
            layout_toolbar_cate.setVisibility(View.VISIBLE);
        } else {
            layout_toolbar.setVisibility(View.VISIBLE);
            layout_toolbar_post.setVisibility(View.GONE);
            layout_toolbar_cate.setVisibility(View.GONE);
        }
    }

    View.OnClickListener CateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.cate_previous) {
                if (search_pos == 2){
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_search, CategoryFragment.newInstance(post)).commit();

                    cate_previous.setVisibility(View.INVISIBLE);
                }
            }
        }
    };
    View.OnClickListener PostClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (v.getId() == R.id.btn_previous) {
                Log.d("test", post_pos + "");
                switch (post_pos) {
                    case 1:
                        AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
                        alert.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mTabLayout.setScrollPosition(0, 0, true);
                                mViewPager.setCurrentItem(0);
                                post_pos = 0;
                                post = new Post();
                            }
                        });
                        alert.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alert.setMessage("정말로 공구하기를 그만두시겠습니까?");
                        alert.show();
                        break;
                    case 2:
                        transaction.replace(R.id.frame_post, PostHashtagFragment.newInstance(post));
                        transaction.addToBackStack(null);
                        transaction.commit();

                        // 버튼 모양 바꾸기
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(SignUpActivity.dpToPx(getApplicationContext(), 20), SignUpActivity.dpToPx(getApplicationContext(), 20));
                        layoutParams.leftMargin = SignUpActivity.dpToPx(getApplicationContext(), 20);
                        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                        btn_previous.setLayoutParams(layoutParams);
                        btn_previous.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btn_cancle));
                        break;
                    case 3:
                        PostNumberFragment postNumberFragment = (PostNumberFragment) fragmentManager.findFragmentById(R.id.frame_post);
                        postNumberFragment.checkTextPrevious();
                        transaction.replace(R.id.frame_post, PostTermFragment.newInstance(post));
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case 4:
                        transaction.replace(R.id.frame_post, PostNumberFragment.newInstance(post));
                        transaction.addToBackStack(null);
                        transaction.commit();

                        // 버튼 모양 바꾸기
                        layoutParams = new RelativeLayout.LayoutParams(SignUpActivity.dpToPx(getApplicationContext(), 14), SignUpActivity.dpToPx(getApplicationContext(), 25));
                        layoutParams.rightMargin = SignUpActivity.dpToPx(getApplicationContext(), 20);
                        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        btn_next.setLayoutParams(layoutParams);
                        btn_next.setText("");
                        btn_next.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btn_next));
                        break;
                }
            } else {
                switch (post_pos) {
                    case 1:
                        Log.d("test", post.getHashtag() + "");
                        if (post.getHashtag() == null) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alert.setMessage("해시태그를 입력해주세요");
                            alert.show();
                        } else {
                            transaction.replace(R.id.frame_post, PostTermFragment.newInstance(post));
                            transaction.commit();

                            // 버튼 모양 바꾸기
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(SignUpActivity.dpToPx(getApplicationContext(), 14), SignUpActivity.dpToPx(getApplicationContext(), 25));
                            layoutParams.leftMargin = SignUpActivity.dpToPx(getApplicationContext(), 20);
                            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                            btn_previous.setLayoutParams(layoutParams);
                            btn_previous.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btn_prev));
                        }
                        break;
                    case 2:
                        Log.d("test", post.getEndDay().toString());
                        transaction.replace(R.id.frame_post, PostNumberFragment.newInstance(post));
                        transaction.commit();
                        break;
                    case 3:
                        PostNumberFragment fragment = (PostNumberFragment) fragmentManager.findFragmentById(R.id.frame_post);
                        if (fragment.checkText()) {
                            transaction.replace(R.id.frame_post, PostFragment.newInstance(post));
                            transaction.commit();

                            // 버튼 모양 바꾸기
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                            btn_next.setLayoutParams(layoutParams);
                            btn_next.setText("완료");
                            btn_next.setBackgroundColor(Color.TRANSPARENT);
                        }
                        break;
                    case 4:
                        final PostFragment postFragment = (PostFragment) fragmentManager.findFragmentById(R.id.frame_post);
                        if (postFragment.check()) {
                            post.setUserId(user.getId());
                            post.setUserUid(user.getUid());
                            post.setHashtag(post.getHashtag().trim());
                            post.setLocation(user.getLocation());

                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    post.setStartDay(new Date());
                                    addPost(post);
                                    uploadThumbnailPhoto(post.getStartDay().getTime(), postFragment.adapter.getList());
                                    mTabLayout.setScrollPosition(0, 0, true);
                                    mViewPager.setCurrentItem(0);
                                    post = new Post();
                                    post_pos = 0;

                                    // 버튼 모양 바꾸기
                                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(SignUpActivity.dpToPx(getApplicationContext(), 20), SignUpActivity.dpToPx(getApplicationContext(), 20));
                                    layoutParams.leftMargin = SignUpActivity.dpToPx(getApplicationContext(), 20);
                                    layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                                    btn_previous.setLayoutParams(layoutParams);
                                    btn_previous.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btn_cancle));

                                    // 버튼 모양 바꾸기
                                    layoutParams = new RelativeLayout.LayoutParams(SignUpActivity.dpToPx(getApplicationContext(), 14), SignUpActivity.dpToPx(getApplicationContext(), 25));
                                    layoutParams.rightMargin = SignUpActivity.dpToPx(getApplicationContext(), 20);
                                    layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    btn_next.setLayoutParams(layoutParams);
                                    btn_next.setText("");
                                    btn_next.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btn_next));
                                }
                            });
                            alert.setMessage("새로운 공구를 등록하시겠습니까?");
                            alert.show();
                        }
                        break;
                }
            }
        }
    };

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_search, fragment).commit();      // Fragment로 사용할 MainActivity내의 layout공간을 선택합니다.

        cate_previous.setVisibility(View.VISIBLE);
    }

    public void addPost(Post post) {
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Post");
        mDatabaseRef.child(String.valueOf(post.getStartDay().getTime())).setValue(post);
        Toast.makeText(this, "새로운 공구가 등록되었습니다!", Toast.LENGTH_SHORT).show();

        addChatroom();
    }

    public void addChatroom() {

        Chat chat = new Chat();
        chat.users.put(user.getUid(), true);

        FirebaseDatabase.getInstance().getReference().child("Post").child(String.valueOf(post.getStartDay().getTime())).child("chatroom").child(String.valueOf(post.getStartDay().getTime())).setValue(chat);
        FirebaseDatabase.getInstance().getReference().child("User").child(user.getId()).child("chatlist").child(String.valueOf(post.getStartDay().getTime())).setValue(chat);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case THUMBNAIL_PHOTO_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        if (clipData.getItemCount() > 3) {
                            Toast.makeText(this, "썸네일은 최대 3장까지만 선택할 수 있습니다", Toast.LENGTH_SHORT).show();
                        } else {
                            PostFragment fragment = (PostFragment) getSupportFragmentManager().findFragmentById(R.id.frame_post);
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                fragment.adapter.addPostThumbnailAdapter(clipData.getItemAt(i).getUri());
                            }
                            fragment.adapter.notifyDataSetChanged();
                            post.setImgCount(fragment.adapter.getItemCount());
                        }
                    } else {
                        //Toast.makeText(this, "이 기기는 사진을 여러 장 선택할 수 없습니다", Toast.LENGTH_SHORT).show();
                        if (data.getData() != null) {
                            Log.d("test", "getData");
                            PostFragment fragment = (PostFragment) getSupportFragmentManager().findFragmentById(R.id.frame_post);
                            fragment.adapter.addPostThumbnailAdapter(data.getData());
                            fragment.adapter.notifyDataSetChanged();
                            post.setImgCount(fragment.adapter.getItemCount());
                        }
                    }
                }
                break;
        }
    }

    public void uploadThumbnailPhoto(long time, ArrayList<Uri> list) {
        StorageReference mStorageRef;
        for (int i = 0; i < list.size(); i++) {
            mStorageRef = FirebaseStorage.getInstance().getReference().child("thumbnail/" + time + "/thumbnail" + (i + 1) + ".jpg");
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .build();

            UploadTask uploadTask = mStorageRef.putFile(list.get(i), metadata);
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

    // TODO : 푸시알림
    void passPushTokenToServer() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> map = new HashMap<>();
        map.put("pushToken", token);

        FirebaseDatabase.getInstance().getReference().child("User/").child(user.getId()).updateChildren(map);
    }
}