package com.example.gongumi.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gongumi.R;
import com.example.gongumi.activity.MainActivity;
import com.example.gongumi.chat.MessageActivity;
import com.example.gongumi.model.Chat;
import com.example.gongumi.model.ChatList;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class ChatListRecyclerViewAdapter extends RecyclerView.Adapter<ChatListRecyclerViewAdapter.ViewHolder> {

    Context context;
    ArrayList<ChatList> chatList = new ArrayList<>();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm");

    public ChatListRecyclerViewAdapter(Context context, ArrayList<ChatList> chatList) {
        this.context = context;
        this.chatList = chatList;


        notifyDataSetChanged();
    }

    public void setChatList(ArrayList<ChatList> chatList) {
        this.chatList = chatList;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ChatListRecyclerViewAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Glide.with(context)
                .load(FirebaseStorage.getInstance().getReference().child(chatList.get(position).getThumbnailUrl()))
                .apply(new RequestOptions().error(R.drawable.profile_photo))
                .apply(new RequestOptions().circleCrop())
                .into(holder.image_thumbnail);
        holder.text_chatroom.setText(chatList.get(position).getPost().getProduct());

        // 메세지를 내림차순으로 정렬 후 마지막 메세지의 키값을 가져옴옴
        Map<String, Chat.Comment> commentMap = new TreeMap<>(Collections.<String>reverseOrder());
        commentMap.putAll(chatList.get(position).getChat().comments);
        Log.d(chatList.get(position).getPost().getProduct() + "getChat", chatList.get(position).getChat().comments.size() +  "");
        Log.d("getUser", chatList.get(position).getChat().users.size() + "");
        if(commentMap.size() > 0) {
            String lastMessageKey = (String) commentMap.keySet().toArray()[0];
            if(chatList.get(position).getChat().comments.get(lastMessageKey).message.equals(chatList.get(position).getChat().comments.get(lastMessageKey).timestamp + "")) {
                holder.text_lastMessage.setText("사진을 보냈습니다");
            }
            else {
                holder.text_lastMessage.setText(chatList.get(position).getChat().comments.get(lastMessageKey).message);
            }

            // 시간 포맷
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

            long unixTime = (long) chatList.get(position).getChat().comments.get(lastMessageKey).timestamp;

            Date date = new Date(unixTime);
            holder.text_lastTime.setText(simpleDateFormat.format(date));
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatList.get(position).getChat().comments = new HashMap<>();
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("post", chatList.get(position).getPost());
                intent.putExtra("chat", chatList.get(position).getChat());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layout;
        public ImageView image_thumbnail;
        public TextView text_chatroom;
        public TextView text_lastTime;
        public TextView text_lastMessage;

        public ViewHolder(@NonNull View v) {
            super(v);

            layout = v.findViewById(R.id.chatitem_linearlayout);
            image_thumbnail = v.findViewById(R.id.chatitem_imageview);
            text_chatroom = v.findViewById(R.id.chatitem_textview);
            text_lastTime = v.findViewById(R.id.chatitem_textview_timestamp);
            text_lastMessage = v.findViewById(R.id.chatitem_textview_last_message);
        }
    }
}
