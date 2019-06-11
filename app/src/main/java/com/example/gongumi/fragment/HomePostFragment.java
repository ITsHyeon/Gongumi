package com.example.gongumi.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gongumi.R;
import com.example.gongumi.activity.MainActivity;
import com.example.gongumi.custom.CustomDialog;
import com.example.gongumi.custom.CustomHomePostDialog;
import com.example.gongumi.model.Chat;
/*import com.example.gongumi.model.ChatUser;*/
import com.example.gongumi.model.Post;
import com.example.gongumi.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.constraint.Constraints.TAG;

public class HomePostFragment extends Fragment {
    CircleImageView profileImg;
    TextView profile;
    TextView product;
    TextView price;
    TextView hashtag;
    ProgressBar progressBar;
    TextView people;
    TextView content;
    TextView url;
    TextView date;
    ViewFlipper flipper;
    Button joinBtn, backBtn, nextBtn;
    WebView webView;
    private User user;
    boolean userCheck = false;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    StorageReference mStorage = storageRef.child("thumbnail/");
    StorageReference profileRef = storageRef.child("user_profile");

    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Post/");
    DatabaseReference databaseRef = mDatabaseRef;

    private DecimalFormat decimalFormat = new DecimalFormat("#,###");

    String profile_text, profileImg_text, product_text, price_text, hashtag_text, content_text, time_text, people_text, userId_text, url_text, start_text, end_text;
    int progress_int, people_int, imgCount;
    boolean update = false;
    String opt;
    private int m_nPreTouchPosX = 0;

    public HomePostFragment() {
    }


    public static HomePostFragment newInstance(String profile, String profileImg, String product, String price, String hashtag, int progressbar, int people, String content, String time, Date startDay, Date endDay, int imgCount, String userId, String url) {
        HomePostFragment fragment = new HomePostFragment();
        Bundle args = new Bundle();
        args.putString("profile", profile);
        args.putString("profileImg", profileImg);
        args.putString("product", product);
        args.putString("price", price);
        args.putString("hashtag", hashtag);
        args.putInt("progressbar", progressbar);
        args.putInt("people", people);
        args.putString("content", content);
        args.putString("time", time);
        args.putString("start", (startDay.getYear()+1900) + "." + (startDay.getMonth()+1) + "." + startDay.getDate());
        args.putString("end", (endDay.getYear()+1900) + "." + (endDay.getMonth()+1) + "." + endDay.getDate());
        args.putInt("imgCount",imgCount);
        args.putString("userId", userId);
        args.putString("url", url);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_post, container, false);

        if(getArguments() != null){
            profile_text = getArguments().getString("profile");
            profileImg_text = getArguments().getString("profileImg");
            product_text = getArguments().getString("product");
            price_text = getArguments().getString("price");
            hashtag_text = getArguments().getString("hashtag");
            progress_int = getArguments().getInt("progressbar");
            people_int = getArguments().getInt("people");
            content_text = getArguments().getString("content");
            time_text = getArguments().getString("time");
            imgCount = getArguments().getInt("imgCount");
            userId_text = getArguments().getString("userId");
            url_text = getArguments().getString("url");
            start_text = getArguments().getString("start");
            end_text = getArguments().getString("end");
        }

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String joinId = data.getKey();
                    Log.v("joinIDcheck", joinId + " : " + user.getId());
                    if (user.getId().equals(joinId)) {
                        userCheck = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };
        databaseRef.child(time_text).child("join").addValueEventListener(postListener);

        date = view.findViewById(R.id.dateText);
        date.setText(start_text + " ~ " + end_text);

