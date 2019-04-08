package com.example.gongumi.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gongumi.R;

import java.util.ArrayList;

public class PostThumbnailRecyclerViewAdapter extends RecyclerView.Adapter<PostThumbnailRecyclerViewAdapter.ViewHolder> {
    Context context;
    ArrayList<Uri> list;

    public PostThumbnailRecyclerViewAdapter(Context context, ArrayList<Uri> list) {
        this.context = context;
        this.list = list;
    }

    public void setPostThumbnailAdapter(ArrayList<Uri> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void addPostThumbnailAdapter(Uri uri) {
        if(list.size() < 3) {
            list.add(uri);
            notifyDataSetChanged();
        }
        else {
            Toast.makeText(context, "썸네일은 최대 3개까지만 선택할 수 있습니다", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearList() {
        list.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.post_thumbnail_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Glide.with(context)
             .load(list.get(position))
             .into(viewHolder.image_thumbnail);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView image_thumbnail;

        public ViewHolder(@NonNull View v) {
            super(v);

            image_thumbnail = v.findViewById(R.id.image_thumbnail);
        }
    }
}
