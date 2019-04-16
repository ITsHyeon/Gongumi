package com.example.gongumi.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gongumi.R;

public class HomePostFragment extends Fragment {
    TextView product;
    TextView price;
    TextView url;
    ProgressBar progressBar;
    TextView people;
    TextView content;

    String product_text, price_text, url_text, content_text;
    int progress_int, people_int;

    public HomePostFragment() {
    }


    public static HomePostFragment newInstance(String product, String price, String url, int progressbar, int people, String content) {
        HomePostFragment fragment = new HomePostFragment();
        Bundle args = new Bundle();
        args.putString("product", product);
        args.putString("price", price);
        args.putString("url", url);
        args.putInt("progressbar", progressbar);
        args.putInt("people", people);
        args.putString("content", content);
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
        }

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


        return view;
    }

}
