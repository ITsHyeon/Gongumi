package com.example.gongumi.chat;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.example.gongumi.R;
import com.example.gongumi.adapter.OptionListRecyclerViewAdapter;
import com.example.gongumi.adapter.PostThumbnailRecyclerViewAdapter;
import com.example.gongumi.custom.CustomOptionProfileDialog;
import com.example.gongumi.fragment.PostFragment;
import com.example.gongumi.model.Chat;
import com.example.gongumi.model.OnImageRecyclerViewItemClickListener;
import com.example.gongumi.model.Option;
import com.example.gongumi.model.Post;
import com.example.gongumi.model.User;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.example.gongumi.activity.MainActivity.THUMBNAIL_PHOTO_REQUEST_CODE;

public class MessageActivity extends AppCompatActivity implements OnImageRecyclerViewItemClickListener {

    // 채팅
    private String destinationUid;
    private String destinationUid2;
    private Button mBtSendMessage;
    private EditText mEtInputMessage;

    // 이미지 채팅
    private Button btn_pic;
    private RecyclerView recyclerView_pic;
    private PostThumbnailRecyclerViewAdapter pic_adapter;
    private ArrayList<Uri> pic_list;
    final public int IMG_LIMIT = 3;

    // 공구 리스트 보기
    private RelativeLayout layout_option;
    private RecyclerView recyclerView_option;
    private OptionListRecyclerViewAdapter option_adapter;
    private ArrayList<Option> option_list;

    // Glide
    private RequestManager requestManager;

    // 이미지 원본 크기
    private RelativeLayout layout_picture;
    private Button btn_prev_pic;
    private ImageView imageView_origin;

    // Toolbar
    private Button btn_prev;
    private TextView textView_chatroom;
    private Button btn_list;

    private String uid;
    private String chatRoomName;

    private RecyclerView mRvMessage;

    private Post post;
    private Chat chat;

    // 시간 표시
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    private ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 액션바 없애기
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_message);

        // TODO : 각 해당 chatroom을 불러와서 추가되어 있는 user들을 setting하고, 그 방에 commet를 넣는다
        Intent intent = getIntent();
        post = (Post) intent.getSerializableExtra("post");
        chat = (Chat) intent.getSerializableExtra("chat");

      /*  Log.e("Post", post.getUserId());
        Log.e("Chat", chat.users.toString());*/

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 채팅을 요구하는 아이디 즉 단말기에 로그인된 UID

//        chatRoomName = String.valueOf(post.getStartDay().getTime());
        chatRoomName = String.valueOf(post.getStartDay().getTime());
        users = new ArrayList<>();

//        destinationUid = getIntent().getStringExtra("destinationUid"); // 채팅을 당하는 아이디

        textView_chatroom = findViewById(R.id.text_chatroom);
        textView_chatroom.setText(post.getProduct());

        btn_prev = findViewById(R.id.btn_prev);
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layout_option.getVisibility() == View.VISIBLE) {
                    layout_option.setVisibility(View.GONE);
                    btn_list.setVisibility(View.VISIBLE);
                }
                else {
                    finish();
                }
            }
        });

        mBtSendMessage = findViewById(R.id.message_btn);
        mEtInputMessage = findViewById(R.id.message_edit);

        mRvMessage = findViewById(R.id.message_recyclerview);
        mRvMessage.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
        requestManager = Glide.with(this);
        mRvMessage.setAdapter(new RecyclerViewAdapter(requestManager));
        Log.d("keyset : ", chat.users.keySet().toString());
        mBtSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chat chat = new Chat();

                Chat.Comment comment = new Chat.Comment();
                comment.uid = uid;
                comment.message = mEtInputMessage.getText().toString().trim();
                // comment.timestamp = ServerValue.TIMESTAMP;
                comment.timestamp = new Date().getTime();
                if(pic_list.size() < 1) {
                    if(comment.message.equals("")) {
                        Toast.makeText(MessageActivity.this, "메세지를 입력해주세요", Toast.LENGTH_SHORT).show();
                        mEtInputMessage.setText("");
                        return;
                    }
                    uploadComment(comment);
                }
                else {
                    comment.message = comment.timestamp + comment.message;
                    uploadPicandComment(comment.timestamp, pic_list, comment);
                }
