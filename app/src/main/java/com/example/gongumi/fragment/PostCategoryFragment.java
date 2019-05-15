package com.example.gongumi.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gongumi.R;
import com.example.gongumi.activity.MainActivity;
import com.example.gongumi.model.Post;

import static com.example.gongumi.fragment.PostFragment.post_pos;

public class PostCategoryFragment extends Fragment {

    private Post post;

    EditText edit_hashtag;
    Button btn_done;
    TextView text_hashtags[] = new TextView[5];

    private int index_hashtag = 0;
    private String hashtags = "";

    public static PostCategoryFragment newInstance(Post post) {
        PostCategoryFragment fragment = new PostCategoryFragment();
        Bundle bundle =  new Bundle(1);
        bundle.putSerializable("post", post);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        post_pos = 1;
        if(getArguments() != null) {
            post = (Post) getArguments().getSerializable("post");
            ((MainActivity)getActivity()).post = post;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_category, container, false);
        edit_hashtag = view.findViewById(R.id.edit_hashtag);
        btn_done = view.findViewById(R.id.btn_done);
        text_hashtags[0] = view.findViewById(R.id.text_hashtag1);
        text_hashtags[1] = view.findViewById(R.id.text_hashtag2);
        text_hashtags[2] = view.findViewById(R.id.text_hashtag3);
        text_hashtags[3] = view.findViewById(R.id.text_hashtag4);
        text_hashtags[4] = view.findViewById(R.id.text_hashtag5);

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkText();
            }
        });

        edit_hashtag.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    checkText();
                }
                return false;
            }
        });


        return view;
    }

    public void hidekeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(edit_hashtag.getWindowToken(), 0);
    }

    public void checkText() {
        String editText = edit_hashtag.getText().toString().trim();
        if(editText.equals("")) {
            Toast.makeText(getContext(), "해시태그를 입력해주세요", Toast.LENGTH_SHORT).show();
        }
        else if(editText.contains(" ")) {
            Toast.makeText(getContext(), "공백을 제외하고 입력해주세요", Toast.LENGTH_SHORT).show();
        }
        else if(index_hashtag > 4) {
            Toast.makeText(getContext(), "해시태그는 최대 5개까지 입력할 수 있습니다", Toast.LENGTH_SHORT).show();
            edit_hashtag.setText("");
            hidekeyboard();
        }
        else {
            if(editText.indexOf("#") != 0) {
                editText = "#" + editText;
            }
            hashtags += editText + " ";
            text_hashtags[index_hashtag].setText(editText);
            text_hashtags[index_hashtag].setVisibility(View.VISIBLE);
            index_hashtag++;
            post.setHashtag(hashtags);
            edit_hashtag.setText("");
            hidekeyboard();
        }
    }

}
