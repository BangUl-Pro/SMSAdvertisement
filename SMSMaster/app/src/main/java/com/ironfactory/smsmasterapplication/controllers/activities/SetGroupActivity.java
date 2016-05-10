package com.ironfactory.smsmasterapplication.controllers.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.ironfactory.smsmasterapplication.Global;
import com.ironfactory.smsmasterapplication.R;
import com.ironfactory.smsmasterapplication.controllers.adapters.SetMasterAdapter;
import com.ironfactory.smsmasterapplication.controllers.adapters.SetMemberAdapter;
import com.ironfactory.smsmasterapplication.entities.GroupEntity;
import com.ironfactory.smsmasterapplication.entities.UserEntity;
import com.ironfactory.smsmasterapplication.networks.SocketListener;
import com.ironfactory.smsmasterapplication.networks.SocketManager;

import java.util.ArrayList;
import java.util.List;

public class SetGroupActivity extends AppCompatActivity implements SetMemberAdapter.OnMember, SetMasterAdapter.OnMaster {

    private static final String TAG = "SetGroupActivity";

    private EditText groupNameInput;
    private RecyclerView masterRecycler;
    private RecyclerView mateRecycler;
    private SetMemberAdapter mateAdapter;
    private SetMasterAdapter masterAdapter;

    private List<String> addMembers;
    private List<String> addMasters;
    private List<String> removeMembers;
    private List<String> removeMasters;

    private GroupEntity groupEntity;

    private SocketListener.OnInsertMember onInsertMember = new SocketListener.OnInsertMember() {
        @Override
        public void onSuccess(UserEntity userEntity) {
            Log.d(TAG, "멤버 추가 성공");
            insertMember();
        }

        @Override
        public void onException(int code) {
            if (code == 442) {
                Log.d(TAG, "없는 멤버 입니다");
                Toast.makeText(getApplicationContext(), addMembers.get(0) + "는 등록되지 않은 회원입니다", Toast.LENGTH_SHORT).show();
            } else if (code == 444) {
                Log.d(TAG, "이미 추가된 멤버");
                Toast.makeText(getApplicationContext(), addMembers.get(0) + "는 이미 등록되어있습니다", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "멤버 추가 실패");
                Toast.makeText(getApplicationContext(), "멤버 추가 실패", Toast.LENGTH_SHORT).show();
            }

            insertMember();
        }
    };

