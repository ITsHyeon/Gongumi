package com.example.gongumi.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.gongumi.R;
import com.example.gongumi.model.Post;

public class CategoryFragment extends Fragment {
    private Post post;

    EditText edit_search;
    ImageButton btn_search;
    Button btn_hashtags[] = new Button[5];

    private int index_hashtag = 0;
    private String hashtags = "";

    public static CategoryFragment newInstance(Post post){
        CategoryFragment fragment = new CategoryFragment();
        Bundle bundle =  new Bundle(1);
        bundle.putSerializable("post", post);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        edit_search = view.findViewById(R.id.edit_search);
        btn_search = view.findViewById(R.id.btn_search);

        btn_hashtags[0] = view.findViewById(R.id.btn_hashtag1);
        btn_hashtags[1] = view.findViewById(R.id.btn_hashtag2);
        btn_hashtags[2] = view.findViewById(R.id.btn_hashtag3);
        btn_hashtags[3] = view.findViewById(R.id.btn_hashtag4);
        btn_hashtags[4] = view.findViewById(R.id.btn_hashtag5);



        return view;
    }

}
