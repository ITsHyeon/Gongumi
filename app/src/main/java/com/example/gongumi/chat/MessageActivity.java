package com.example.gongumi.chat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gongumi.R;
import com.example.gongumi.model.Chat;
import com.example.gongumi.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MessageActivity extends AppCompatActivity {

    // 채팅
    private String destinationUid;
    private Button mBtSendMessage;
    private EditText mEtInputMessage;

    private String uid;
    private String chatRoomUid;

    private RecyclerView mRvMessage;

    // 시간 표시
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 액션바 없애기
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_message);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 채팅을 요구하는 아이디 즉 단말기에 로그인된 UID

//        destinationUid = getIntent().getStringExtra("destinationUid"); // 채팅을 당하는 아이디
        if (uid=="whwACuY42kRCpkQU6I4REQfpDMF3"){
            destinationUid = "IO1VPyWi9XSxBb81rwNdOFjxiGR2";
        } else{
            destinationUid = "whwACuY42kRCpkQU6I4REQfpDMF3";
        }
        mBtSendMessage = findViewById(R.id.message_btn);
        mEtInputMessage = findViewById(R.id.message_edit);

        mRvMessage = findViewById(R.id.message_recyclerview);

        // TODO : 채팅방 Setting Post에서 글쓰기 완료버튼을 누르면 생기게 만들기
        mBtSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chat chat = new Chat();
                chat.users.put(uid, true);
                chat.users.put(destinationUid, true);

                if (chatRoomUid == null) {
                    mBtSendMessage.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });
                } else {
                    Chat.Comment comment = new Chat.Comment();
                    comment.uid = uid;
                    comment.message = mEtInputMessage.getText().toString();
                    comment.timestamp = ServerValue.TIMESTAMP;
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mEtInputMessage.setText("");
                        }
                    });

//                    Log.e("room : ", chatRoomUid);
                }

            }
        });


    }

    void checkChatRoom() {
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Chat chat = item.getValue(Chat.class);
                    if (chat.users.containsKey(destinationUid)) {
                        chatRoomUid = item.getKey();
                        mBtSendMessage.setEnabled(true);
                        mRvMessage.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        mRvMessage.setAdapter(new RecyclerViewAdapter());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<Chat.Comment> comments;
        User user;

        public RecyclerViewAdapter() {
            comments = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("User").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    getMessageList();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        void getMessageList(){
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    comments.clear();

                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        comments.add(item.getValue(Chat.Comment.class));
                    }

                    // 메세지가 갱신
                    notifyDataSetChanged();

                    mRvMessage.scrollToPosition(comments.size()-1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);

            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder messageViewHolder = ((MessageViewHolder)holder);
            // 내가 보낸 메세지
            if (comments.get(position).uid.equals(uid)){
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.bubble_right);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.textView_message.setTextSize(25);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                // 상대방이 보낸 메세지
            } else{
                if (destinationUid=="whwACuY42kRCpkQU6I4REQfpDMF3"){
                    Glide.with(holder.itemView.getContext())
                            .load("https://firebasestorage.googleapis.com/v0/b/gongumi-6995f.appspot.com/o/user_profile%2Fs2017s02.jpg?alt=media&token=28b901ae-ac42-4270-be12-b0d6dd2d415e")
                            .apply(new RequestOptions().circleCrop())
                            .into(messageViewHolder.imageView_profile);
                    messageViewHolder.textView_name.setText("가슬");

                } else{
                    Glide.with(holder.itemView.getContext())
                            .load("https://firebasestorage.googleapis.com/v0/b/gongumi-6995f.appspot.com/o/user_profile%2Fsuhyeon917917.jpg?alt=media&token=1320d74e-069a-4310-a2c9-2ea5522b592f")
                            .apply(new RequestOptions().circleCrop())
                            .into(messageViewHolder.imageView_profile);
                    messageViewHolder.textView_name.setText("수현");

                }

                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.bubble_left);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(25);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
            }

            // 시간 포맷 설정
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            messageViewHolder.textView_timestamp.setText(time);
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public TextView textView_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;

            public MessageViewHolder(View view) {
                super(view);
                textView_message = view.findViewById(R.id.messageItem_testView_message);
                textView_name = view.findViewById(R.id.messageItem_textView_name);
                imageView_profile = view.findViewById(R.id.messageItem_imageView_profile);
                linearLayout_destination = view.findViewById(R.id.messageItem_linearLayout_destination);
                linearLayout_main = view.findViewById(R.id.messageItem_linearLayout_main);
                textView_timestamp = view.findViewById(R.id.messageItem_textView_timestamp);

            }
        }
    }
}
