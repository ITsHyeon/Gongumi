package com.example.gongumi.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gongumi.R;
import com.example.gongumi.fragment.HomePostFragment;
import com.example.gongumi.model.Home;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    List<Home> items;
    int item_layout;

    public RecyclerAdapter(Context context, List<Home> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Home item = items.get(position);
        Drawable drawable = ContextCompat.getDrawable(context, item.getThumbnail());
        holder.thumbnail.setBackground(drawable);
        holder.product.setText(item.getProduct());
        holder.price.setText(item.getPrice());
        holder.progressBar.setMax(item.getProgress());
        holder.progressBar.setProgress(item.getPeople());
        holder.people.setText(String.valueOf(item.getPeople()) + "명이 참여했습니다.");
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.frame_home,
                        HomePostFragment.newInstance(item.getProduct(),item.getPrice(),item.getUrl(), item.getProgress(),item.getPeople(), item.getContent())).commit();
                fragmentTransaction.addToBackStack(null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView product;
        TextView price;
        ProgressBar progressBar;
        TextView people;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            product = (TextView) itemView.findViewById(R.id.product);
            price = (TextView) itemView.findViewById(R.id.price);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            people = (TextView) itemView.findViewById(R.id.people);
            cardview = (CardView) itemView.findViewById(R.id.cardview);
        }
    }
}
