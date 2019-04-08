package com.example.gongumi.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gongumi.R;

import java.util.ArrayList;

public class PostThumbnailRecyclerViewAdapter extends RecyclerView.Adapter<PostThumbnailRecyclerViewAdapter.ViewHolder> {
    Context context;
    ArrayList<Uri> list;

    public PostThumbnailRecyclerViewAdapter(Context context, ArrayList<Uri> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.post_thumbnail_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View v) {
            super(v);


        }
    }
}
