package com.example.gongumi.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.gongumi.R;
import com.example.gongumi.model.Post;

import static com.example.gongumi.fragment.PostCategoryFragment.newInstance;

public class PostFragment extends Fragment {

    private Button btn_previous, btn_next;
    private Post post = new Post();

    public static int post_pos = 0;

    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment newInstance(Post post) {
        PostFragment fragment = new PostFragment();
        Bundle bundle =  new Bundle(1);
        bundle.putSerializable("post", post);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            post = (Post) getArguments().getSerializable("post");
            post_pos = 4;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        if(post_pos == 0) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_post, PostCategoryFragment.newInstance(post));
            transaction.commit();
        }
        btn_previous = view.findViewById(R.id.btn_previous);
        btn_next = view.findViewById(R.id.btn_next);

        btn_previous.setOnClickListener(ChangeFragmentClickListener);
        btn_next.setOnClickListener(ChangeFragmentClickListener);

        return view;
    }

    View.OnClickListener ChangeFragmentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btn_previous:
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_post, PostNumberFragment.newInstance(post));
                    transaction.commit();

                    break;
                case R.id.btn_next:

                    break;
            }
        }
    };


}
