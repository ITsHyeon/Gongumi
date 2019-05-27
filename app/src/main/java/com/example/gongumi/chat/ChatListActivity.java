package com.example.gongumi.chat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gongumi.R;
import com.example.gongumi.model.Chat;
import com.example.gongumi.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ChatListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        RecyclerView recyclerView = findViewById(R.id.chatlist_recyclerview);
        recyclerView.setAdapter(new ChatRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        // 채팅목록
        private List<Chat> chats = new ArrayList<>();
        private String uid;
        public ChatRecyclerViewAdapter() {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("chatroom").orderByChild("users/"+uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    chats.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()){
                        chats.add(item.getValue(Chat.class));
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,parent,false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final CustomViewHolder customViewHolder = (CustomViewHolder)holder;
            String destinationUid = null;

            // 일일 챗방에 있는 유저를 체크
            for (String user: chats.get(position).users.keySet()){
                if (!user.equals(uid)){
                    destinationUid = user;
                }
            }
            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    // 이름이랑 이미지 주소, 프로필 사진
                    /*Glide.with(customViewHolder.itemView.getContext())
                            .load(user.)// 사용자 프로필 이미지 주소
                            .apply(new RequestOptions().circleCrop())
                            .into(customViewHolder.imageView);
                    */
                    customViewHolder.textView_title.setText(user.getName());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            // 메세지를 내림차순으로 정렬 후 마지막 메세지의 키값을 가져옴옴
           Map<String, Chat.Comment> commentMap = new TreeMap<>(Collections.<String>reverseOrder());
            commentMap.putAll(chats.get(position).comments);
            String lastMessageKey = (String) commentMap.keySet().toArray()[0];
            customViewHolder.textView_last_message.setText(chats.get(position).comments.get(lastMessageKey).message);
        }

        @Override
        public int getItemCount() {
            return chats.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView_title;
            public TextView textView_last_message;

            public CustomViewHolder(View view) {
                super(view);

                imageView = findViewById(R.id.chatitem_imageview);
                textView_title = findViewById(R.id.chatitem_textview);
                textView_last_message = findViewById(R.id.chatitem_textview_last_message);


            }
        }
    }
}
