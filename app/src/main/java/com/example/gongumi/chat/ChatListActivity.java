package com.example.gongumi.chat;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gongumi.R;
import com.example.gongumi.adapter.ChatListRecyclerViewAdapter;
import com.example.gongumi.model.Chat;
import com.example.gongumi.model.ChatList;
import com.example.gongumi.model.Post;
import com.example.gongumi.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class ChatListActivity extends AppCompatActivity {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm");

    private RecyclerView recyclerView;
    private ChatListRecyclerViewAdapter chatListRecyclerViewAdapter;
    private ArrayList<ChatList> chatLists;

    private DatabaseReference databaseRef;

    private String userId;

    private Button btn_prev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 액션바 없애기
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_chat_list);

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userId = email.substring(0, email.indexOf('@'));
        Log.d("userUid", userId);
        databaseRef = FirebaseDatabase.getInstance().getReference().child("User").child(userId).child("chatlist");
        recyclerView = findViewById(R.id.chatlist_recyclerview);
        chatLists = new ArrayList<>();
        chatListRecyclerViewAdapter = new ChatListRecyclerViewAdapter(ChatListActivity.this, chatLists);

        getChatDatabase();

        recyclerView.setAdapter(chatListRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btn_prev = findViewById(R.id.btn_prev);
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void getChatDatabase() {
        ValueEventListener chatValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                for(DataSnapshot dataSnapshot : dataSnapshots.getChildren()) {
                    chatLists.clear();
                    final String chatroom = dataSnapshot.getKey();
                    final Chat chatLastReadTime = dataSnapshot.getValue(Chat.class);

                    String url = "thumbnail/" + chatroom + "/thumbnail1.jpg";

                    final ChatList chatList = new ChatList();
                    chatList.setThumbnailUrl(url);

                    FirebaseDatabase.getInstance().getReference().child("Post").orderByKey().equalTo(chatroom).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Post post = dataSnapshot.getValue(Post.class);
                            chatList.setPost(post);

                            FirebaseDatabase.getInstance().getReference().child("Post").child(chatroom).child("chatroom").addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    final Chat chat = dataSnapshot.getValue(Chat.class);
                                    chat.lastReadTime = chatLastReadTime.lastReadTime;

                                    FirebaseDatabase.getInstance().getReference().child("Post").child(chatroom).child("chatroom").child(chatroom).child("comment").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for(DataSnapshot data : dataSnapshot.getChildren()) {
                                                chat.comments.put(data.getKey(), data.getValue(Chat.Comment.class));
                                            }
                                            int i = 0;
                                            for(i = 0; i < chatLists.size(); i++) {
                                                if(chatLists.get(i).getPost().getStartDay().getTime() == chatList.getPost().getStartDay().getTime()) {
                                                    chatList.setChat(chat);
                                                    chatLists.set(i, chatList);
                                                    Log.d("chatLists",chatroom + " " + i);
                                                    chatListRecyclerViewAdapter.notifyItemChanged(i);
                                                    break;
                                                }
                                            }
                                            if(i >= chatLists.size()) {
                                                chatList.setChat(chat);
                                                chatLists.add(chatList);
                                                Log.d("chatLists",chatroom + " " + (chatLists.size() - 1));
                                                chatListRecyclerViewAdapter.notifyDataSetChanged();
                                            }
//                                            if(chatLists.contains(chatList)) {
//                                                int index = chatLists.indexOf(chatList);
//                                                chatList.setChat(chat);
//                                                chatLists.set(index, chatList);
//                                                Log.d("chatLists",chatroom + " " + index);
//                                                chatListRecyclerViewAdapter.notifyItemChanged(index);
//                                            }
//                                            else {
//                                                chatList.setChat(chat);
//                                                chatLists.add(chatList);
//                                                Log.d("chatLists",chatroom + " " + (chatLists.size() - 1));
//                                                chatListRecyclerViewAdapter.notifyDataSetChanged();
//                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
//        ChildEventListener chatEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                chatLists.clear();
//                chatListRecyclerViewAdapter.notifyDataSetChanged();
//                final String chatroom = dataSnapshot.getKey();
//                final Chat chatLastReadTime = dataSnapshot.getValue(Chat.class);
//
//                String url = "thumbnail/" + chatroom + "/thumbnail1.jpg";
//
//                final ChatList chatList = new ChatList();
//                chatList.setThumbnailUrl(url);
//
//                FirebaseDatabase.getInstance().getReference().child("Post").orderByKey().equalTo(chatroom).addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                        Post post = dataSnapshot.getValue(Post.class);
//                        chatList.setPost(post);
//
//                        FirebaseDatabase.getInstance().getReference().child("Post").child(chatroom).child("chatroom").addChildEventListener(new ChildEventListener() {
//                            @Override
//                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                                final Chat chat = dataSnapshot.getValue(Chat.class);
//                                chat.lastReadTime = chatLastReadTime.lastReadTime;
//
//                                FirebaseDatabase.getInstance().getReference().child("Post").child(chatroom).child("chatroom").child(chatroom).child("comment").addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        for(DataSnapshot data : dataSnapshot.getChildren()) {
//                                            chat.comments.put(data.getKey(), data.getValue(Chat.Comment.class));
//                                        }
//                                        if(chatLists.contains(chatList)) {
//                                            int index = chatLists.indexOf(chatList);
//                                            chatList.setChat(chat);
//                                            chatLists.set(index, chatList);
//                                            Log.d("chatLists",chatroom + " " + index);
//                                            chatListRecyclerViewAdapter.notifyItemChanged(index);
//                                        }
//                                        else {
//                                            chatList.setChat(chat);
//                                            chatLists.add(chatList);
//                                            Log.d("chatLists",chatroom + " " + (chatLists.size() - 1));
//                                            chatListRecyclerViewAdapter.notifyDataSetChanged();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//
//                            }
//
//                            @Override
//                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                            }
//
//                            @Override
//                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                            }
//
//                            @Override
//                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                chatLists.clear();
//                chatListRecyclerViewAdapter.notifyDataSetChanged();
//                final String chatroom = dataSnapshot.getKey();
//                final Chat chatLastReadTime = dataSnapshot.getValue(Chat.class);
//
//                String url = "thumbnail/" + chatroom + "/thumbnail1.jpg";
//
//                final ChatList chatList = new ChatList();
//                chatList.setThumbnailUrl(url);
//
//                FirebaseDatabase.getInstance().getReference().child("Post").orderByKey().equalTo(chatroom).addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                        Post post = dataSnapshot.getValue(Post.class);
//                        chatList.setPost(post);
//
//                        FirebaseDatabase.getInstance().getReference().child("Post").child(chatroom).child("chatroom").addChildEventListener(new ChildEventListener() {
//                            @Override
//                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                                final Chat chat = dataSnapshot.getValue(Chat.class);
//                                chat.lastReadTime = chatLastReadTime.lastReadTime;
//
//                                FirebaseDatabase.getInstance().getReference().child("Post").child(chatroom).child("chatroom").child(chatroom).child("comment").addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        for(DataSnapshot data : dataSnapshot.getChildren()) {
//                                            chat.comments.put(data.getKey(), data.getValue(Chat.Comment.class));
//                                        }
//                                        if(chatLists.contains(chatList)) {
//                                            int index = chatLists.indexOf(chatList);
//                                            chatList.setChat(chat);
//                                            chatLists.set(index, chatList);
//                                            Log.d("chatLists",chatroom + " " + index);
//                                            chatListRecyclerViewAdapter.notifyItemChanged(index);
//                                        }
//                                        else {
//                                            chatList.setChat(chat);
//                                            chatLists.add(chatList);
//                                            Log.d("chatLists",chatroom + " " + (chatLists.size() - 1));
//                                            chatListRecyclerViewAdapter.notifyDataSetChanged();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//
//                            }
//
//                            @Override
//                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                            }
//
//                            @Override
//                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                            }
//
//                            @Override
//                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };

        // databaseRef.addChildEventListener(chatEventListener);
        databaseRef.addValueEventListener(chatValueListener);
    }

//    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//        // 채팅목록
//        private List<Chat> chats = new ArrayList<>();
//        private String uid;
//        private ArrayList<String> destinationUsers = new ArrayList<>();
//
//        public ChatRecyclerViewAdapter() {
//            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    chats.clear();
//                    for (DataSnapshot item : dataSnapshot.getChildren()){
//                        chats.add(item.getValue(Chat.class));
//                    }
//                    notifyDataSetChanged();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//
//        }
//
//        @NonNull
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,parent,false);
//            return new CustomViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
//            final CustomViewHolder customViewHolder = (CustomViewHolder)holder;
//            String destinationUid = "whwACuY42kRCpkQU6I4REQfpDMF3";
//
//            // 일일 챗방에 있는 유저를 체크
//            /*for (String user: chats.get(position).users.keySet()){
//                if (!user.equals(uid)){
//                    destinationUid = user;
//                    destinationUsers.add(destinationUid);
//                }
//            }*/
//            FirebaseDatabase.getInstance().getReference().child("User").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    User user = dataSnapshot.getValue(User.class);
//                    // 이름이랑 이미지 주소, 프로필 사진
//                    /*Glide.with(customViewHolder.itemView.getContext())
//                            .load(user.)// 사용자 프로필 이미지 주소
//                            .apply(new RequestOptions().circleCrop())
//                            .into(customViewHolder.imageView);
//                    */
//                    customViewHolder.textView_title.setText(user.getName());
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//
//            // 메세지를 내림차순으로 정렬 후 마지막 메세지의 키값을 가져옴옴
//            Map<String, Chat.Comment> commentMap = new TreeMap<>(Collections.<String>reverseOrder());
//            commentMap.putAll(chats.get(position).comments);
//            String lastMessageKey = (String) commentMap.keySet().toArray()[0];
//            customViewHolder.textView_last_message.setText(chats.get(position).comments.get(lastMessageKey).message);
//
//            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(ChatListActivity.this, MessageActivity.class);
//                    // 누구랑 대화할지 넘겨주기
////                    intent.putExtra("destinationUid", destinationUsers.get(position));
//                    intent.putExtra("destinationUid", "whwACuY42kRCpkQU6I4REQfpDMF3");
//
//                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(ChatListActivity.this, R.anim.fromright, R.anim.toleft);
//                    startActivity(intent, activityOptions.toBundle());
//                }
//            });
//            // 시간 포맷
//            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
//
//            long unixTime = (long) chats.get(position).comments.get(lastMessageKey).timestamp;
//
//            Date date = new Date(unixTime);
//            customViewHolder.textView_timestamp.setText(simpleDateFormat.format(date));
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return chats.size();
//        }
//
//        private class CustomViewHolder extends RecyclerView.ViewHolder {
//            public ImageView imageView;
//            public TextView textView_title;
//            public TextView textView_last_message;
//            public TextView textView_timestamp;
//
//            public CustomViewHolder(View view) {
//                super(view);
//
//                imageView = findViewById(R.id.chatitem_imageview);
//                textView_title = findViewById(R.id.chatitem_textview);
//                textView_last_message = findViewById(R.id.chatitem_textview_last_message);
//                textView_timestamp = findViewById(R.id.chatitem_textview_timestamp);
//
//            }
//        }
//    }
}