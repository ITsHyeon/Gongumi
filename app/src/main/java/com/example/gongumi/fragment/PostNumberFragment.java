package com.example.gongumi.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.gongumi.R;
import com.example.gongumi.activity.MainActivity;
import com.example.gongumi.model.Post;

import static com.example.gongumi.fragment.PostFragment.post_pos;


public class PostNumberFragment extends Fragment {

    private Post post;
    private RelativeLayout layout;
    private EditText edit_num;

    public PostNumberFragment() {
        // Required empty public constructor
    }

    public static PostNumberFragment newInstance(Post post) {
        PostNumberFragment fragment = new PostNumberFragment();
        Bundle bundle =  new Bundle(1);
        bundle.putSerializable("post", post);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        post_pos = 3;
        if(getArguments() != null) {
            post = (Post) getArguments().getSerializable("post");
            ((MainActivity)getActivity()).post = post;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_number, container, false);
        layout = view.findViewById(R.id.layout_post_number);
        edit_num = view.findViewById(R.id.edit_num);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkText();
            }
        });
        edit_num.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    checkText();
                }
                return false;
            }
        });

        if(post.getNum() != 0) {
            edit_num.setText(post.getNum() + "");
        }

        return view;
    }

    public void hidekeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(edit_num.getWindowToken(), 0);
    }

    public boolean checkText() {
        String text = edit_num.getText().toString().trim().equals("") ? "0" : edit_num.getText().toString().trim();
        int num = Integer.parseInt(text);

        if(num <= 1) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.setMessage("2 이상의 숫자를 입력해주세요");
            alert.show();

            edit_num.getText().clear();
            edit_num.hasFocus();
            return false;
        }
        else {
            post.setNum(num);
            hidekeyboard();
            return true;
        }
    }

    public void checkTextPrevious() {
        String text = edit_num.getText().toString().trim().equals("") ? "0" : edit_num.getText().toString().trim();
        int num = Integer.parseInt(text);

        if(num >= 2) {
            post.setNum(num);
        }
        else {
            post.setNum(0);
        }
        hidekeyboard();
    }

}