        flipper = view.findViewById(R.id.flipper);
        flipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movePrevView();
            }
        });

        for(int i=1;i<=imgCount;i++) {
            ImageView img= new ImageView(getContext());
            storageRef = mStorage.child(time_text + "/thumbnail"+i+".jpg");
            Glide.with(getActivity()).load(storageRef).apply(new RequestOptions().error(null)).into(img);
            flipper.addView(img);
        }

        backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movePrevView();
            }
        });

        nextBtn = view.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveNextView();
            }
        });

        flipper.setOnTouchListener(touchListener);

        profile = view.findViewById(R.id.profile);
        profile.setText(profile_text);
        profileImg = view.findViewById(R.id.profileImg);
        profileRef = profileRef.child(userId_text+".jpg");
        Glide.with(getActivity()).load(profileRef).into(profileImg);

        product = view.findViewById(R.id.product);
        product.setText(product_text);

        price = view.findViewById(R.id.price);
        price_text = decimalFormat.format(Integer.parseInt(price_text.replaceAll(",","")));
        price.setText(price_text + "원");

        hashtag = view.findViewById(R.id.hashtag);
        hashtag.setText(hashtag_text);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setMax(progress_int);
        progressBar.setProgress(people_int);

        people = view.findViewById(R.id.people);
        if(!update)
        {
            people_text = String.valueOf(progress_int) + "명 중 " + String.valueOf(people_int) + people.getText();
            people.setText(people_text);
        }

        content = view.findViewById(R.id.content);
        content.setText(content_text);

        Intent intent = getActivity().getIntent();
        user = (User) intent.getSerializableExtra("user");

        mDatabaseRef = mDatabaseRef.child(time_text + "/join/" + user.getId());

        joinBtn = view.findViewById(R.id.btJoin);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*CustomHomePostDialog customDialog2 = new CustomHomePostDialog(getActivity());
                customDialog2.callFunction("수량을 입력해주세요", "qty", user.getId(), time_text);

                CustomHomePostDialog customDialog = new CustomHomePostDialog(getActivity());
                boolean okCheck2= customDialog.callFunction("옵션을 입력해주세요", "option", user.getId(), time_text);*/

                Intent intent = getActivity().getIntent();
                user = (User) intent.getSerializableExtra("user");

                if(!userCheck) showDialog();
                else Toast.makeText(getContext(), "이미 공구에 참여했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        webView = view.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());

        url = view.findViewById(R.id.url);
        url.setText(Html.fromHtml("<u>" + url_text + "</u>"));

        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showWeb(url_text);
                    }
                });
                alert.setMessage("해당 URL로 이동하시겠습니까?");
                alert.show();
            }
        });

        return view;
    }

    public void showDialog() {
        CustomHomePostDialog dialog = CustomHomePostDialog.newInstance(new CustomHomePostDialog.NameInputListener() {
            @Override
            public void onNameInputComplete(String text) {
                if (text != null) {
                    CustomHomePostDialog dialog2 = CustomHomePostDialog.newInstance(new CustomHomePostDialog.NameInputListener() {
                        @Override
                        public void onNameInputComplete(String text) {
                            if (text != null) {
                                Map<String, Object> values = new HashMap<>();
                                values.put("opt", opt);
                                values.put("qty", text);

                                mDatabaseRef.updateChildren(values);
                                int peopleCount = people_int + 1;

                                databaseRef = databaseRef.child(time_text + "/people");
                                databaseRef.setValue(peopleCount);

                                final Chat chat = new Chat();

                                FirebaseDatabase.getInstance().getReference().child("Post").child(time_text).child("chatroom").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                                            Chat chatItem = data.getValue(Chat.class);
                                            chat.users = chatItem.users;
                                            Log.d("chatItem_users_size", chatItem.users.size() + "");
                                        }
                                        chat.users.put(user.getUid(), true);

                                        // TODO : 기존 USER까지 다 불러와서 넣어라
                                        FirebaseDatabase.getInstance().getReference().child("Post").child(time_text).child("chatroom").child(time_text).child("users").setValue(chat.users);
                                        FirebaseDatabase.getInstance().getReference().child("User").child(userId_text).child("chatlist").child(time_text).setValue(chat);

                                        FirebaseDatabase.getInstance().getReference().child("Post").child(time_text).child("join").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                    String joinId = data.getKey();
                                                    FirebaseDatabase.getInstance().getReference().child("User").child(joinId).child("chatlist").child(time_text).setValue(chat);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                Toast.makeText(getContext(), "주문을 완료하였습니다..", Toast.LENGTH_SHORT).show();

                                update = true;
                                progressBar.setProgress(peopleCount);

                                people_text = String.valueOf(progress_int) + "명 중 " + String.valueOf(peopleCount) + "명이 참여했습니다.";
                                people.setText(people_text);
                            }
                        }
                    });
                    dialog2.setText("수량을 입력하세요");
                    dialog2.show(getFragmentManager(), "qtyDialog");
                    opt = text;
                }
            }
        });
        dialog.setText("옵션을 입력하세요");
        dialog.show(getFragmentManager(), "optDialog");
    }

    public void showWeb(String url) {
        webView.loadUrl(url);
        webView.setVisibility(View.VISIBLE);
    }

    public void moveNextView() {
        flipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.appear_from_right));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.disappear_to_left));

        if(flipper.getDisplayedChild() != flipper.getChildCount() - 1) {
            flipper.showNext();
        } else {
            flipper.setDisplayedChild(0);
        }
    }

    public void movePrevView() {
        flipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.appear_from_left));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.disappear_to_right));

        if(flipper.getDisplayedChild() != 0) {
            flipper.showPrevious();
        } else {
            flipper.setDisplayedChild(flipper.getChildCount()-1);
        }
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                m_nPreTouchPosX = (int)event.getX();
            }
            if(event.getAction() == MotionEvent.ACTION_UP) {
                int nTouchPosX = (int)event.getX();

                if(nTouchPosX < m_nPreTouchPosX) {
                    moveNextView();
                }
                else if(nTouchPosX > m_nPreTouchPosX) {
                    movePrevView();
                }
                m_nPreTouchPosX = nTouchPosX;
            }
            return true;
        }
    };

}
