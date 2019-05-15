package com.example.gongumi.fragment;

import android.content.Intent;
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
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gongumi.R;
import com.example.gongumi.custom.CustomDialog;
import com.example.gongumi.custom.CustomHomePostDialog;
import com.example.gongumi.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    ViewFlipper flipper;
    ImageView img01, img02, img03;
    Button joinBtn;
    boolean dialogOk = false;
    private User user;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("thumbnail/");
    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Post/");

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

        flipper = view.findViewById(R.id.flipper);
        flipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("viewflipper click","true");
                flipper.showNext();
                }
        });


        img01 = view.findViewById(R.id.img01);
        storageRef = storageRef.child(time_text + "/thumbnail1.jpg");
        Glide.with(getActivity()).load(storageRef).apply(new RequestOptions().error(null)).into(img01);

        img02 = view.findViewById(R.id.img02);
        storageRef = storageRef.child(time_text + "/thumbnail2.jpg");
        Glide.with(getActivity()).load(storageRef).apply(new RequestOptions().error(null)).into(img02);

        img03 = view.findViewById(R.id.img03);
        storageRef = storageRef.child(time_text + "/thumbnail3.jpg");
        Glide.with(getActivity()).load(storageRef).apply(new RequestOptions().error(null)).into(img03);

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

        Intent intent = getActivity().getIntent();
        user = (User) intent.getSerializableExtra("user");

        mDatabaseRef = mDatabaseRef.child(time_text + "/join/" + user.getId());

        joinBtn = view.findViewById(R.id.btJoin);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomHomePostDialog customDialog2 = new CustomHomePostDialog(getActivity());
                customDialog2.callFunction("수량을 입력해주세요", "qty", user.getId(), time_text);

                CustomHomePostDialog customDialog = new CustomHomePostDialog(getActivity());
                customDialog.callFunction("옵션을 입력해주세요", "option", user.getId(), time_text);
            }
        });



        return view;
    }

}
