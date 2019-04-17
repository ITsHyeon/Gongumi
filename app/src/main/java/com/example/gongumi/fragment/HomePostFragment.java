package com.example.gongumi.fragment;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gongumi.R;
import com.example.gongumi.custom.CustomDialog;
import com.example.gongumi.custom.CustomHomePostDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

public class HomePostFragment extends Fragment {
    TextView product;
    TextView price;
    TextView url;
    ProgressBar progressBar;
    TextView people;
    TextView content;
    ImageView img01;
    Button joinBtn;
    boolean dialogOk = false;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("thumbnail/");

    String product_text, price_text, url_text, content_text, time_text;
    int progress_int, people_int;

    public HomePostFragment() {
    }


    public static HomePostFragment newInstance(String product, String price, String url, int progressbar, int people, String content, String time) {
        HomePostFragment fragment = new HomePostFragment();
        Bundle args = new Bundle();
        args.putString("product", product);
        args.putString("price", price);
        args.putString("url", url);
        args.putInt("progressbar", progressbar);
        args.putInt("people", people);
        args.putString("content", content);
        args.putString("time", time);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_post, container, false);

        if(getArguments() != null){
            product_text = getArguments().getString("product");
            price_text = getArguments().getString("price");
            url_text = getArguments().getString("url");
            progress_int = getArguments().getInt("progressbar");
            people_int = getArguments().getInt("people");
            content_text = getArguments().getString("content");
            time_text = getArguments().getString("time");
        }

        img01 = view.findViewById(R.id.img01);
        storageRef = storageRef.child(time_text + "/thumbnail1.jpg");
        Glide.with(getActivity()).load(storageRef).into(img01);

        product = view.findViewById(R.id.product);
        product.setText(product_text);

        price = view.findViewById(R.id.price);
        price.setText(price_text);

        url = view.findViewById(R.id.url);
        url.setText(url_text);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setMax(progress_int);
        progressBar.setProgress(people_int);

        people = view.findViewById(R.id.people);
        people.setText(String.valueOf(progress_int) + "명 중 " + String.valueOf(people_int) + people.getText());

        content = view.findViewById(R.id.content);
        content.setText(content_text);

        joinBtn = view.findViewById(R.id.joinBtn);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomHomePostDialog customDialog2 = new CustomHomePostDialog(getActivity());
                customDialog2.callFunction("수량을 입력해주세요");

                CustomHomePostDialog customDialog = new CustomHomePostDialog(getActivity());
                customDialog.callFunction("옵션을 입력해주세요");
            }
        });

        Log.i("dialogOk",String.valueOf(dialogOk));
        if(dialogOk) {
            CustomHomePostDialog customHomePostDialog = new CustomHomePostDialog(getActivity());
            customHomePostDialog.callFunction("수량을 입력해주세요");
        }
        return view;
    }

}
