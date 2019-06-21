package com.example.gongumi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gongumi.R;
import com.example.gongumi.model.PostList;

import java.util.ArrayList;

public class SettingPostListRecyclerViewAdapter extends RecyclerView.Adapter<SettingPostListRecyclerViewAdapter.ViewHolder> {
    Context context;
    ArrayList<PostList> list;


    public SettingPostListRecyclerViewAdapter(Context context, ArrayList<PostList> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SettingPostListRecyclerViewAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_post_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position == 0) {
            holder.textView_post.setBackgroundColor(context.getResources().getColor(R.color.mainColor));
            holder.textView_term.setBackgroundColor(context.getResources().getColor(R.color.mainColor));
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.textView_term.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            holder.textView_term.setLayoutParams(layoutParams);
        }
        holder.textView_post.setText(list.get(position).getPostName());
        holder.textView_term.setText(list.get(position).getTerm());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView_post;
        private TextView textView_term;

        public ViewHolder(@NonNull View v) {
            super(v);

            textView_post = v.findViewById(R.id.textview_post);
            textView_term = v.findViewById(R.id.textview_term);
        }
    }
}
