package com.example.gongumi.fragment;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gongumi.R;
import com.example.gongumi.activity.MainActivity;
import com.example.gongumi.model.Post;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.gongumi.fragment.PostFragment.post_pos;


public class PostTermFragment extends Fragment {

    private Post post;
    private TextView textView_start_year, textView_start_month, textView_start_day;
    private Spinner spinner_end_year, spinner_end_month, spinner_end_day;
    private ArrayAdapter<Integer> spinnerAdapter_Year, spinnerAdapter_Month, spinnerAdapter_Day;
    private ArrayList<Integer> spinner_item_year, spinner_item_month, spinner_item_day;
    private Calendar calendar = Calendar.getInstance();
    private int month_select = 0;
    public int[] select = new int[3];

    private String end = "";
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date endDay;

    private boolean isFirst = false;

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
            ((MainActivity)getActivity()).post = post;
            post.setStartDay(new Date());
            if(post.getEndDay() != null)
                Log.d("test", post.getEndDay().toString());
            Log.d("test", post.getCategory());
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

        textView_start_year = view.findViewById(R.id.text_startYear);
        textView_start_month = view.findViewById(R.id.text_startMonth);
        textView_start_day = view.findViewById(R.id.text_startDay);
        spinner_end_year = view.findViewById(R.id.spinner_EndYear);
        spinner_end_month = view.findViewById(R.id.spinner_EndMonth);
        spinner_end_day = view.findViewById(R.id.spinner_EndDay);

        textView_start_year.setText(calendar.get(Calendar.YEAR) + "년");
        textView_start_month.setText(calendar.get(Calendar.MONTH) + 1 + "월");
        textView_start_day.setText(calendar.get(Calendar.DATE) + "일");

        spinner_end_year.setAdapter(spinnerAdapter_Year);
        spinner_end_month.setAdapter(spinnerAdapter_Month);
        spinner_end_day.setAdapter(spinnerAdapter_Day);

        spinner_end_month.setOnItemSelectedListener(spinnerMonthSelectedListener);
        spinner_end_year.setOnItemSelectedListener(spinnerListener);
        spinner_end_day.setOnItemSelectedListener(spinnerListener);

        if(post.getEndDay() == null) {
            select[0] = calendar.get(Calendar.YEAR);
            select[1] = calendar.get(Calendar.MONTH) + 1;
            select[2] = calendar.get(Calendar.DATE);
        }
        else {
            isFirst = true;
            Log.d("test", "not null");
            Calendar temp = Calendar.getInstance();
            temp.setTime(post.getEndDay());
            select[0] = temp.get(Calendar.YEAR);
            select[1] = temp.get(Calendar.MONTH) + 1;
            select[2] = temp.get(Calendar.DATE);

            spinner_end_month.setSelection(select[1] - calendar.get(Calendar.MONTH) - 1);
        }
        end = select[0] + "-" + (select[1] < 10 ? "0" + select[1] : select[1]) + "-" + select[2] + " 11:59:59";
        try {
            endDay = format.parse(end);
            post.setEndDay(endDay);
//            Toast.makeText(getContext(), post.getEndDay().toString(), Toast.LENGTH_SHORT).show();
//            Toast.makeText(getContext(), post.getStartDay().toString(), Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }

    AdapterView.OnItemSelectedListener spinnerMonthSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d("test", "monthSelect");
            month_select =  (int) parent.getItemAtPosition(position);
            select[1] = month_select;
            setSpinnerItemDay(month_select);
            end = select[0] + "-" + (select[1] < 10 ? "0" + select[1] : select[1]) + "-" + select[2] + " 11:59:59";
            try {
                endDay = format.parse(end);
                post.setEndDay(endDay);
//                Toast.makeText(getContext(), post.getEndDay().toString(), Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                Log.d("test", "error transfer string to date");
                e.printStackTrace();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d("test", "selectDay");
            if(parent.getId() == R.id.spinner_EndYear) {
                select[0] = (int) parent.getItemAtPosition(position);
            }
            else {
                select[2] = (int) parent.getItemAtPosition(position);
            }

            end = select[0] + "-" + (select[1] < 10 ? "0" + select[1] : select[1]) + "-" + select[2] + " 11:59:59";
            try {
                endDay = format.parse(end);
                post.setEndDay(endDay);
//                Toast.makeText(getContext(), post.getEndDay().toString(), Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                Log.d("test", "error transfer string to date");
                e.printStackTrace();
            }
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
        //Calendar.MONTH : 0부터 시작
        for (int i = calendar.get(Calendar.MONTH) + 1; i <= calendar.get(Calendar.MONTH) + 3; i++) {
            spinner_item_month.add(i);
        }
        spinnerAdapter_Month.notifyDataSetChanged();
        Log.d("test", "setitem");
    }

    public void setSpinnerItemDay(int select) {
        spinner_item_day.clear();
        int startday = 1;
        int lastday = 0;

        // 같은 달일 경우 공구 끝나는 날짜를 오늘과 같은 날짜부터 선택해야함
        if(select == calendar.get(Calendar.MONTH) + 1) {
            startday = calendar.get(Calendar.DATE) + 1;
        }

        // 공구 기간이 2달이 최대기에 마지막 달을 선택한 경우 선택 가능 날짜 마지막 날이 오늘과 같은 날짜가 되어야함
        if(select == calendar.get(Calendar.MONTH) + 3) {
            lastday = calendar.get(Calendar.DATE);
        }
        else {
            calendar.set(Calendar.MONTH, select - 1);
            lastday = calendar.getActualMaximum(Calendar.DATE);
            calendar = Calendar.getInstance();
        }

        for(int i = startday; i <= lastday; i++) {
            spinner_item_day.add(i);
        }

        spinnerAdapter_Day.notifyDataSetChanged();
        if(isFirst) {
            isFirst = false;
            if(this.select[1] == calendar.get(Calendar.MONTH) + 1) {
                Log.d("test", "month");
                spinner_end_day.setSelection(this.select[2] - (calendar.get(Calendar.DATE) + 1));
            }
            else
                spinner_end_day.setSelection(this.select[2] - 1);
        }
        else {
            spinner_end_day.setSelection(0);
            this.select[2] = startday;
        }
    }


}
