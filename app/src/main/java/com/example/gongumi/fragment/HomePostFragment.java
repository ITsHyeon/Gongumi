package com.example.gongumi.fragment;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gongumi.R;
import com.example.gongumi.custom.CustomDialog;
import com.example.gongumi.custom.CustomHomePostDialog;
import com.example.gongumi.model.Chat;
import com.example.gongumi.model.ChatUser;
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HomePostFragment extends Fragment {
    TextView product;
    TextView price;
    TextView hashtag;
    ProgressBar progressBar;
    TextView people;
    TextView content;
    ViewFlipper flipper;
    ImageView img01, img02, img03;
    Button joinBtn, backBtn, nextBtn;
    boolean dialogOk = false;
    private User user;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("thumbnail/");
    StorageReference mStorage = storageRef;
    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Post/");
    DatabaseReference databaseRef = mDatabaseRef;

    String product_text, price_text, hashtag_text, content_text, time_text, people_text;
    int progress_int, people_int, imgCount;
    boolean update = false;
    String opt;
    private int m_nPreTouchPosX = 0;

    public HomePostFragment() {
    }


    public static HomePostFragment newInstance(String product, String price, String hashtag, int progressbar, int people, String content, String time, int imgCount) {
        HomePostFragment fragment = new HomePostFragment();
        Bundle args = new Bundle();
        args.putString("product", product);
        args.putString("price", price);
        args.putString("hashtag", hashtag);
        args.putInt("progressbar", progressbar);
        args.putInt("people", people);
        args.putString("content", content);
        args.putString("time", time);
        args.putInt("imgCount",imgCount);
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
            product_text = getArguments().getString("product");
            price_text = getArguments().getString("price");
            hashtag_text = getArguments().getString("hashtag");
            progress_int = getArguments().getInt("progressbar");
            people_int = getArguments().getInt("people");
            content_text = getArguments().getString("content");
            time_text = getArguments().getString("time");
            imgCount = getArguments().getInt("imgCount");
        }

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

        product = view.findViewById(R.id.product);
        product.setText(product_text);

        price = view.findViewById(R.id.price);
        price.setText(price_text);

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

                CustomHomePostDialog dialog = CustomHomePostDialog.newInstance(new CustomHomePostDialog.NameInputListener() {
                    @Override
                    public void onNameInputComplete(String text) {
                        if(text != null) {
                            CustomHomePostDialog dialog2 = CustomHomePostDialog.newInstance(new CustomHomePostDialog.NameInputListener() {
                                @Override
                                public void onNameInputComplete(String text) {
                                    if(text != null) {
                                        Map<String, Object> values = new HashMap<>();
                                        values.put("opt", opt);
                                        values.put("qty", text);

                                        mDatabaseRef.updateChildren(values);
                                        int peopleCount = people_int + 1;

                                        databaseRef = databaseRef.child(time_text + "/people");
                                        databaseRef.setValue(peopleCount);

                                        Chat chat = new Chat();

                                        FirebaseDatabase.getInstance().getReference().child("Post").child(time_text).child("chatroom").child(time_text).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Chat chatItem = dataSnapshot.getValue(Chat.class);
                                                Iterator<String> keySet = chatItem.users.keySet().iterator();
                                                while(keySet.hasNext()) {
                                                    String key = (String) keySet.next();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                        chat.users.put(user.getUid(), true);

                                        // TODO : 기존 USER까지 다 불러와서 넣어라
                                        FirebaseDatabase.getInstance().getReference().child("Post").child(time_text).child("chatroom").child(time_text).setValue(chat);
                                        FirebaseDatabase.getInstance().getReference().child("User").child(user.getId()).child("chatlist").child(time_text).setValue(chat);

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
        });



        return view;
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
