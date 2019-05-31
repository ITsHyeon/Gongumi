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
        setContentView(R.layout.activity_message);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 채팅을 요구하는 아이디 즉 단말기에 로그인된 UID

//        destinationUid = getIntent().getStringExtra("destinationUid"); // 채팅을 당하는 아이디
        destinationUid = "6BbWjdcRrKO4aDUqL5Z0luW2vJ33";
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
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment);
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
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<Chat.Comment> comments;

        public RecyclerViewAdapter(){
            comments = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("commets").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    comments.clear();

                    for (DataSnapshot item : dataSnapshot.getChildren()){
                        comments.add(item.getValue(Chat.Comment.class));
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
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
