package com.example.gongumi.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gongumi.R;
import com.example.gongumi.adapter.RecyclerAdapter;
import com.example.gongumi.model.Home;
import com.example.gongumi.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class HomeFragment extends Fragment {
    private DatabaseReference mDatabase;

    ArrayList<Home> items = new ArrayList<>();
    RecyclerAdapter adpater;
    Home item;

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

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                items.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    Post post = data.getValue(Post.class);
                    item = new Home(post.getProduct(), String.valueOf(post.getPrice()), post.getUrl(), post.getNum(), 0, post.getContent(), post.getStartDay());
                    items.add(item);
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

//        Home[] item = new Home[ITEM_SIZE];
//        item[0] = new Home(R.drawable.tab_home, "맛있는 아마스빈", "3000원", 5, 2);
//        item[1] = new Home(R.drawable.tab_write, "맛있는 과자", "5000원", 10, 4);
//        item[2] = new Home(R.drawable.tab_setting, "예쁜 봄 옷", "10000원", 20, 17);
//        item[3] = new Home(R.drawable.tab_category, "휴대폰케이스", "5000원", 10, 3);
//        item[4] = new Home(R.drawable.tab_setting, "예쁜 지현이", "1억원", 1, 0);
//        item[5] = new Home(R.drawable.tab_category, "하백", "1억원", 15, 15);
//
//        for (int i = 0; i < ITEM_SIZE; i++) {
//            items.add(item[i]);
//        }

        return view;
    }
}
