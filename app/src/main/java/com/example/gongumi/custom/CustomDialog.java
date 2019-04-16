package com.example.gongumi.custom;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gongumi.R;

// https://sharp57dev.tistory.com/10
public class CustomDialog {

    private Context context;
    public TextView text;
    private EditText message;

    public CustomDialog(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(final TextView mainLabel){

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dialog = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dialog.setContentView(R.layout.custom_dialog);

        // 커스텀 다이얼로그를 노출한다.
        dialog.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        text = dialog.findViewById(R.id.tvInput);
        message = dialog.findViewById(R.id.etInput);
        Button btOk = dialog.findViewById(R.id.btOk);
        Button btCancel = dialog.findViewById(R.id.btCancel);

        // TODO : 호출된 것에 따라 제목 변경
//        text.setText();


        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // '확인' 버튼 클릭시 메인 액티비티에서 설정한 main_label에
                // 커스텀 다이얼로그에서 입력한 메세지를 대입한다.
                mainLabel.setText(message.getText().toString());
                Toast.makeText(context,"\"" + message.getText().toString() + "\"을 입력하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

}
