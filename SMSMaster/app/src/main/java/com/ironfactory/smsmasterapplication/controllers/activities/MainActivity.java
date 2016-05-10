package com.ironfactory.smsmasterapplication.controllers.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ironfactory.smsmasterapplication.Global;
import com.ironfactory.smsmasterapplication.R;
import com.ironfactory.smsmasterapplication.controllers.adapters.DetailAdapter;
import com.ironfactory.smsmasterapplication.controllers.adapters.MemberAdapter;
import com.ironfactory.smsmasterapplication.entities.GroupEntity;
import com.ironfactory.smsmasterapplication.entities.UserEntity;
import com.ironfactory.smsmasterapplication.networks.SocketListener;
import com.ironfactory.smsmasterapplication.networks.SocketManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView groupNameView;
    private TextView yesterdayView;
    private TextView todayView;
    private TextView dateView;
    private TextView detailView;
    private TextView countView;
    private TextView settingView;
    private RecyclerView recyclerView;
    private RecyclerView detailRecyclerView;

    private LinearLayout settingContainer;
    private ToggleButton allowChangeMsgBtn;
    private ToggleButton allowDetailBtn;
    private ToggleButton allowSendBtn;
    private Button applyAllGroupBtn;

    private MemberAdapter adapter;
    private DetailAdapter detailAdapter;

    private GroupEntity groupEntity;

    private int isAbleChangeMsg;
    private int isAbleShowAdDetail;
    private int isAbleSend;

    private int i = 0;
    private List<Integer> yesterdayList;
    private List<Integer> todayList;

    private SocketListener.OnGetGroupCount onGetGroupCount = new SocketListener.OnGetGroupCount() {
        @Override
        public void onSuccess(int count) {
            countView.setText(count + "회");
        }

        @Override
        public void onException() {
            Log.d(TAG, "getGroupCount Exception");
        }
    };

    private SocketListener.OnGetUserCount onGetUserYesterdayCount = new SocketListener.OnGetUserCount() {
        @Override
        public void onSuccess(int count, int position) {
            Log.d(TAG, "어제 메세지 카운트 로드 성공");
            yesterdayList.set(position, count);
            Date date = new Date(System.currentTimeMillis());
            SocketManager.getUserCount(groupEntity.getMembers().get(i).getId(), date, i, onGetUserTodayCount);
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
            i++;
            if (i < groupEntity.getMembers().size()) {
                Date date = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
                SocketManager.getUserCount(groupEntity.getMembers().get(i).getId(), date, i, onGetUserYesterdayCount);
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
        setContentView(R.layout.activity_main);
        Log.d(TAG, "액티비티 시작");
        groupEntity = getIntent().getParcelableExtra(Global.GROUP);
//        init();
    }


    private void init() {
        yesterdayList = new ArrayList<>();
        todayList = new ArrayList<>();
        for (int i = 0; i < groupEntity.getMembers().size(); i++) {
            yesterdayList.add(0);
            todayList.add(0);
        }

        groupNameView = (TextView) findViewById(R.id.activity_main_group_name);
        yesterdayView = (TextView) findViewById(R.id.activity_main_yesterday);
        todayView = (TextView) findViewById(R.id.activity_main_today);
        dateView = (TextView) findViewById(R.id.activity_main_date);
        detailView = (TextView) findViewById(R.id.activity_main_detail);
        countView = (TextView) findViewById(R.id.activity_main_count);
        settingView = (TextView) findViewById(R.id.activity_main_setting);
        recyclerView = (RecyclerView) findViewById(R.id.activity_main_recycler);
        detailRecyclerView = (RecyclerView) findViewById(R.id.activity_main_detail_recycler);

        settingContainer = (LinearLayout) findViewById(R.id.activity_main_setting_container);
        allowChangeMsgBtn = (ToggleButton) findViewById(R.id.activity_main_access_change_msg);
        allowDetailBtn = (ToggleButton) findViewById(R.id.activity_main_access_detail);
        allowSendBtn = (ToggleButton) findViewById(R.id.activity_main_send);
        applyAllGroupBtn = (Button) findViewById(R.id.activity_main_apply_all_group);

        groupNameView.setText(groupEntity.getName());

        Date date = new Date(System.currentTimeMillis());
        SocketManager.getGroupCount(groupEntity.getId(), date, onGetGroupCount);

        date = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
        SocketManager.getUserCount(groupEntity.getMembers().get(0).getId(), date, 0, onGetUserYesterdayCount);

        setRecyclerView();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SocketManager.getGroup(groupEntity.getId(), new SocketListener.OnGetGroup() {
            @Override
            public void onSuccess(GroupEntity groupEntity) {
                Log.d(TAG, "그룹 정보 받아오기 성공");
                MainActivity.this.groupEntity = groupEntity;

                init();
            }

            @Override
            public void onException() {
                Log.d(TAG, "그룹 정보 받아오기 실패");
                Toast.makeText(getApplicationContext(), "그룹 정보 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setListener() {
        yesterdayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                SocketManager.getGroupCount(groupEntity.getId(), calendar.getTime(), onGetGroupCount);
            }
        });

        todayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date(System.currentTimeMillis());
                SocketManager.getGroupCount(groupEntity.getId(), date, onGetGroupCount);
            }
        });

        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        detailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detailRecyclerView.getVisibility() == View.VISIBLE) {
                    detailRecyclerView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    detailRecyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });


        settingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingContainer.setVisibility((settingContainer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
            }
        });

        allowChangeMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAbleChangeMsg = (allowChangeMsgBtn.isChecked() ? UserEntity.ABLE : UserEntity.DISABLE);
            }
        });

        allowDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAbleShowAdDetail = (allowDetailBtn.isChecked() ? UserEntity.ABLE : UserEntity.DISABLE);
            }
        });

        allowSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAbleSend = (allowSendBtn.isChecked() ? UserEntity.ABLE : UserEntity.DISABLE);
            }
        });

        applyAllGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (UserEntity user :
                        groupEntity.getMembers()) {
                    SocketManager.setChangeMsg(user.getId(), isAbleChangeMsg, new SocketListener.OnSetChangeMsg() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "메세지 변경 허용 세팅 성공");
                        }

                        @Override
                        public void onException() {
                            Log.d(TAG, "메세지 변경 허용 세팅 실패");
                        }
                    });

                    SocketManager.setSend(user.getId(), isAbleSend, new SocketListener.OnSetSend() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "스마트폰 수신 번호로 문자 보내기 허용 세팅 성공");
                        }

                        @Override
                        public void onException() {
                            Log.d(TAG, "스마트폰 수신 번호로 문자 보내기 허용 세팅 실패");
                        }
                    });

                    SocketManager.setShowAdDetail(user.getId(), isAbleShowAdDetail, new SocketListener.OnSetShowAdDetail() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "광고 상세 조회 허용 세팅 성공");
                        }

                        @Override
                        public void onException() {
                            Log.d(TAG, "광고 상세 조회 허용 세팅 실패");
                        }
                    });
                }
            }
        });
    }


    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        detailRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new MemberAdapter(getApplicationContext(), groupEntity.getMembers());
        recyclerView.setAdapter(adapter);

        detailAdapter = new DetailAdapter(getApplicationContext(), groupEntity.getMembers(), yesterdayList, todayList);
        detailRecyclerView.setAdapter(detailAdapter);
    }
}
