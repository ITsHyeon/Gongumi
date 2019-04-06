package com.example.gongumi.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gongumi.R;
import com.example.gongumi.model.Post;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.gongumi.fragment.PostCategoryFragment.newInstance;

public class PostFragment extends Fragment {

    private TextView text_category, text_term, text_num;
    private EditText edit_product, edit_price, edit_desc, edit_url;

    private Post post;

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
            post = new Post();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_post, PostCategoryFragment.newInstance(post));
            transaction.commit();
        }
        else {
            text_category = view.findViewById(R.id.text_category);
            text_term = view.findViewById(R.id.text_term);
            text_num = view.findViewById(R.id.text_num);
            edit_product = view.findViewById(R.id.edit_product);
            edit_price = view.findViewById(R.id.edit_price);
            edit_desc = view.findViewById(R.id.edit_description);
            edit_url = view.findViewById(R.id.edit_url);

            if(post.getCategory() != null && post.getEndDay() != null && post.getNum() != 0) {
                text_category.setText(post.getCategory());
                text_term.setText(DateToString());
                text_num.setText(post.getNum() + "명");
            }
        }

        return view;
    }

    public String DateToString() {
        String date = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        post.setStartDay(new Date());
        date = format.format(post.getStartDay()) + " ~ " + format.format(post.getEndDay());

        return date;
    }

    public boolean check() {
        String product = edit_product.getText().toString();
        int price = Integer.parseInt(edit_price.getText().toString().equals("") ? "0" : edit_price.getText().toString());
        String desc = edit_desc.getText().toString().trim();
        String url = edit_url.getText().toString().trim();

        if(product.equals("")) {
            Toast.makeText(getContext(), "상품명을 입력해주세요", Toast.LENGTH_LONG).show();
        }
        else if(price <= 0) {
            Toast.makeText(getContext(), "가격을 입력해주세요", Toast.LENGTH_LONG).show();
        }
        else if(desc.equals("")) {
            Toast.makeText(getContext(), "설명을 입력해주세요", Toast.LENGTH_LONG).show();
        }
        else if(url.equals("")) {
            Toast.makeText(getContext(), "URL을 입력해주세요", Toast.LENGTH_LONG).show();
        }
        else {
            post.setProduct(product);
            post.setPrice(price);
            post.setContent(desc);
            post.setUrl(url);

            return true;
        }

        return false;
    }

}
