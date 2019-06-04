package com.example.gongumi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gongumi.R;
import com.example.gongumi.activity.MainActivity;
import com.example.gongumi.adapter.RecyclerAdapter;
import com.example.gongumi.model.Home;
import com.example.gongumi.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static android.support.constraint.Constraints.TAG;
import static com.example.gongumi.fragment.CategoryFragment.keyword;

public class SearchFragment extends Fragment {
    private DatabaseReference mDatabase;
    private Post post;
    ArrayList<Home> items = new ArrayList<>();
    RecyclerAdapter adpater;
    Home item;
    TextView searchtag;

    public static int search_pos = 0;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(Post post) {
        SearchFragment fragment = new SearchFragment();
        Bundle bundle =  new Bundle(1);
        bundle.putSerializable("post", post);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        search_pos = 2;
        if(getArguments() != null) {
            post = (Post) getArguments().getSerializable("post");
            ((MainActivity)getActivity()).post = post;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Post");

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.searchList);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        searchtag = view.findViewById(R.id.hashtag_text);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to ugitpdate the UI
                items.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    post = data.getValue(Post.class);
                    String str = String.valueOf(post.getHashtag());
                    Log.d("getHashtag", str);
                    if(keyword != null && str.contains(keyword)) {
                        item = new Home(post.getProduct(), String.valueOf(post.getPrice()), post.getHashtag(), post.getNum(), post.getPeople(), post.getContent(), post.getStartDay(), post.getImgCount(), post.getUserId(), post.getUrl());
                        items.add(item);
                    }
                }

                Collections.reverse(items);

                searchtag.setText(keyword);
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
