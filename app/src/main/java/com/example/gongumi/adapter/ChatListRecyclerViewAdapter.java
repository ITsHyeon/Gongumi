package com.example.gongumi.adapter;

import android.app.Activity;
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
    ArrayList<ChatList> chatLists = new ArrayList<>();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm");

    public ChatListRecyclerViewAdapter(Context context, ArrayList<ChatList> chatLists) {
        this.context = context;
        this.chatLists = chatLists;


        notifyDataSetChanged();
    }

    public void setChatList(ArrayList<ChatList> chatList) {
        this.chatLists = chatList;

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
                .load(FirebaseStorage.getInstance().getReference().child(chatLists.get(position).getThumbnailUrl()))
                .apply(new RequestOptions().error(R.drawable.profile_photo))
                .apply(new RequestOptions().circleCrop())
                .into(holder.image_thumbnail);
        holder.text_chatroom.setText(chatLists.get(position).getPost().getProduct());

        // 메세지를 내림차순으로 정렬 후 마지막 메세지의 키값을 가져옴옴
        Map<String, Chat.Comment> commentMap = new TreeMap<>(Collections.<String>reverseOrder());
        commentMap.putAll(chatLists.get(position).getChat().comments);
        Log.d(chatLists.get(position).getPost().getProduct() + "getChat", chatLists.get(position).getChat().comments.size() +  "");
        Log.d("getUser", chatLists.get(position).getChat().users.size() + "");
        if(commentMap.size() > 0) {
            String lastMessageKey = (String) commentMap.keySet().toArray()[0];
            if(chatLists.get(position).getChat().comments.get(lastMessageKey).message.equals(chatLists.get(position).getChat().comments.get(lastMessageKey).timestamp + "")) {
                holder.text_lastMessage.setText("사진을 보냈습니다");
            }
            else if(chatLists.get(position).getChat().comments.get(lastMessageKey).message.length() >= (chatLists.get(position).getChat().comments.get(lastMessageKey).timestamp + "").length() && chatLists.get(position).getChat().comments.get(lastMessageKey).message.contains(chatLists.get(position).getChat().comments.get(lastMessageKey).timestamp + "")) {
                holder.text_lastMessage.setText(chatLists.get(position).getChat().comments.get(lastMessageKey).message.substring((chatLists.get(position).getChat().comments.get(lastMessageKey).timestamp + "").length()));
            }
            else {
                holder.text_lastMessage.setText(chatLists.get(position).getChat().comments.get(lastMessageKey).message);
            }

            // 시간 포맷
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

            long unixTime = (long) chatLists.get(position).getChat().comments.get(lastMessageKey).timestamp;

            Date date = new Date(unixTime);
            holder.text_lastTime.setText(simpleDateFormat.format(date));

            Log.d("chatlistsize", chatLists.size() + "");
            if(chatLists.get(position).getChat().lastReadTime == 0) {
                if(commentMap.size() > 50) {
                    holder.text_lastreadTime.setText("50+");
                }
                else {
                    holder.text_lastreadTime.setText(commentMap.size() + "");
                }




            } else {
                int count = 0;
                for(int i = 0; i < commentMap.size(); i++) {
                    String key = (String) commentMap.keySet().toArray()[i];
                    long time = chatLists.get(position).getChat().comments.get(key).timestamp;
                    Log.d("chatlisttime", chatLists.get(position).getChat().lastReadTime + " " + time);
                    if(chatLists.get(position).getChat().lastReadTime >= time) {
                        if(commentMap.size() > 50) {
                            holder.text_lastreadTime.setText("50+");
                            holder.text_lastreadTime.setVisibility(View.VISIBLE);
                        }
                        else if(count > 0) {
                            holder.text_lastreadTime.setText(count + "");
                            holder.text_lastreadTime.setVisibility(View.VISIBLE);
                        }
                        else {
                            holder.text_lastreadTime.setVisibility(View.GONE);
                        }
                        break;
                    }
                    count++;
                }
            }
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatLists.get(position).getChat().comments = new HashMap<>();
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("post", chatLists.get(position).getPost());
                intent.putExtra("chat", chatLists.get(position).getChat());
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.fromright, R.anim.toleft);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layout;
        public ImageView image_thumbnail;
        public TextView text_chatroom;
        public TextView text_lastTime;
        public TextView text_lastMessage;
        public TextView text_lastreadTime;

        public ViewHolder(@NonNull View v) {
            super(v);

            layout = v.findViewById(R.id.chatitem_linearlayout);
            image_thumbnail = v.findViewById(R.id.chatitem_imageview);
            text_chatroom = v.findViewById(R.id.chatitem_textview);
            text_lastTime = v.findViewById(R.id.chatitem_textview_timestamp);
            text_lastMessage = v.findViewById(R.id.chatitem_textview_last_message);
            text_lastreadTime = v.findViewById(R.id.chatitem_textview_lastreadtime);
        }
    }
}