    private SocketListener.OnInsertMaster onInsertMaster = new SocketListener.OnInsertMaster() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "그룹장 추가 성공");
            insertMaster();
        }

        @Override
        public void onException(int code) {
            Log.d(TAG, "그룹장 추가 실패");
            if (code == 432) {
                Toast.makeText(getApplicationContext(), addMasters.get(0) + "는 등록되지 않은 회원입니다", Toast.LENGTH_SHORT).show();
            } else if (code == 435) {
                Toast.makeText(getApplicationContext(), addMasters.get(0) + "는 이미 등록된 멤버입니다", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "그룹장 추가 실패", Toast.LENGTH_SHORT).show();
            }
            insertMaster();
        }
    };

    private SocketListener.OnDeleteMemberReq onDeleteMemberReq = new SocketListener.OnDeleteMemberReq() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "멤버 추방 요청 성공");
            deleteMember();
        }

        @Override
        public void onException() {
            Log.d(TAG, "멤버 추방 요청 실패");
            Toast.makeText(getApplicationContext(), "멤버 추방 요청 실패", Toast.LENGTH_SHORT).show();

            deleteMember();
        }
    };

    private SocketListener.OnDeleteMaster onDeleteMaster = new SocketListener.OnDeleteMaster() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "그룹장 추방 성공");
            deleteMaster();
        }

        @Override
        public void onException(int code) {
            Log.d(TAG, "그룹장 추방 실패");
            Toast.makeText(getApplicationContext(), "그룹장 추방 요청 실패", Toast.LENGTH_SHORT).show();

            deleteMaster();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_group);
        setSupportActionBar((Toolbar) findViewById(R.id.activity_set_group_toolbar));
        setTitle("그룹 설정");
        Log.d(TAG, "액티비티 시작");
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_set_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // 확인
        if (id == R.id.item_set_group_action_submit) {
            final String groupName = groupNameInput.getText().toString();

            if (TextUtils.isEmpty(groupName)) {
                Toast.makeText(getApplicationContext(), "그룹명을 입력해주세요", Toast.LENGTH_SHORT).show();
                return false;
            }


            if (groupEntity.getId() == 0) {
                Log.d(TAG, "그룹추가");
                // 신규
                // 그룹 추가
                SocketManager.insertGroup(groupName, new SocketListener.OnInsertGroup() {
                    @Override
                    public void onSuccess(GroupEntity groupEntity) {
                        Log.d(TAG, "그룹 추가 성공");
                        SetGroupActivity.this.groupEntity = groupEntity;

                        checkInsertData();

                        // 멤버 추가
                        if (addMembers.size() > 0)
                            SocketManager.insertMember(groupEntity.getId(), addMembers.get(0), onInsertMember);

                        // 마스터 추가
                        if (addMasters.size() > 0)
                            SocketManager.insertMaster(groupEntity.getId(), addMasters.get(0), onInsertMaster);

                        // 멤버 삭제
                        if (removeMembers.size() > 0)
                            SocketManager.deleteMemberReq(groupEntity.getId(), removeMembers.get(0), onDeleteMemberReq);

                        // 그룹장 삭제
                        if (removeMasters.size() > 0)
                            SocketManager.deleteMaster(groupEntity.getId(), removeMasters.get(0), onDeleteMaster);
                    }

                    @Override
                    public void onException(int code) {
                        Log.d(TAG, "그룹 추가 실패");
                        if (code == 474) {
                            Toast.makeText(getApplicationContext(), "이미 해당 이름을 사용중입니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Log.d(TAG, "그룹수정");
                // 수정
                if (!groupName.equals(groupEntity.getName())) {
                    groupEntity.setName(groupName);
                    SocketManager.updateGroupName(groupEntity.getId(), groupName, new SocketListener.OnUpdateGroupName() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "이름 변경 성공");
                        }

                        @Override
                        public void onException() {
                            Log.d(TAG, "이름 변경 실패");
                            Toast.makeText(getApplicationContext(), "이름 변경에 실패했습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                checkInsertData();

                // 멤버 추가
                if (addMembers.size() > 0)
                    SocketManager.insertMember(groupEntity.getId(), addMembers.get(0), onInsertMember);

                // 마스터 추가
                if (addMasters.size() > 0)
                    SocketManager.insertMaster(groupEntity.getId(), addMasters.get(0), onInsertMaster);

                // 멤버 삭제
                if (removeMembers.size() > 0)
                    SocketManager.deleteMemberReq(groupEntity.getId(), removeMembers.get(0), onDeleteMemberReq);

                // 그룹장 삭제
                if (removeMasters.size() > 0)
                    SocketManager.deleteMaster(groupEntity.getId(), removeMasters.get(0), onDeleteMaster);
            }
        } else if (id == R.id.item_set_group_action_remove) {
            // 그룹삭제
            if (groupEntity.getMembers().size() == 0 && groupEntity.getMasters().size() == 0) {
                SocketManager.deleteGroup(groupEntity.getId(), new SocketListener.OnDeleteGroup() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "그룹 삭제 성공");
                        Toast.makeText(getApplicationContext(), "그룹 삭제 성공", Toast.LENGTH_SHORT).show();

                        setResult(200);
                        finish();
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "그룹 삭제 실패");
                        Toast.makeText(getApplicationContext(), "그룹 삭제 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "아직 그룹원이 남아있습니다", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        addMasters = new ArrayList<>();
        addMembers = new ArrayList<>();
        removeMembers = new ArrayList<>();
        removeMasters = new ArrayList<>();

        groupNameInput = (EditText) findViewById(R.id.activity_set_group_name);
        masterRecycler = (RecyclerView) findViewById(R.id.activity_set_group_master);
        mateRecycler = (RecyclerView) findViewById(R.id.activity_set_group_mate);

        if ((groupEntity = getIntent().getParcelableExtra(Global.GROUP)) == null)
            groupEntity = new GroupEntity();
        else {
            groupNameInput.setText(groupEntity.getName());
        }

        setRecyclerView();
    }

    private void setRecyclerView() {
        mateRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        masterRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        mateAdapter = new SetMemberAdapter(this, groupEntity);
        masterAdapter = new SetMasterAdapter(this, groupEntity);
        mateRecycler.setAdapter(mateAdapter);
        masterRecycler.setAdapter(masterAdapter);
    }


    @Override
    public void onAddMaster(String id) {
        if (removeMasters.indexOf(id) != -1)
            removeMasters.remove(id);
        else
            addMasters.add(id);
    }

    @Override
    public void onRemoveMaster(String id) {
        if (addMasters.indexOf(id) != -1)
            addMasters.remove(id);
        else
            removeMasters.add(id);
    }

    @Override
    public void onAddMember(String id) {
        Log.d(TAG, "addMember = " + id);

        if (removeMembers.indexOf(id) != -1)
            removeMembers.remove(id);
        else
            addMembers.add(id);
    }

    @Override
    public void onRemoveMember(String id) {
        if (addMembers.indexOf(id) != -1)
            addMembers.remove(id);
        else
            removeMembers.add(id);
    }

    public void checkInsertData() {
        if (addMasters.size() == 0 && addMembers.size() == 0 && removeMembers.size() == 0 && removeMasters.size() == 0) {
            finish();
        }
    }

    private void insertMember() {
        addMembers.remove(0);
        checkInsertData();
        if (addMembers.size() > 0) {
            SocketManager.insertMember(groupEntity.getId(), addMembers.get(0), onInsertMember);
        }
    }

    private void insertMaster() {
        addMasters.remove(0);
        checkInsertData();
        if (addMasters.size() > 0)
            SocketManager.insertMaster(groupEntity.getId(), addMasters.get(0), onInsertMaster);
    }

    private void deleteMember() {
        mateAdapter.removeMember(removeMembers.get(0));
        removeMembers.remove(0);
        checkInsertData();
        if (removeMembers.size() > 0)
            SocketManager.deleteMemberReq(groupEntity.getId(), removeMembers.get(0), onDeleteMemberReq);
    }

    private void deleteMaster() {
        masterAdapter.removeMaster(removeMasters.get(0));
        removeMasters.remove(0);
        checkInsertData();
        if (removeMasters.size() > 0)
            SocketManager.deleteMaster(groupEntity.getId(), removeMasters.get(0), onDeleteMaster);
    }
}
