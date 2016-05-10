package com.ironfactory.smsapplication.controllers.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ironfactory.smsapplication.Global;
import com.ironfactory.smsapplication.R;
import com.ironfactory.smsapplication.networks.SocketListener;
import com.ironfactory.smsapplication.networks.SocketManager;

public class SignUpActivity extends AppCompatActivity {

    private EditText idView;
    private EditText pwView;
    private EditText confirmView;
    private EditText nameView;
    private EditText phoneView;
    private Button submitView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        idView = (EditText) findViewById(R.id.activity_sign_up_id);
        pwView = (EditText) findViewById(R.id.activity_sign_up_pw);
        confirmView = (EditText) findViewById(R.id.activity_sign_up_confirm);
        nameView = (EditText) findViewById(R.id.activity_sign_up_name);
        phoneView = (EditText) findViewById(R.id.activity_sign_up_phone);
        submitView = (Button) findViewById(R.id.activity_sign_up_submit);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_SMS) !=
                    PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {

                } else {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_SMS}, 1);
                }
            }
        }

        submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idView.getText().toString();
                String pw = pwView.getText().toString();
                String confirm = confirmView.getText().toString();
                String name = nameView.getText().toString();
                String phone = phoneView.getText().toString();

                idView.setError(null);
                pwView.setError(null);
                confirmView.setError(null);
                nameView.setError(null);
                phoneView.setError(null);

                if (TextUtils.isEmpty(id)) {
                    idView.setError("아이디를 입력하세요.");
                    return;
                }

                if (TextUtils.isEmpty(pw)) {
                    pwView.setError("비밀번호를 입력하세요.");
                    return;
                }

                if (!confirm.equals(pw)) {
                    confirmView.setError("비밀번호를 확인하세요.");
                    return;
                }

                if (TextUtils.isEmpty(name)) {
                    pwView.setError("상호명을 입력하세요.");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    pwView.setError("매장 번호를 입력하세요.");
                    return;
                }


                // 회원가입
                SocketManager.signUp(id, pw, name, phone, new SocketListener.OnSignUp() {
                    @Override
                    public void onSuccess(String id) {
                        Toast.makeText(getApplicationContext(), "성공했습니다", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onException(int code) {
                        switch (code) {
                            case Global.CODE_SIGN_UP_NOT_ENOUGH_DATA:
                            case Global.CODE_SIGN_UP_FAIL_TO_WRITE_DB:
                                // 데이터 누락
                                Toast.makeText(getApplicationContext(), "네트워크 상태를 확인하세요", Toast.LENGTH_SHORT).show();
                                break;

                            case Global.CODE_SIGN_UP_OVERLAP_ID:
                                Toast.makeText(getApplicationContext(), "이미 사용중인 아아디입니다", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                String phoneNum = manager.getLine1Number();
                idView.setText(phoneNum);
            }
        }
    }
}
