package com.ironfactory.smsmasterapplication.controllers.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ironfactory.smsmasterapplication.Global;
import com.ironfactory.smsmasterapplication.R;
import com.ironfactory.smsmasterapplication.entities.MsgEntity;
import com.ironfactory.smsmasterapplication.networks.SocketListener;
import com.ironfactory.smsmasterapplication.networks.SocketManager;

public class InputActivity extends AppCompatActivity {

    private static final String TAG = "InputActivity";

    private Toolbar toolbarView;
    private EditText smsContentView;
    private EditText phoneNumView;
    private TextView submitView;
    private CheckBox checkBox;

    private MsgEntity msgEntity;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        Log.d(TAG, "액티비티 시작");
        init();
    }


    private void init() {
        msgEntity = getIntent().getParcelableExtra(Global.MSG);
        userId = getIntent().getStringExtra(Global.ID);

        toolbarView = (Toolbar) findViewById(R.id.activity_input_toolbar);
        submitView = (TextView) findViewById(R.id.activity_input_submit);
        smsContentView = (EditText) findViewById(R.id.activity_input_sms_content);
        phoneNumView = (EditText) findViewById(R.id.activity_input_phone_num);
        checkBox = (CheckBox) findViewById(R.id.activity_input_check);

        if (msgEntity != null) {
            if (msgEntity.getPort() != null)
                phoneNumView.setText(msgEntity.getPort());

            if (msgEntity.getContent() != null)
                smsContentView.setText(msgEntity.getContent());

            if (msgEntity.getIsEverybody() == MsgEntity.EVERYONE)
                checkBox.setChecked(true);
        }

        setSupportActionBar(toolbarView);
        setListener();
//        setNoti();
    }


    private void setNoti() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentTitle("M# SMS");
        builder.setContentText("문자 전송 가능");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);

        Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
        intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent2, 0);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, notification);
    }


    private void setListener() {
        submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 확인 버튼 클릭
                smsContentView.setError(null);
                phoneNumView.setError(null);

                int check = (checkBox.isChecked() ? MsgEntity.EVERYONE : MsgEntity.NOT_EVERYONE);

                if (TextUtils.isEmpty(smsContentView.getText().toString())) {
                    smsContentView.setError("내용을 입력해주세요");
                    return;
                }

                if (TextUtils.isEmpty(phoneNumView.getText().toString())) {
                    phoneNumView.setError("포트를 입력해주세요");
                    return;
                }

                if (msgEntity != null) {
                    msgEntity.setContent(smsContentView.getText().toString());
                    msgEntity.setPort(phoneNumView.getText().toString());
                    msgEntity.setIsEverybody(check);

                    SocketManager.updateMsg(msgEntity, new SocketListener.OnUpdateMsg() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "updateMsg 성공");
                            Toast.makeText(getApplicationContext(), "앞으로 이 내용으로 메세지가 전송 됩니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onException() {
                            Log.d(TAG, "updateMsg 실패");
                            Toast.makeText(getApplicationContext(), "메세지 수정 도중 에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                msgEntity = new MsgEntity();
                msgEntity.setContent(smsContentView.getText().toString());
                msgEntity.setPort(phoneNumView.getText().toString());
                msgEntity.setUserId(userId);
                msgEntity.setIsEverybody(check);

                SocketManager.insertMsg(msgEntity, new SocketListener.OnInsertMsg() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "insertMsg 성공");
                        Toast.makeText(getApplicationContext(), "앞으로 이 내용으로 메세지가 전송 됩니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "insertMsg 실패");
                    }
                });
            }
        });
    }
}
