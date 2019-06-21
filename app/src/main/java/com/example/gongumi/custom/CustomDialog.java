package com.example.gongumi.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gongumi.R;
import com.example.gongumi.fragment.SettingFragment;
import com.example.gongumi.model.CustomDialogInterface;
import com.example.gongumi.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

// https://sharp57dev.tistory.com/10
public class CustomDialog {
    private CustomDialogInterface customDialogInterface;

    private Context context;
    public TextView text;
    public EditText message;
    public String changeValue;
    private User user;
    Map<String, Object> values = new HashMap<>();
    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("User/");
    public CustomDialog(Context context) {
        this.context = context;
    }
    public CustomDialog(Context context, CustomDialogInterface customDialogInterface) {
        this.context = context;
        this.customDialogInterface = customDialogInterface;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(String editText, final String key, final String name){

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dialog = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dialog.setContentView(R.layout.custom_dialog);

        // 커스텀 다이얼로그의 사이즈를 지정한다.
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        // 커스텀 다이얼로그를 노출한다.
        dialog.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        text = dialog.findViewById(R.id.tvInput);
        message = dialog.findViewById(R.id.etInput);
        Button btOk = dialog.findViewById(R.id.btOk);
        Button btCancel = dialog.findViewById(R.id.btCancel);

        Intent intent = ((Activity) context).getIntent();
        user = (User) intent.getSerializableExtra("user");

        text.setText(editText);

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // '확인' 버튼 클릭시 메인 액티비티에서 설정한 main_label에
                // 커스텀 다이얼로그에서 입력한 메세지를 대입한다.
                Toast.makeText(context,"\"" + message.getText().toString() + "\"을 입력하였습니다.", Toast.LENGTH_SHORT).show();
                Log.v("dialog info message", message.getText().toString());
                Log.v("dialog info name", name);
                Log.v("dialog info key", key);

                changeValue = message.getText().toString().trim();
                user.setName(changeValue);

                mDatabaseRef = mDatabaseRef.child("/"+name+"/");
                values.put(key, changeValue);
                mDatabaseRef.updateChildren(values);

                if(customDialogInterface != null) {
                    customDialogInterface.onPositiveClick();
                }
                Log.v("dialog info user", user.getId());

                Log.i("dialog info dbPath", mDatabaseRef.toString());
                Log.i("dialog info values", String.valueOf(values));

                dialog.dismiss();
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
