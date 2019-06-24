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
import com.example.gongumi.model.JoinList;
import com.example.gongumi.model.PostList;

import java.util.ArrayList;

public class SettingJoinListRecyclerViewAdapter extends RecyclerView.Adapter<SettingJoinListRecyclerViewAdapter.ViewHolder> {
    Context context;
    ArrayList<JoinList> list;


    public SettingJoinListRecyclerViewAdapter(Context context, ArrayList<JoinList> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SettingJoinListRecyclerViewAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_join_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position == 0) {
            holder.textView_post.setBackground(context.getResources().getDrawable(R.drawable.custom_background_list_title));
            holder.textView_term.setBackground(context.getResources().getDrawable(R.drawable.custom_background_list_title));
            holder.textView_option.setBackground(context.getResources().getDrawable(R.drawable.custom_background_list_title));
            holder.textView_quantity.setBackgroundColor(context.getResources().getColor(R.color.mainColor));
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.textView_term.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            holder.textView_term.setLayoutParams(layoutParams);
        }
        holder.textView_post.setText(list.get(position).getPostName());
        holder.textView_term.setText(list.get(position).getTerm());
        holder.textView_option.setText(list.get(position).getOption());
        holder.textView_quantity.setText(list.get(position).getQuantity());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView_post;
        private TextView textView_term;
        private TextView textView_option;
        private TextView textView_quantity;

        public ViewHolder(@NonNull View v) {
            super(v);

            textView_post = v.findViewById(R.id.textview_post);
            textView_term = v.findViewById(R.id.textview_term);
            textView_option = v.findViewById(R.id.textview_option);
            textView_quantity = v.findViewById(R.id.textview_quantity);
        }
    }
}
