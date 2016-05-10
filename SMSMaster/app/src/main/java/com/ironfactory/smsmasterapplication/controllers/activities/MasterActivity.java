package com.ironfactory.smsmasterapplication.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ironfactory.smsmasterapplication.Global;
import com.ironfactory.smsmasterapplication.R;
import com.ironfactory.smsmasterapplication.controllers.adapters.DetailAdapter;
import com.ironfactory.smsmasterapplication.controllers.adapters.MasterAdapter;
import com.ironfactory.smsmasterapplication.entities.GroupEntity;
import com.ironfactory.smsmasterapplication.entities.UserEntity;
import com.ironfactory.smsmasterapplication.networks.SocketListener;
import com.ironfactory.smsmasterapplication.networks.SocketManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MasterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MasterActivity";

    private TextView detailView;
    private RecyclerView masterRecyclerView;
    private RecyclerView detailRecyclerView;

    private LinearLayout settingContainer;
    private ToggleButton allowChangeMsgBtn;
    private ToggleButton allowDetailBtn;
    private ToggleButton allowSendBtn;
    private Button applyAllGroupBtn;
    private CheckBox infiniteCoinCheckBox;
    private EditText maxCoinInput;

    private MasterAdapter masterAdapter;
    private DetailAdapter detailAdapter;

    private GroupEntity groupEntity;

    // 메세지 변경 허용
    private int isAbleChangeMsg = UserEntity.DISABLE;

    // 광고 상세 보기 허용
    private int isAbleShowAdDetail = UserEntity.DISABLE;

    // 스마트폰 수신번호로 문자 발송 허용
    private int isAbleSend = UserEntity.DISABLE;

    private List<Integer> yesterdayList;
    private List<Integer> todayList;

    private SocketListener.OnGetUserCount onGetUserYesterdayCount = new SocketListener.OnGetUserCount() {
        @Override
        public void onSuccess(int count, int position) {
            Log.d(TAG, "어제 메세지 카운트 로드 성공");
            yesterdayList.set(position, count);
            Date date = new Date(System.currentTimeMillis());
            SocketManager.getUserCount(groupEntity.getMembers().get(position).getId(), date, position, onGetUserTodayCount);
        }

        @Override
        public void onException() {
            Log.d(TAG, "어제 메세지 카운트 로드 실패");
        }
    };

    private SocketListener.OnGetUserCount onGetUserTodayCount = new SocketListener.OnGetUserCount() {
        @Override
        public void onSuccess(int count, int position) {
            Log.d(TAG, "오늘 메세지 카운트 로드 성공");
            todayList.set(position, count);
            if (++position < groupEntity.getMembers().size()) {
                Date date = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
                SocketManager.getUserCount(groupEntity.getMembers().get(position).getId(), date, position, onGetUserYesterdayCount);
            } else {
                // 전부 다 받아왔으면
                detailAdapter.setMsgYesterdayList(yesterdayList);
                detailAdapter.setMsgTodayList(todayList);
            }
        }

        @Override
        public void onException() {
            Log.d(TAG, "오늘 메세지 카운트 로드 실패");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        Log.d(TAG, "액티비티 시작");
        groupEntity = getIntent().getParcelableExtra(Global.GROUP);
    }


    private void init() {
        setSupportActionBar((Toolbar) findViewById(R.id.activity_master_toolbar));
        setTitle(groupEntity.getName());

        yesterdayList = new ArrayList<>();
        todayList = new ArrayList<>();
        for (int i = 0; i < groupEntity.getMembers().size(); i++) {
            yesterdayList.add(0);
            todayList.add(0);
        }

        masterRecyclerView = (RecyclerView) findViewById(R.id.activity_master_recycler);
        detailRecyclerView = (RecyclerView) findViewById(R.id.activity_master_detail_recycler);
        detailView = (TextView) findViewById(R.id.activity_master_detail);
        infiniteCoinCheckBox = (CheckBox) findViewById(R.id.activity_master_infinite_coin);
        maxCoinInput = (EditText) findViewById(R.id.activity_master_max_coin);

        settingContainer = (LinearLayout) findViewById(R.id.activity_master_setting_container);
        allowChangeMsgBtn = (ToggleButton) findViewById(R.id.activity_master_access_change_msg);
        allowDetailBtn = (ToggleButton) findViewById(R.id.activity_master_access_detail);
        allowSendBtn = (ToggleButton) findViewById(R.id.activity_master_send);
        applyAllGroupBtn = (Button) findViewById(R.id.activity_master_apply_all_group);

        if (groupEntity.getMaxCoin() == -1) {
            infiniteCoinCheckBox.setChecked(true);
            maxCoinInput.setClickable(false);
            maxCoinInput.setFocusable(false);
        }
        else {
            infiniteCoinCheckBox.setChecked(false);
            maxCoinInput.setText(String.valueOf(groupEntity.getMaxCoin()));
        }

        setListener();
        setRecyclerView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 20) {
            if (resultCode == 200) {
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        SocketManager.getGroup(groupEntity.getId(), new SocketListener.OnGetGroup() {
            @Override
            public void onSuccess(GroupEntity groupEntity) {
                Log.d(TAG, "그룹 정보 받아오기 성공");
                MasterActivity.this.groupEntity = groupEntity;
                init();
            }

            @Override
            public void onException() {
                Log.d(TAG, "그룹 정보 받아오기 실패");
                Toast.makeText(getApplicationContext(), "그룹 정보 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });
        super.onResume();
    }

    private void setListener() {
        infiniteCoinCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                maxCoinInput.setClickable(!isChecked);
                maxCoinInput.setFocusable(!isChecked);
                maxCoinInput.setFocusableInTouchMode(!isChecked);

                SocketManager.setMaxCoin(groupEntity.getId(), -1, new SocketListener.OnSetMaxCoin() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "일일 코인 사용량 설정 성공");
                        if (!isChecked) {
                            maxCoinInput.setText("");
                        }
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "일일 코인 사용량 설정 실패");
                    }
                });
            }
        });

        maxCoinInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int num = 0;
                if (!TextUtils.isEmpty(maxCoinInput.getText().toString())) {
                    num = Integer.parseInt(maxCoinInput.getText().toString());
                }

                SocketManager.setMaxCoin(groupEntity.getId(), num, new SocketListener.OnSetMaxCoin() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "일일 코인 사용량 설정 성공");
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "일일 코인 사용량 설정 실패");
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        allowChangeMsgBtn.setOnClickListener(this);
        allowDetailBtn.setOnClickListener(this);
        allowSendBtn.setOnClickListener(this);
        applyAllGroupBtn.setOnClickListener(this);
        detailView.setOnClickListener(this);
    }


    private void setRecyclerView() {
        masterRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        detailRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        masterAdapter = new MasterAdapter(getApplicationContext(), groupEntity);
        masterRecyclerView.setAdapter(masterAdapter);

        Date date = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
        if (groupEntity.getMembers().size() > 0) {
            SocketManager.getUserCount(groupEntity.getMembers().get(0).getId(), date, 0, onGetUserYesterdayCount);
        }

        detailAdapter = new DetailAdapter(getApplicationContext(), groupEntity.getMembers(), todayList, yesterdayList);
        detailRecyclerView.setAdapter(detailAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_master, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_master_setting) {
            // 설정
            settingContainer.setVisibility((settingContainer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
        } else if (id == R.id.action_master_super_setting) {
            // 관리자 설정
            Intent intent = new Intent(getApplicationContext(), SetGroupActivity.class);
            intent.putExtra(Global.GROUP, groupEntity);
            startActivityForResult(intent, 20);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(allowChangeMsgBtn)) {
            isAbleChangeMsg = (allowChangeMsgBtn.isChecked() ? UserEntity.ABLE : UserEntity.DISABLE);
        } else if (v.equals(allowDetailBtn)) {
            isAbleShowAdDetail = (allowDetailBtn.isChecked() ? UserEntity.ABLE : UserEntity.DISABLE);
        } else if (v.equals(allowSendBtn)) {
            isAbleSend = (allowSendBtn.isChecked() ? UserEntity.ABLE : UserEntity.DISABLE);
        } else if (v.equals(applyAllGroupBtn)) {
            for (UserEntity user :
                    groupEntity.getMembers()) {
                SocketManager.setChangeMsg(user.getId(), isAbleChangeMsg, new SocketListener.OnSetChangeMsg() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "메세지 변경 세팅 성공");
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "메세지 변경 세팅 실패");
                    }
                });

                SocketManager.setSend(user.getId(), isAbleSend, new SocketListener.OnSetSend() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "스마트폰 수신번호로 메세지 전송 세팅 성공");
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "스마트폰 수신번호로 메세지 전송 세팅 실패");
                    }
                });

                SocketManager.setShowAdDetail(user.getId(), isAbleShowAdDetail, new SocketListener.OnSetShowAdDetail() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "광고상세보기 전송 세팅 성공");
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "광고상세보기 전송 세팅 실패");
                    }
                });
            }

            int num = 0;
            if (!TextUtils.isEmpty(maxCoinInput.getText().toString())) {
                num = Integer.parseInt(maxCoinInput.getText().toString());
            }

            if (!infiniteCoinCheckBox.isChecked()) {
                SocketManager.setMaxCoin(groupEntity.getId(), num, new SocketListener.OnSetMaxCoin() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "일일 코인 사용량 설정 성공");
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "일일 코인 사용량 설정 실패");
                    }
                });
            }
        } else if (v.equals(detailView)) {
            if (detailRecyclerView.getVisibility() == View.VISIBLE) {
                detailRecyclerView.setVisibility(View.GONE);
                masterRecyclerView.setVisibility(View.VISIBLE);
            } else {
                detailRecyclerView.setVisibility(View.VISIBLE);
                masterRecyclerView.setVisibility(View.GONE);
            }
        }
    }
}
