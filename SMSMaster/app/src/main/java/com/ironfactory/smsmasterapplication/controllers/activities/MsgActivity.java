package com.ironfactory.smsmasterapplication.controllers.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ironfactory.smsmasterapplication.Global;
import com.ironfactory.smsmasterapplication.R;
import com.ironfactory.smsmasterapplication.controllers.adapters.MsgAdapter;
import com.ironfactory.smsmasterapplication.entities.MsgEntity;
import com.ironfactory.smsmasterapplication.entities.UserEntity;
import com.ironfactory.smsmasterapplication.networks.SocketListener;
import com.ironfactory.smsmasterapplication.networks.SocketManager;

import java.util.List;

public class MsgActivity extends AppCompatActivity {

    private static final String TAG = "MsgActivity";

    private UserEntity userEntity;

    private RecyclerView recyclerView;
    private MsgAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        Log.d(TAG, "액티비티 시작");
        init();
    }

    private void init() {
        userEntity = getIntent().getParcelableExtra(Global.USER);

        recyclerView = (RecyclerView) findViewById(R.id.activity_msg_recycler);

        setRecyclerView();
    }


    private void setRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new MsgAdapter(getApplicationContext(), userEntity);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMsg();
    }

    private void getMsg() {
        SocketManager.getMsg(userEntity.getId(), new SocketListener.OnGetMsg() {
            @Override
            public void onSuccess(List<MsgEntity> msgEntities) {
                Log.d(TAG, "메세지 받아오기 성공");

                userEntity.setMsgEntities(msgEntities);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onException() {
                Log.d(TAG, "메세지 받아오기 실패");
            }
        });
    }
}
