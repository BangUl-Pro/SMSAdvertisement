package com.ironfactory.smsmasterapplication.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ironfactory.smsmasterapplication.Global;
import com.ironfactory.smsmasterapplication.R;
import com.ironfactory.smsmasterapplication.controllers.adapters.ChargeCoinReqAdapter;
import com.ironfactory.smsmasterapplication.controllers.adapters.DeleteMemberReqAdapter;
import com.ironfactory.smsmasterapplication.controllers.adapters.DeleteUserReqAdapter;
import com.ironfactory.smsmasterapplication.controllers.adapters.TopMasterAdapter;
import com.ironfactory.smsmasterapplication.entities.ChargeCoinEntity;
import com.ironfactory.smsmasterapplication.entities.DeleteMemberEntity;
import com.ironfactory.smsmasterapplication.entities.GroupEntity;
import com.ironfactory.smsmasterapplication.networks.SocketListener;
import com.ironfactory.smsmasterapplication.networks.SocketManager;

import java.util.ArrayList;
import java.util.List;

// 최고관리자 세팅 화면
public class SuperMasterActivity extends AppCompatActivity {

    private static final String TAG = "SuperMasterActivity";

    private RecyclerView topMasterListRecycler;
    private RecyclerView deleteUserReqListRecycler;
    private RecyclerView deleteMemberReqListRecycler;
    private RecyclerView chargeCoinRecycler;

    private TopMasterAdapter topMasterAdapter;
    private DeleteUserReqAdapter deleteUserReqAdapter;
    private DeleteMemberReqAdapter deleteMemberReqAdapter;
    private ChargeCoinReqAdapter chargeCoinReqAdapter;

    private GroupEntity groupEntity;
    private List<String> deleteUserReqList;
    private List<ChargeCoinEntity> chargeCoinReqList;
    private List<DeleteMemberEntity> deleteMemberEntities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_master);
        setSupportActionBar((Toolbar) findViewById(R.id.activity_super_master_setting_toolbar));
        Log.d(TAG, "액티비티 시작");
//        init();
    }

    private void init() {
        groupEntity = getIntent().getParcelableExtra(Global.GROUP);

        setTitle("최고관리자 설정");

        topMasterListRecycler = (RecyclerView) findViewById(R.id.activity_master_setting_top_master);
        deleteUserReqListRecycler = (RecyclerView) findViewById(R.id.activity_master_setting_delete_user);
        deleteMemberReqListRecycler = (RecyclerView) findViewById(R.id.activity_master_setting_delete_member);
        chargeCoinRecycler = (RecyclerView) findViewById(R.id.activity_master_setting_charge_coin);

        setRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        init();
    }

    private void setRecyclerView() {
        deleteUserReqList = new ArrayList<>();
        deleteMemberEntities = new ArrayList<>();
        deleteMemberEntities = new ArrayList<>();
        chargeCoinReqList = new ArrayList<>();

        topMasterListRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        deleteUserReqListRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        deleteMemberReqListRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        chargeCoinRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        topMasterAdapter = new TopMasterAdapter(this, groupEntity);
        deleteUserReqAdapter = new DeleteUserReqAdapter(getApplicationContext(), deleteUserReqList);
        deleteMemberReqAdapter = new DeleteMemberReqAdapter(getApplicationContext(), deleteMemberEntities);
        chargeCoinReqAdapter = new ChargeCoinReqAdapter(getApplicationContext(), chargeCoinReqList);

        topMasterListRecycler.setAdapter(topMasterAdapter);
        deleteUserReqListRecycler.setAdapter(deleteUserReqAdapter);
        deleteMemberReqListRecycler.setAdapter(deleteMemberReqAdapter);
        chargeCoinRecycler.setAdapter(chargeCoinReqAdapter);

        SocketManager.getDeleteUserList(new SocketListener.OnGetDeleteUser() {
            @Override
            public void onSuccess(List<String> userList) {
                Log.d(TAG, "삭제 요청 계정 리스트 로드 성공");
                deleteUserReqList = userList;
                deleteUserReqAdapter.setUserList(userList);
            }

            @Override
            public void onException() {
                Log.d(TAG, "삭제 요청 계정 리스트 로드 실패");
                Toast.makeText(getApplicationContext(), "삭제 요청 계정 리스트 로드 실패", Toast.LENGTH_SHORT).show();
            }
        });

        SocketManager.getDeleteMemberList(new SocketListener.OnGetDeleteMember() {
            @Override
            public void onSuccess(List<DeleteMemberEntity> userList) {
                Log.d(TAG, "그룹 추방 요청 계정 리스트 로드 성공");
                deleteMemberEntities = userList;
                deleteMemberReqAdapter.setUserList(deleteMemberEntities);

                for (int i = 0; i < userList.size(); i++) {
                    final int I = i;

                    SocketManager.getGroupName(userList.get(i).getGroupId(), new SocketListener.OnGetGroupName() {
                        @Override
                        public void onSuccess(String name) {
                            Log.d(TAG, "그룹이름 로딩 성공");
                            deleteMemberReqAdapter.setGroupName(name, I);
                        }

                        @Override
                        public void onException() {
                            Log.d(TAG, "그룹이름 로딩 실패");
                        }
                    });
                }
            }

            @Override
            public void onException() {
                Log.d(TAG, "그룹 추방 요청 계정 리스트 로드 실패");
                Toast.makeText(getApplicationContext(), "삭제 요청 계정 리스트 로드 실패", Toast.LENGTH_SHORT).show();
            }
        });

        SocketManager.getChargeCoinList(new SocketListener.OnGetChargeCoin() {
            @Override
            public void onSuccess(List<ChargeCoinEntity> userList) {
                Log.d(TAG, "코인 추가 요청 리스트 로드 성공");
                chargeCoinReqList = userList;
                chargeCoinReqAdapter.setUserList(chargeCoinReqList);
            }

            @Override
            public void onException() {
                Log.d(TAG, "코인 추가 요청 리스트 로드 실패");
                Toast.makeText(getApplicationContext(), "코인 추가 요청 리스트 로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.master_setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // 그룹 생성 버튼
        if (id == R.id.master_setting_action_add_group) {
            Intent intent = new Intent(getApplicationContext(), SetGroupActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
