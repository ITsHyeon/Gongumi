package com.example.gongumi.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gongumi.R;
import com.example.gongumi.adapter.RecyclerAdapter;
import com.example.gongumi.model.Home;
import com.example.gongumi.model.Post;
import com.example.gongumi.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class HomeFragment extends Fragment {
    private DatabaseReference mDatabase;

    RecyclerView recyclerView;
    ArrayList<Home> items = new ArrayList<>();
    RecyclerAdapter adpater;
    Home item;
    private User user;
    TextView address;
    String profile, profileImg;

    public HomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        recyclerView = (RecyclerView) view.findViewById(R.id.homeList);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = getActivity().getIntent();
        user = (User) intent.getSerializableExtra("user");
        address = view.findViewById(R.id.address_text);
        address.setText(user.getLocation());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        update();

    }

    public void update() {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                items.clear();
                Date date = new Date();
                boolean dateCheck;

                for(DataSnapshot data : dataSnapshot.getChildren()){
                    final Post post = data.getValue(Post.class);

                    int compare = date.compareTo( post.getEndDay() ); // 날짜비교

                    if ( compare > 0 ){ // 현재날짜가 삭제 시작일 후 인 경우
                        //System.out.println("currentDate  >  memDelStartDate");
                        dateCheck = true;
                    } else if ( compare < 0) { // 현재날짜가 삭제 시작일 전 인 경우
                        dateCheck = false;
                        //System.out.println("currentDate  <  memDelStartDate");
                    } else { // 현재날짜가 삭제 시작일 인 경우
                        dateCheck = false;
                        //System.out.println("currentDate  =  memDelStartDate");
                    }

                    ValueEventListener userListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot data : dataSnapshot.getChildren()){
                                User postUser = data.getValue(User.class);
                                if(postUser.getUid().equals(post.getUserUid())) {
                                    profile = postUser.getName();
                                    profileImg = postUser.getProfileUrl();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // ...
                        }
                    };
                    mDatabase.child("User").addValueEventListener(userListener);

                    if(post.getLocation().equals(user.getLocation()) && !dateCheck) {
                        item = new Home(post.getUserName(), profileImg, post.getProduct(), String.valueOf(post.getPrice()), post.getHashtag(), post.getNum(), post.getPeople(), post.getContent(), post.getStartDay(), post.getEndDay(), post.getImgCount(), post.getUserId(), post.getUrl());
                        items.add(item);

                        adpater.notifyDataSetChanged();
                    }
                }

                Collections.reverse(items);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.child("Post").addValueEventListener(postListener);

        adpater = new RecyclerAdapter(getActivity(), items, R.layout.activity_main);
        recyclerView.setAdapter(adpater);

        adpater.notifyDataSetChanged();
    }
}
