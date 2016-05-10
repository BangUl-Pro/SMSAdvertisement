package com.ironfactory.smsmasterapplication.controllers.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ironfactory.smsmasterapplication.Global;
import com.ironfactory.smsmasterapplication.R;
import com.ironfactory.smsmasterapplication.controllers.adapters.SelectGroupAdapter;
import com.ironfactory.smsmasterapplication.entities.GroupEntity;
import com.ironfactory.smsmasterapplication.entities.UserEntity;
import com.ironfactory.smsmasterapplication.networks.SocketListener;
import com.ironfactory.smsmasterapplication.networks.SocketManager;

import java.util.ArrayList;
import java.util.List;

public class SelectGroupActivity extends AppCompatActivity {

    private static final String TAG = "SelectGroupActivity";

    private RecyclerView recyclerView;
    private SelectGroupAdapter adapter;

    private UserEntity userEntity;
    private List<GroupEntity> groupEntities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);
        Log.d(TAG, "액티비티 시작");
        init();
    }


    private void init() {
        setSupportActionBar((Toolbar) findViewById(R.id.activity_select_group_toolbar));
        setTitle("그룹선택");

        groupEntities = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.activity_select_group_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SelectGroupAdapter(getApplicationContext(), groupEntities);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        login();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_select_group_logout) {
            SharedPreferences preferences = getSharedPreferences(Global.APP_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.clear();
            editor.commit();

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void login() {
        SharedPreferences preferences = getSharedPreferences(Global.APP_NAME, MODE_PRIVATE);

        final String id = preferences.getString(Global.ID, null);
        String pw = preferences.getString(Global.PW, null);

        SocketManager.login(id, pw, new SocketListener.OnLogin() {
            @Override
            public void onSuccess(final UserEntity userEntity, final List<GroupEntity> groupEntities) {
                SocketManager.getGroup(Global.MASTER_GROUP_ID, new SocketListener.OnGetGroup() {
                    @Override
                    public void onSuccess(GroupEntity groupEntity) {
                        Log.d(TAG, "최고관리자 그룹 받아오기 성공");
                        boolean isMaster = false;
                        for (String masterId :
                                groupEntity.getMasters()) {
                            Log.d(TAG, "masterId = " + masterId);
                            if (masterId.equals(id)) {
                                isMaster = true;
                                break;
                            }
                        }
                        if (isMaster) {
                            Log.d(TAG, "최고관리자임");
                            Global.USER_TYPE = Global.TYPE_SUPER_MASTER;

                            // 최고권한 관리자라면
                            SocketManager.getAllGroup(new SocketListener.OnGetAllGroup() {
                                @Override
                                public void onSuccess(List<GroupEntity> groupEntities) {
                                    Log.d(TAG, "그룹 다 받아오기 성공");
                                    adapter.setGroupEntities(groupEntities);
                                }

                                @Override
                                public void onException() {
                                    Log.d(TAG, "그룹 다 받아오기 실패");
                                }
                            });
                        } else {
                            Log.d(TAG, "최고관리자 아님");
                            Global.USER_TYPE = Global.TYPE_MASTER;

                            SelectGroupActivity.this.userEntity = userEntity;
                            SelectGroupActivity.this.groupEntities = groupEntities;
                            adapter.setGroupEntities(groupEntities);
                        }
                    }

                    @Override
                    public void onException() {

                    }
                });
            }

            @Override
            public void onException(int code) {

            }
        });
    }
}