//                    Log.e("room : ", chatRoomUid);
            }
        });

        // 이미지 가져오기
        btn_pic = findViewById(R.id.btn_pic);
        pic_list = new ArrayList<>();
        pic_adapter = new PostThumbnailRecyclerViewAdapter(getApplicationContext(), pic_list);
        recyclerView_pic = findViewById(R.id.recyclerview_pic);
        recyclerView_pic.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView_pic.setAdapter(pic_adapter);

        btn_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicture();
            }
        });

        // 원본 이미지
        layout_picture = findViewById(R.id.layout_picture);
        btn_prev_pic = findViewById(R.id.btn_prev_pic);
        imageView_origin = findViewById(R.id.imageView_origin);
        btn_prev_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_picture.setVisibility(View.GONE);
            }
        });

        // 공구 리스트
        btn_list = findViewById(R.id.btn_list);
        layout_option = findViewById(R.id.layout_option_list);
        recyclerView_option = findViewById(R.id.recyclerview_option_list);

        option_list = new ArrayList<>();
        option_adapter = new OptionListRecyclerViewAdapter(MessageActivity.this, option_list);
        recyclerView_option.setAdapter(option_adapter);
        recyclerView_option.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_option.setVisibility(View.VISIBLE);
                btn_list.setVisibility(View.GONE);
            }
        });

        getOptionList();
    }

    public void getOptionList() {
        getUser();

        FirebaseDatabase.getInstance().getReference().child("Post").child(chatRoomName).child("join").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                option_list.clear();
                option_list.add(new Option("", "멤버", "옵션", "수량"));

                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    Option option = data.getValue(Option.class);

                    for(User user : users) {
                        if(data.getKey().equals(user.getId())) {
                            option.setUrl(user.getProfileUrl());
                            option.setName(user.getName());
                            option_list.add(option);
                            break;
                        }
                    }
                }

                option_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void uploadComment(Chat.Comment comment) {
        Log.d("pic_comment", comment.message);
        FirebaseDatabase.getInstance().getReference().child("Post").child(chatRoomName).child("chatroom").child(chatRoomName).child("comment").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mEtInputMessage.setText("");
            }
        });
    }

    public void getPicture() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            Log.d("test", "dd");
        }
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(Intent.createChooser(intent, "Get Album"), THUMBNAIL_PHOTO_REQUEST_CODE);
    }

    public void uploadPicandComment(long time, ArrayList<Uri> list, Chat.Comment comment) {
        recyclerView_pic.setVisibility(View.GONE);

        StorageReference mStorageRef;
        for (int i = 0; i < list.size(); i++) {
            final int position = i;
            mStorageRef = FirebaseStorage.getInstance().getReference().child("message/" + chatRoomName + "/" + time + "/pic" + (i + 1) + ".jpg");
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .build();

            UploadTask uploadTask = mStorageRef.putFile(list.get(i), metadata);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Toast.makeText(SignUpActivity.this, "성공", Toast.LENGTH_SHORT).show();
                    Log.d("pic 사진 업로드", "성공");
                    mRvMessage.getAdapter().notifyItemChanged(position);
                }
            });
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(SignUpActivity.this, "실패", Toast.LENGTH_SHORT).show();
                    Log.e("pic 사진 업로드", "실패");
                }
            });
        }
        uploadComment(comment);
        pic_list.clear();
    } // uploadProfilePhoto()

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case THUMBNAIL_PHOTO_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        recyclerView_pic.setVisibility(View.VISIBLE);
                        if (clipData.getItemCount() > IMG_LIMIT) {
                            Toast.makeText(this, "사진은 최대 " + IMG_LIMIT + "장까지만 선택할 수 있습니다", Toast.LENGTH_SHORT).show();
                        } else {
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                pic_adapter.addPostThumbnailAdapter(clipData.getItemAt(i).getUri());
                            }
                            pic_adapter.notifyDataSetChanged();
                        }
                    } else {
                        //Toast.makeText(this, "이 기기는 사진을 여러 장 선택할 수 없습니다", Toast.LENGTH_SHORT).show();
                        if (data.getData() != null) {
                            recyclerView_pic.setVisibility(View.VISIBLE);
                            Log.d("test", "getData");
                            pic_adapter.addPostThumbnailAdapter(data.getData());
                            pic_adapter.notifyDataSetChanged();
                        }
                    }
                }
                break;
        }
    }

    /* void checkChatRoom() {
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
    }*/

    public void getUser() {
        users.clear();

        FirebaseDatabase.getInstance().getReference().child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    // String user: chats.get(position).users.keySet()
                    for (String key : chat.users.keySet()) {
                        if (key.equals(user.getUid())) {
                            users.add(user);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(Uri uri) {
        Glide.with(getApplicationContext())
                .load(uri)
                .into(imageView_origin);
        layout_picture.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if(layout_option.getVisibility() == View.VISIBLE) {
            layout_option.setVisibility(View.GONE);
            btn_list.setVisibility(View.VISIBLE);
        }
        else if(layout_picture.getVisibility() == View.VISIBLE) {
            layout_picture.setVisibility(View.GONE);
        }
        else {
            super.onBackPressed();
        }
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<Chat.Comment> comments;
        User user;
        RequestManager requestManager;

        public RecyclerViewAdapter(RequestManager requestManager) {
            this.requestManager = requestManager;
            comments = new ArrayList<>();

            getUser();
            getMessageList();
//            FirebaseDatabase.getInstance().getReference().child("User").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    user = dataSnapshot.getValue(User.class);
//                    getMessageList();
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
        }

        void getMessageList() {
            FirebaseDatabase.getInstance().getReference().child("Post").child(chatRoomName).child("chatroom").child(chatRoomName).child("comment").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    comments.clear();

                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        comments.add(item.getValue(Chat.Comment.class));
                    }

                    // 메세지가 갱신
                    notifyDataSetChanged();

                    mRvMessage.scrollToPosition(comments.size() - 1);
                    Log.d("getMesssageList", comments.size() + " " + chatRoomName + " " + post.getStartDay().getTime());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

//            FirebaseDatabase.getInstance().getReference().child("Post").child(chatRoomName).child("chatroom").child(chatRoomName).child("comment").addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                    comments.add(dataSnapshot.getValue(Chat.Comment.class));
//                    notifyDataSetChanged();
//
//                    mRvMessage.scrollToPosition(comments.size() - 1);
//                }
//
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                }
//
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                }
//
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);

            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            final MessageViewHolder messageViewHolder = ((MessageViewHolder) holder);

            Glide.with(getApplicationContext())
                    .load(R.drawable.loading)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                    .into(new DrawableImageViewTarget(messageViewHolder.imageView_loading));

            messageViewHolder.imageView_loading.setVisibility(View.VISIBLE);

            final String timestamp = comments.get(position).timestamp + "";
            final String message = comments.get(position).message;

            // 사진 가져오기
            if(message.length() >= timestamp.length() && message.substring(0, timestamp.length()).equals(timestamp)) {
                ArrayList<Uri> pic_list = new ArrayList<>();
                final PostThumbnailRecyclerViewAdapter pic_adapter;
                pic_adapter = new PostThumbnailRecyclerViewAdapter(getApplicationContext(), pic_list, MessageActivity.this);
                messageViewHolder.recyclerView_pic.setAdapter(pic_adapter);
                messageViewHolder.recyclerView_pic.setVisibility(View.GONE);

//                if(position == comments.size() - 1) {
//                    Log.d("getPicView" + position, messageViewHolder.recyclerView_pic.getVisibility() + ", " + messageViewHolder.textView_message.getVisibility());
//                }

                for (int i = IMG_LIMIT - 1; i >= 0; i--) {
                    Log.d(position + "getPic", position + "");
                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("message/" + chatRoomName + "/" + comments.get(position).timestamp + "/pic" + (i + 1) + ".jpg");
                    if(i == 0) {
                        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                pic_adapter.addPostThumbnailAdapter(uri);
                                pic_adapter.notifyDataSetChanged();

                                Log.d("getDownloadPic" + position, uri.toString());
                                messageViewHolder.imageView_loading.setVisibility(View.GONE);
                                messageViewHolder.recyclerView_pic.setVisibility(View.VISIBLE);

                                // 사진 + 메세지인 경우
                                if(message.length() > timestamp.length()) {
                                    messageViewHolder.textView_message.setText(message.substring(timestamp.length()));
                                    messageViewHolder.textView_message.setTextSize(17);

                                    messageViewHolder.textView_message.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                    else {
                        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                pic_adapter.addPostThumbnailAdapter(uri);
                                pic_adapter.notifyDataSetChanged();

                                Log.d("getDownloadPic" + position, uri.toString());
                            }
                        });
                    }
                }
                messageViewHolder.textView_message.setVisibility(View.GONE);
            }
            else {
                Log.d("getPicView_message", messageViewHolder.textView_message.getVisibility() + "");
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(17);

                messageViewHolder.imageView_loading.setVisibility(View.GONE);
                messageViewHolder.recyclerView_pic.setVisibility(View.GONE);
                messageViewHolder.textView_message.setVisibility(View.VISIBLE);
            }

            // 내가 보낸 메세지
            if (comments.get(position).uid.equals(uid)) {
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.custom_message);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                // 상대방이 보낸 메세지
            } else {
                FirebaseDatabase.getInstance().getReference("User").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        users.add(user);

                        for (final User user1 : users){
                            if (comments.get(position).uid.equals(user1.getUid())){
                                // RequestManager requestManager = Glide.with(holder.itemView.getContext());

                                requestManager.load(user1.getProfileUrl())
                                        .apply(new RequestOptions().error(R.drawable.profile_photo))
                                        .apply(new RequestOptions().circleCrop())
                                        .into(messageViewHolder.imageView_profile);

                                messageViewHolder.imageView_profile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FirebaseDatabase.getInstance().getReference().child("Post").child(chatRoomName).child("join").orderByKey().equalTo(user1.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Option option;
                                                for(DataSnapshot data : dataSnapshot.getChildren()) {
                                                    option = data.getValue(Option.class);
                                                    CustomOptionProfileDialog dialog = new CustomOptionProfileDialog(MessageActivity.this);
                                                    dialog.showDialog(user1.getProfileUrl(), user1.getName(), option.getOpt(), option.getQty());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });

//                                Glide.with(holder.itemView.getContext())
//                                        .load(R.drawable.profile_photo)
//                                        .apply(new RequestOptions().circleCrop())
//                                        .into(messageViewHolder.imageView_profile);
                                messageViewHolder.textView_name.setText(user1.getName());
                                Log.d("db comment uid", comments.get(position).uid);
                                Log.d("db user uid", user1.getUid());
                                Log.d("db user Id", user1.getId());
                            }


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.custom_message_others);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
            }


            // 시간 포맷 설정
            long unixTime = comments.get(position).timestamp;
