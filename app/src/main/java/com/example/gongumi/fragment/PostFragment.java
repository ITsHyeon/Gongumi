package com.example.gongumi.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gongumi.R;
import com.example.gongumi.activity.MainActivity;
import com.example.gongumi.adapter.PostThumbnailRecyclerViewAdapter;
import com.example.gongumi.model.Post;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.gongumi.activity.MainActivity.THUMBNAIL_PHOTO_REQUEST_CODE;
import static com.example.gongumi.fragment.PostCategoryFragment.newInstance;

public class PostFragment extends Fragment {

    private ScrollView scrollView;
    private RecyclerView recyclerView;
    private Button btn_thumbnail;
    private TextView text_category, text_term, text_num, text_desc;
    private EditText edit_product, edit_price, edit_desc, edit_url;

    private DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private String result="";

    public PostThumbnailRecyclerViewAdapter adapter;
    private ArrayList<Uri> list;

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
        if(post_pos != 4) {
            post = new Post();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_post, PostCategoryFragment.newInstance(post));
            transaction.commit();
        }
        else {
            recyclerView = view.findViewById(R.id.post_thumbnail_recyclerview);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            list = new ArrayList<>();
            adapter = new PostThumbnailRecyclerViewAdapter(getContext(), list);
            recyclerView.setAdapter(adapter);

            scrollView = view.findViewById(R.id.scrollView);
            btn_thumbnail = view.findViewById(R.id.btn_thumbnail);
            text_category = view.findViewById(R.id.text_category);
            text_term = view.findViewById(R.id.text_term);
            text_num = view.findViewById(R.id.text_num);
            text_desc = view.findViewById(R.id.text_description);
            edit_product = view.findViewById(R.id.edit_product);
            edit_price = view.findViewById(R.id.edit_price);
            edit_desc = view.findViewById(R.id.edit_description);
            edit_url = view.findViewById(R.id.edit_url);

            btn_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getThumbnail();
                }
            });

            if(post.getCategory() != null && post.getEndDay() != null && post.getNum() != 0) {
                text_category.setText(post.getCategory());
                text_term.setText(DateToString());
                text_num.setText(post.getNum() + "명");
            }

            edit_price.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(result)){
                        result = decimalFormat.format(Integer.parseInt(s.toString().replaceAll(",","")));
                        edit_price.setText(result);
                        edit_price.setSelection(result.length());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            text_desc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edit_desc.requestFocus();
                }
            });

            edit_desc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus) {
                        scrollView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.smoothScrollBy(0, 800);
                            }
                        }, 100);
                    }
                }
            });
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

    public void getThumbnail() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            Log.d("test", "dd");
        }
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        getActivity().startActivityForResult(Intent.createChooser(intent, "Get Album"), THUMBNAIL_PHOTO_REQUEST_CODE);
    }


    public boolean check() {
        String product = edit_product.getText().toString();
        String str_price = edit_price.getText().toString().replace(",", "");
        int price = Integer.parseInt(str_price.equals("") ? "0" : str_price);
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
