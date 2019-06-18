package com.example.gongumi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gongumi.R;
import com.example.gongumi.model.Option;

import java.util.ArrayList;

public class OptionListRecyclerViewAdapter extends RecyclerView.Adapter<OptionListRecyclerViewAdapter.ViewHolder> {

    Context context;
    ArrayList<Option> list;

    public OptionListRecyclerViewAdapter(Context context, ArrayList<Option> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new OptionListRecyclerViewAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_option, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position == 0) {
            holder.textView_name.setBackgroundColor(context.getResources().getColor(R.color.mainColor));
            holder.textView_option.setBackgroundColor(context.getResources().getColor(R.color.mainColor));
            holder.textView_quantity.setBackgroundColor(context.getResources().getColor(R.color.mainColor));
            holder.imageView_profile.setVisibility(View.GONE);
            holder.textView_name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        else {
            Glide.with(context)
                .load(list.get(position).getUrl())
                .apply(new RequestOptions().error(R.drawable.profile_photo))
                .apply(new RequestOptions().circleCrop())
                .into(holder.imageView_profile);
        }
        holder.textView_name.setText(list.get(position).getName());
        holder.textView_option.setText(list.get(position).getOpt());
        holder.textView_quantity.setText(list.get(position).getQty());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView_name;
        private TextView textView_option;
        private TextView textView_quantity;
        private ImageView imageView_profile;

        public ViewHolder(@NonNull View view) {
            super(view);

            textView_name = view.findViewById(R.id.textview_name);
            textView_option = view.findViewById(R.id.textview_option);
            textView_quantity = view.findViewById(R.id.textview_quantity);
            imageView_profile = view.findViewById(R.id.imageview_option_profile);
        }
    }
}
