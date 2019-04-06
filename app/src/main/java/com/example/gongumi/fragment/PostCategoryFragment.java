package com.example.gongumi.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.gongumi.R;
import com.example.gongumi.activity.MainActivity;
import com.example.gongumi.model.Post;

import static com.example.gongumi.fragment.PostFragment.post_pos;

public class PostCategoryFragment extends Fragment {

    private Post post;
    private Button btn_fashion, btn_beauty, btn_food, btn_etc;
    private Button[] btn_categories = new Button[4];
    private String[] categories = {"패션", "뷰티", "푸드", "기타"};

    private int selected = -1;


    public static PostCategoryFragment newInstance(Post post) {
        PostCategoryFragment fragment = new PostCategoryFragment();
        Bundle bundle =  new Bundle(1);
        bundle.putSerializable("post", post);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        post_pos = 1;
        if(getArguments() != null) {
            post = (Post) getArguments().getSerializable("post");
            ((MainActivity)getActivity()).post = post;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_category, container, false);

        btn_fashion = view.findViewById(R.id.btn_category_fashion);
        btn_beauty = view.findViewById(R.id.btn_category_beauty);
        btn_food = view.findViewById(R.id.btn_category_food);
        btn_etc = view.findViewById(R.id.btn_category_etc);

        btn_categories[0] = btn_fashion;
        btn_categories[1] = btn_beauty;
        btn_categories[2] = btn_food;
        btn_categories[3] = btn_etc;

        if(post.getCategory() != null) {
            for (int i = 0; i < categories.length; i++) {
                if (post.getCategory().equals(categories[i])) {
                    selected = i;
                    btn_categories[i].setSelected(true);
                }
            }
        }

        btn_fashion.setOnClickListener(CategoryClickListener);
        btn_beauty.setOnClickListener(CategoryClickListener);
        btn_food.setOnClickListener(CategoryClickListener);
        btn_etc.setOnClickListener(CategoryClickListener);

        return view;
    }

    View.OnClickListener CategoryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btn_category_fashion:
                    if(selected != -1 && selected != 0) {
                        btn_categories[selected].setSelected(false);
                    }
                    selected = 0;
                    btn_fashion.setSelected(true);
                    break;
                case R.id.btn_category_beauty:
                    if(selected != -1 && selected != 1) {
                        btn_categories[selected].setSelected(false);
                    }
                    selected = 1;
                    btn_beauty.setSelected(true);
                    break;
                case R.id.btn_category_food:
                    if(selected != -1 && selected != 2) {
                        btn_categories[selected].setSelected(false);
                    }
                    selected = 2;
                    btn_food.setSelected(true);
                    break;
                case R.id.btn_category_etc:
                    if(selected != -1 && selected != 3) {
                        btn_categories[selected].setSelected(false);
                    }
                    selected = 3;
                    btn_etc.setSelected(true);
                    break;
            }
            post.setCategory(categories[selected]);
            Log.d("test", post.getCategory());
        }
    };

}
