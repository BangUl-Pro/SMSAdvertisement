package com.ironfactory.smsmasterapplication.controllers.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ironfactory.smsmasterapplication.Global;
import com.ironfactory.smsmasterapplication.R;
import com.ironfactory.smsmasterapplication.entities.UserEntity;
import com.ironfactory.smsmasterapplication.networks.SocketListener;
import com.ironfactory.smsmasterapplication.networks.SocketManager;

public class SetMemberActivity extends AppCompatActivity {

    private static final String TAG = "SetMemberActivity";

    private UserEntity userEntity;

    private EditText idInput;
    private EditText pwInput;
    private Button deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_member);
        setSupportActionBar((Toolbar) findViewById(R.id.activity_set_member_toolbar));

        Log.d(TAG, "액티비티 시작");
        init();
    }

    private void init() {
        userEntity = getIntent().getParcelableExtra(Global.USER);

        idInput = (EditText) findViewById(R.id.activity_set_member_id);
        pwInput = (EditText) findViewById(R.id.activity_set_member_pw);
        deleteBtn = (Button) findViewById(R.id.activity_set_member_delete);

        idInput.setText(userEntity.getId());

        setListener();
    }

    private void setListener() {
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketManager.deleteUserReq(userEntity.getId(), new SocketListener.OnDeleteUserReq() {
                    @Override
                    public void onSuccess() {
                        finish();
                        Log.d(TAG, "계정 삭제 요청 성공");
                    }

                    @Override
                    public void onException() {
                        Toast.makeText(getApplicationContext(), "계정 삭제 요청에 실패했습니다", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "계정 삭제 요청 실패");
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_set_member, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int resId = item.getItemId();
        if (resId == R.id.action_set_member_submit) {
            // 확인버튼 클릭
            String id = idInput.getText().toString();
            String pw = pwInput.getText().toString();

            if (TextUtils.isEmpty(id)) {
                Toast.makeText(getApplicationContext(), "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (TextUtils.isEmpty(pw)) {
                Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                return false;
            }

            // 아이디가 바뀌었다면
            if (!id.equals(userEntity.getId())) {
                SocketManager.updateUserId(userEntity.getId(), id, new SocketListener.OnUpdateUserId() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "유저 아이디 변경 성공");
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "유저 아이디 변경 실패");
                        Toast.makeText(getApplicationContext(), "아이디 변경에 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // 비밀번호가 바뀌었다면
            if (!pw.equals(userEntity.getPassword())) {
                SocketManager.updateUserPw(userEntity.getId(), pw, new SocketListener.OnUpdateUserPw() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "유저 비밀번호 변경 성공");
                        finish();
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "유저 비밀번호 변경 실패");
                        Toast.makeText(getApplicationContext(), "비밀번호 변경에 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