//            if(comments.get(position).time > 0) {
//                unixTime = comments.get(position).time;
//            }
//            else {
//                unixTime = (long) comments.get(position).timestamp;
//            }
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
            public ImageView imageView_loading;
            public RecyclerView recyclerView_pic;
//            public PostThumbnailRecyclerViewAdapter pic_adapter;
//            public ArrayList<Uri> pic_list;

            public MessageViewHolder(View view) {
                super(view);
                textView_message = view.findViewById(R.id.messageItem_testView_message);
                textView_name = view.findViewById(R.id.messageItem_textView_name);
                imageView_profile = view.findViewById(R.id.messageItem_imageView_profile);
                linearLayout_destination = view.findViewById(R.id.messageItem_linearLayout_destination);
                linearLayout_main = view.findViewById(R.id.messageItem_linearLayout_main);
                textView_timestamp = view.findViewById(R.id.messageItem_textView_timestamp);
                imageView_loading = view.findViewById(R.id.messageItem_loading);
                recyclerView_pic = view.findViewById(R.id.messageItem_recyclerview_pic);

//                pic_list = new ArrayList<>();
//                pic_adapter = new PostThumbnailRecyclerViewAdapter(getApplicationContext(), pic_list);
                recyclerView_pic.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
//                recyclerView_pic.setAdapter(pic_adapter);
            }
        }
    }
}
