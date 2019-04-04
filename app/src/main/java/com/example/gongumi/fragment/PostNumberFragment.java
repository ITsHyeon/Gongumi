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

import static com.example.gongumi.fragment.PostFragment.post_pos;


public class PostNumberFragment extends Fragment {

    private Post post;
    private Button btn_previous, btn_next;


    public PostNumberFragment() {
        // Required empty public constructor
    }

    public static PostNumberFragment newInstance(Post post) {
        PostNumberFragment fragment = new PostNumberFragment();
        Bundle bundle =  new Bundle(1);
        bundle.putSerializable("post", post);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        post_pos = 3;
        if(getArguments() != null) {
            post = (Post) getArguments().getSerializable("post");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_number, container, false);
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
                    transaction.replace(R.id.frame_post, PostTermFragment.newInstance(post));
                    transaction.commit();
                    break;
                case R.id.btn_next:
                    transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_post, PostFragment.newInstance(post));
                    transaction.addToBackStack("post_number");
                    transaction.commit();
                    break;
            }
        }
    };

}
