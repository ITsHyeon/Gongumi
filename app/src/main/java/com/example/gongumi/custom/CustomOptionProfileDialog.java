package com.example.gongumi.custom;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gongumi.R;

public class CustomOptionProfileDialog {
    private Context context;
    private ImageView imageView_profile;
    private TextView textView_name;
    private TextView textView_option;
    private TextView textView_quantity;

    public CustomOptionProfileDialog(Context context) {
        this.context = context;
    }

    public void showDialog(String profileUrl, String name, String option, String quantity) {
        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dialog = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dialog.setContentView(R.layout.custom_option_profile_dialog);

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        imageView_profile = dialog.findViewById(R.id.imageview_option_profile);
        textView_name = dialog.findViewById(R.id.textview_option_profile_name);
        textView_option = dialog.findViewById(R.id.textview_option_profile_option);
        textView_quantity = dialog.findViewById(R.id.textview_option_profile_quantity);

        Glide.with(context)
                .load(profileUrl)
                .apply(new RequestOptions().error(R.drawable.profile_photo))
                .apply(new RequestOptions().circleCrop())
                .into(imageView_profile);

        textView_name.setText(name);
        textView_option.setText("옵션 : " + option);
        textView_quantity.setText("수량 : " + quantity);

        // 커스텀 다이얼로그를 노출한다.
        dialog.show();
    }

}
