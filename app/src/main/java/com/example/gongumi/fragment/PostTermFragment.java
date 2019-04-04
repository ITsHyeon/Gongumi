package com.example.gongumi.fragment;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gongumi.R;
import com.example.gongumi.model.Post;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.gongumi.fragment.PostFragment.post_pos;


public class PostTermFragment extends Fragment {

    private Post post;
    private Button btn_previous, btn_next;
    private TextView textView_start_year, textView_start_month, textView_start_day;
    private Spinner spinner_end_year, spinner_end_month, spinner_end_day;
    private ArrayAdapter<Integer> spinnerAdapter_Year, spinnerAdapter_Month, spinnerAdapter_Day;
    private ArrayList<Integer> spinner_item_year, spinner_item_month, spinner_item_day;
    private Calendar calendar = Calendar.getInstance();
    private int select = 0;

    public PostTermFragment() {
        // Required empty public constructor
    }

    public static PostTermFragment newInstance(Post post) {
        PostTermFragment fragment = new PostTermFragment();
        Bundle bundle =  new Bundle(1);
        bundle.putSerializable("post", post);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        post_pos = 2;
        if(getArguments() != null) {
            post = (Post) getArguments().getSerializable("post");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_term, container, false);
        spinner_item_year = new ArrayList<>();
        spinner_item_month = new ArrayList<>();
        spinner_item_day = new ArrayList<>();

        spinnerAdapter_Year = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, spinner_item_year);
        spinnerAdapter_Month = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, spinner_item_month);
        spinnerAdapter_Day = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, spinner_item_day);
        setSpinnerItems();

        btn_previous = view.findViewById(R.id.btn_previous);
        btn_next = view.findViewById(R.id.btn_next);
        textView_start_year = view.findViewById(R.id.text_startYear);
        textView_start_month = view.findViewById(R.id.text_startMonth);
        textView_start_day = view.findViewById(R.id.text_startDay);
        spinner_end_year = view.findViewById(R.id.spinner_EndYear);
        spinner_end_month = view.findViewById(R.id.spinner_EndMonth);
        spinner_end_day = view.findViewById(R.id.spinner_EndDay);

        textView_start_year.setText(calendar.get(Calendar.YEAR) + "년");
        textView_start_month.setText(calendar.get(Calendar.MONTH) + "월");
        textView_start_day.setText(calendar.get(Calendar.DATE) + "일");

        spinner_end_year.setAdapter(spinnerAdapter_Year);
        spinner_end_month.setAdapter(spinnerAdapter_Month);
        spinner_end_day.setAdapter(spinnerAdapter_Day);

        spinner_end_month.setOnItemSelectedListener(spinnerItemSelectedListener);

        btn_previous.setOnClickListener(ChangeFragmentClickListener);
        btn_next.setOnClickListener(ChangeFragmentClickListener);

        return view;
    }

    View.OnClickListener ChangeFragmentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btn_previous:
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_post, PostCategoryFragment.newInstance(post));
                    transaction.commit();
                    break;
                case R.id.btn_next:
                    transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_post, PostNumberFragment.newInstance(post));
                    transaction.addToBackStack("post_term");
                    transaction.commit();
                    break;
            }
        }
    };

    AdapterView.OnItemSelectedListener spinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            select =  (int) parent.getItemAtPosition(position);
            setSpinnerItemDay(select);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void setSpinnerItems() {
        spinner_item_year.clear();
        spinner_item_year.add(calendar.get(Calendar.YEAR));
        spinnerAdapter_Year.notifyDataSetChanged();

        spinner_item_month.clear();
        for (int i = calendar.get(Calendar.MONTH); i <= calendar.get(Calendar.MONTH) + 2; i++) {
            spinner_item_month.add(i);
        }
        spinnerAdapter_Month.notifyDataSetChanged();
    }

    public void setSpinnerItemDay(int select) {
        spinner_item_day.clear();
        int lastday = 0;
        switch (select) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                lastday = 31;
                break;
            case 2:
                lastday = 29;
            default:
                lastday = 30;
        }
        for(int i = 1; i <= lastday; i++) {
            spinner_item_day.add(i);
        }
        spinnerAdapter_Day.notifyDataSetChanged();
    }


}
