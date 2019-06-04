package com.example.gongumi.fragment;


import android.content.Intent;
import android.os.Bundle;
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

    ArrayList<Home> items = new ArrayList<>();
    RecyclerAdapter adpater;
    Home item;
    private User user;
    TextView address;

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
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Post");

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.homeList);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = getActivity().getIntent();
        user = (User) intent.getSerializableExtra("user");
        address = view.findViewById(R.id.address_text);
        address.setText(user.getLocation());

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                items.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    Post post = data.getValue(Post.class);

                    Date date = new Date();
                    long dateCheck = post.getEndDay().getTime() - date.getTime();

                    if(post.getLocation().equals(user.getLocation()) && dateCheck >= 0) {
                        item = new Home(post.getProduct(), String.valueOf(post.getPrice()), post.getHashtag(), post.getNum(), post.getPeople(), post.getContent(), post.getStartDay(), post.getEndDay(), post.getImgCount(), post.getUserId(), post.getUrl());
                        items.add(item);
                    }
                }

                Collections.reverse(items);
                adpater = new RecyclerAdapter(getActivity(), items, R.layout.activity_main);
                recyclerView.setAdapter(adpater);

                adpater.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.addValueEventListener(postListener);

        return view;
    }
}
