package com.example.gongumi.chat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        setContentView(R.layout.activity_message);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 채팅을 요구하는 아이디 즉 단말기에 로그인된 UID

        destinationUid = getIntent().getStringExtra("destinationUid"); // 채팅을 당하는 아이디
        mBtSendMessage = findViewById(R.id.message_btn);
        mEtInputMessage = findViewById(R.id.message_edit);

        mRvMessage = findViewById(R.id.message_recyclerview);
        mBtSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chat chat = new Chat();
                chat.users.put(uid, true);
                chat.users.put(destinationUid, true);

                if (chatRoomUid == null) {
                    mBtSendMessage.setEnabled(false);
                    // 채팅방 이름 임의로 setting
                    FirebaseDatabase.getInstance().getReference("Chat").child("chatrooms").push().setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                }
            }
        });
        checkChatRoom();
    }

    void checkChatRoom() {
        FirebaseDatabase.getInstance().getReference("Chat").child("chatrooms").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
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

            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
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

        void getMessageList() {
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    comments.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        comments.add(item.getValue(Chat.Comment.class));
                    }
                    // 메세지가 갱신
                    notifyDataSetChanged();

                    mRvMessage.scrollToPosition(comments.size() - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder messageViewHolder = ((MessageViewHolder) holder);

            // 내가 보낸 메세지
            if (comments.get(position).uid.equals(uid)) {
                messageViewHolder.mTvMessage.setText(comments.get(position).message);
                messageViewHolder.mTvMessage.setBackgroundResource(R.drawable.bubble_right);
                messageViewHolder.mLlDestination.setVisibility(View.INVISIBLE);
                messageViewHolder.mLlMain.setGravity(Gravity.RIGHT);
            }
            // 상대방이 보낸 메세지
            else {
                // TODO : 상대방 프로필 이미지 가져오기
//                Glide.with(holder.itemView.getContext())
//                        .load(user.)
//                        .apply(new RequestOptions().circleCrop())
//                        .into(messageViewHolder.mIvProfile);
                messageViewHolder.mTvName.setText(user.getName());
                messageViewHolder.mLlDestination.setVisibility(View.VISIBLE);
                messageViewHolder.mTvMessage.setBackgroundResource(R.drawable.bubble_left);
                messageViewHolder.mTvMessage.setText(comments.get(position).message);
                messageViewHolder.mTvMessage.setTextSize(25);
                messageViewHolder.mLlMain.setGravity(Gravity.LEFT);
            }
            // 시간 포맷 설정
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            messageViewHolder.mTvTimestamp.setText(time);

        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView mTvMessage;
            public TextView mTvName;
            public ImageView mIvProfile;
            public LinearLayout mLlDestination;
            public LinearLayout mLlMain;
            public TextView mTvTimestamp;

            public MessageViewHolder(@NonNull View view) {
                super(view);
                mTvMessage = view.findViewById(R.id.messageItem_testView_message);
                mTvName = view.findViewById(R.id.messageItem_textview_name);
                mIvProfile = view.findViewById(R.id.messageItem_imageview_profile);
                mLlDestination = view.findViewById(R.id.messageItem_linearLayout_destination);
                mLlMain = view.findViewById(R.id.messageItem_linearLayout_main);
                mTvTimestamp = view.findViewById(R.id.messageItem_textview_timestamp);
            }
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fromleft, R.anim.toright);
    }
}
