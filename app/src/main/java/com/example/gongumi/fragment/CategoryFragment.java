package com.example.gongumi.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.gongumi.R;
import com.example.gongumi.activity.MainActivity;
import com.example.gongumi.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.support.constraint.Constraints.TAG;
import static com.example.gongumi.fragment.SearchFragment.search_pos;

public class CategoryFragment extends Fragment {
    private DatabaseReference mDatabase;
    private Post post;
    EditText search_edit;
    ArrayList<String> tags = new ArrayList<>();
    String str, ch;
    int index;
    String hashtg[] = new String[8];
    HashMap<String, Integer> hash= new HashMap<String, Integer>();
    Button hashtag_btn[] = new Button[8];

    public CategoryFragment() {
        // Required empty public constructor
    }

    public static CategoryFragment newInstance(Post post){
        CategoryFragment fragment = new CategoryFragment();
        Bundle bundle =  new Bundle(1);
        bundle.putSerializable("post", post);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        search_pos = 1;
        if(getArguments() != null) {
            post = (Post) getArguments().getSerializable("post");
            ((MainActivity)getActivity()).post = post;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Post");
        hashtag_btn[0] = view.findViewById(R.id.btn_hashtag1);
        hashtag_btn[1] = view.findViewById(R.id.btn_hashtag2);
        hashtag_btn[2] = view.findViewById(R.id.btn_hashtag3);
        hashtag_btn[3] = view.findViewById(R.id.btn_hashtag4);
        hashtag_btn[4] = view.findViewById(R.id.btn_hashtag5);
        hashtag_btn[5] = view.findViewById(R.id.btn_hashtag6);
        hashtag_btn[6] = view.findViewById(R.id.btn_hashtag7);
        hashtag_btn[7] = view.findViewById(R.id.btn_hashtag8);
        ImageButton searchbtn = view.findViewById(R.id.btn_search);

        hashtag_btn[0].setOnClickListener(tagbutton);
        hashtag_btn[1].setOnClickListener(tagbutton);
        hashtag_btn[2].setOnClickListener(tagbutton);
        hashtag_btn[3].setOnClickListener(tagbutton);
        hashtag_btn[4].setOnClickListener(tagbutton);
        hashtag_btn[5].setOnClickListener(tagbutton);
        hashtag_btn[6].setOnClickListener(tagbutton);
        hashtag_btn[7].setOnClickListener(tagbutton);
        searchbtn.setOnClickListener(tagsearch);

        search_edit = view.findViewById(R.id.edit_search);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tags.clear();

                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    post = data.getValue(Post.class);
                    str = String.valueOf(post.getHashtag());
                    Log.d("getHashtag", str);
                    str = str.replace(" ", "");
                    tags.add(str);

                    puthashtag();
                }

            }//ondatachange

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        mDatabase.addValueEventListener(postListener);

        return view;
    }

    View.OnClickListener tagbutton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button btn = (Button) v;
            String btntext = btn.getText().toString();

            Toast.makeText(getContext(), btntext + "(으)로 검색합니다", Toast.LENGTH_SHORT).show();

            Fragment fragment = new SearchFragment();
            Bundle args = new Bundle(1);
            args.putString("tagString", btntext);
            fragment.setArguments(args);

            ((MainActivity)getActivity()).replaceFragment(SearchFragment.newInstance(post));
        }
    };

    View.OnClickListener tagsearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String word = search_edit.getText().toString();
            if(word.indexOf("#") != 0){
                Toast.makeText(getContext(), "검색어 앞에 #을 넣어주세요", Toast.LENGTH_SHORT).show();
            }else if(word.contains(" ")){
                Toast.makeText(getContext(), "공백을 제외하고 입력해주세요", Toast.LENGTH_SHORT).show();
            }else if(getCharNumber(word,'#') > 1){
                Toast.makeText(getContext(), "검색어를 하나만 입력해주세요", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(), word + "(으)로 검색합니다", Toast.LENGTH_SHORT).show();

                Fragment fragment = new SearchFragment();
                Bundle args = new Bundle(1);
                args.putString("tagString", word);
                fragment.setArguments(args);

                ((MainActivity)getActivity()).replaceFragment(SearchFragment.newInstance(post));
            }
        }
    };

    public int getCharNumber(String str, char c)
    {
        int count = 0;
        for(int i=0; i < str.length(); i++) {
            if(str.charAt(i) == c)
                count++;
            else
                continue;
        }
        return count;
    }

    public void puthashtag(){
        int s = 0;

        while (tags.size() > s){ //arraylist 크기만큼 반복
            String[] tag = tags.get(s).split("#");  //"#" 을 가지고 문장 나누기

            for(int i = 0; i < tag.length; i++) {
                if (hash.containsKey(tag[i])) { //키를 포함하고 있는지 확인
                    hash.put(tag[i], hash.get(tag[i]) + 1); //있으면 value값 +1
                } else {
                    hash.put(tag[i], 1);
                } //없으면 등록

                if (TextUtils.isEmpty(hashtg[0])) {
                    for (int j = hashtg.length - 1; j >= 0; j--) {
                        if (!(TextUtils.isEmpty(hashtg[j]))) { //배열이 차있으면 넘긴다
                            continue;
                        } else {  //비어있으면 배열에 값을 넣고 반복을 빠져나온다
                            hashtg[j] = tag[i];
                            break;
                        }
                    } //배열을 역순으로 돌린다
                } //버튼에 넣을 배열값이 한칸 이라도 비어있으면
            }//for

            index = 0;
            if(TextUtils.isEmpty(ch)) //ch가 비어있으면 값을 넣어줌
                ch = hashtg[index];
            int j = 0;

            while (!(TextUtils.isEmpty(tag[j]))) { //tag배열이 안 비어있으면
                for (int i = 0; i < hashtg.length; i++) {
                    if (hash.get(hashtg[i]) < hash.get(ch)) {
                        ch = hashtg[i];
                        index = i;
                    }
                } //버튼에 넣을 배열에서 태그된 숫자가 가장 작은 키를 ch에 넣어준다
                //나중에 바꿔줄것을 대비해 index도 저장해놓은다

                for (int i = 0; i < tag.length; i++) {
                    for(int z = 0; z < hashtg.length; z++){
                        if(hashtg[z] == tag[i])
                            break;
                    }//똑같은 단어가 배열안에 있으면 넣지 않는다
                    if (hash.get(ch) < hash.get(tag[i])) {
                        hashtg[index] = tag[i];
                        ch = tag[i];
                    }
                } //tag배열과 비교하여 더 많이 사용된 해쉬태그가 있다면 배열값과 함께 ch값을 바꿔준다
            }
            s++;
        }

        for(int i = 0; i < hashtg.length; i++){
            String st = "#" + hashtg[i];
            hashtag_btn[i].setText(st);
            hashtag_btn[i].setVisibility(View.VISIBLE);
        }//버튼에 배열의 값을 옮겨담는다
    }
}