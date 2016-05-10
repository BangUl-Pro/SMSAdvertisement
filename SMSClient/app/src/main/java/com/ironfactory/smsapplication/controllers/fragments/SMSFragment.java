package com.ironfactory.smsapplication.controllers.fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.ironfactory.smsapplication.Global;
import com.ironfactory.smsapplication.R;
import com.ironfactory.smsapplication.controllers.adapter.DetailAdapter;
import com.ironfactory.smsapplication.entities.GroupEntity;
import com.ironfactory.smsapplication.entities.UserEntity;
import com.ironfactory.smsapplication.networks.SocketListener;
import com.ironfactory.smsapplication.networks.SocketManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

// 첫번째 페이지
public class SMSFragment extends Fragment {

    private static final String TAG = "SMSFragment";

    private UserEntity userEntity;
    private GroupEntity groupEntity;

    private TextView yesterdayView;
    private TextView todayView;
    private TextView dateView;
    private TextView countView;
    private TextView detailView;
    private RecyclerView detailRecycler;
    private DetailAdapter adapter;

    private int i = 0;

    private List<Integer> yesterdayList;
    private List<Integer> todayList;

    private SocketListener.OnGetUserCount onGetUserYesterdayCount = new SocketListener.OnGetUserCount() {
        @Override
        public void onSuccess(int count, int position) {
            Log.d(TAG, "어제 메세지 카운트 로드 성공");
            yesterdayList.set(position, count);
            Date date = new Date(System.currentTimeMillis());
            if (position == i) {
                SocketManager.getUserCount(groupEntity.getMembers().get(i).getId(), date, i, onGetUserTodayCount);
            }
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
                adapter.setMsgYesterdayList(yesterdayList);
                adapter.setMsgTodayList(todayList);
            }
        }

        @Override
        public void onException() {
            Log.d(TAG, "오늘 메세지 카운트 로드 실패");
        }
    };

    public SMSFragment() {
    }

    public static SMSFragment createInstance(UserEntity userEntity, GroupEntity groupEntity) {
        SMSFragment fragment = new SMSFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Global.USER, userEntity);
        bundle.putParcelable(Global.GROUP, groupEntity);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userEntity = getArguments().getParcelable(Global.USER);
        groupEntity = getArguments().getParcelable(Global.GROUP);
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public void setGroupEntity(GroupEntity groupEntity) {
        this.groupEntity = groupEntity;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "groupEntity = " + groupEntity);
        Log.d(TAG, "groupEntity Id = " + groupEntity.getId());

        SocketManager.getGroup(groupEntity.getId(), new SocketListener.OnGetGroup() {
            @Override
            public void onSuccess(GroupEntity groupEntity) {
                Log.d(TAG, "그룹 로딩 성공");
                SMSFragment.this.groupEntity = groupEntity;

                adapter.setMembers(groupEntity.getMembers());

                for (int i = 0; i < groupEntity.getMembers().size(); i++) {
                    yesterdayList.add(0);
                    todayList.add(0);
                }

                // 광고 상세 조회
                Date date = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
                if (groupEntity.getMembers().size() > 0) {
                    i = 0;
                    SocketManager.getUserCount(groupEntity.getMembers().get(i).getId(), date, i, onGetUserYesterdayCount);
                }

                // 오늘 문자 발송량 조회
                date = new Date(System.currentTimeMillis());
                SocketManager.getGroupCount(groupEntity.getId(), date, new SocketListener.OnGetGroupCount() {
                    @Override
                    public void onSuccess(int count) {
                        Log.d(TAG, "오늘 문자 발송량 조회 성공");
                        countView.setText(count + "회");
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "오늘 문자 발송량 조회 실패 ");
                    }
                });
            }

            @Override
            public void onException() {
                Log.d(TAG, "그룹 로딩 실패");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sms, container, false);
        init(view);
        return view;
    }


    private void init(View rootView) {
        yesterdayList = new ArrayList<>();
        todayList = new ArrayList<>();

        yesterdayView = (TextView) rootView.findViewById(R.id.fragment_sms_yesterday);
        todayView = (TextView) rootView.findViewById(R.id.fragment_sms_today);
        dateView = (TextView) rootView.findViewById(R.id.fragment_sms_date);
        countView = (TextView) rootView.findViewById(R.id.fragment_sms_count);
        detailView = (TextView) rootView.findViewById(R.id.fragment_sms_detail);
        detailRecycler = (RecyclerView) rootView.findViewById(R.id.fragment_sms_detail_recycler);

        detailRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new DetailAdapter(getActivity(), groupEntity.getMembers(), yesterdayList, todayList);
        detailRecycler.setAdapter(adapter);

        setListener();
    }


    private void setListener() {
        yesterdayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColor(yesterdayView);

                // 어제 날짜 세팅
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.DAY_OF_MONTH, -1);

                // 어제 문자 발송량 조회
                SocketManager.getGroupCount(groupEntity.getId(), calendar.getTime(), new SocketListener.OnGetGroupCount() {
                    @Override
                    public void onSuccess(int count) {
                        Log.d(TAG, "어제 문자 발송량 조회 성공");
                        countView.setText(count + "회");
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "어제 문자 발송량 조회 실패 ");
                    }
                });
            }
        });

        todayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColor(todayView);

                // 오늘 날짜 세팅
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                // 오늘 문자 발송량 조회
                SocketManager.getGroupCount(groupEntity.getId(), calendar.getTime(), new SocketListener.OnGetGroupCount() {
                    @Override
                    public void onSuccess(int count) {
                        Log.d(TAG, "오늘 문자 발송량 조회 성공");
                        countView.setText(count + "회");
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "오늘 문자 발송량 조회 실패 ");
                    }
                });
            }
        });

        detailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userEntity.isAbleShowAdDetail())
                    detailRecycler.setVisibility((detailRecycler.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
                else
                    Toast.makeText(getActivity(), "관리자에게 광고상세조회 권한을 요청하세요", Toast.LENGTH_SHORT).show();
            }
        });

        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColor(dateView);
                Date date = new Date(System.currentTimeMillis());
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Log.d(TAG, "month = " + monthOfYear);
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        SocketManager.getGroupCount(groupEntity.getId(), calendar.getTime(), new SocketListener.OnGetGroupCount() {
                            @Override
                            public void onSuccess(int count) {
                                Log.d(TAG, "특정 날짜 문자 발송량 조회 성공");
                                countView.setText(count + "회");
                            }

                            @Override
                            public void onException() {
                                Log.d(TAG, "오늘 문자 발송량 조회 실패 ");
                            }
                        });
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });
    }

    private void setColor(View v) {
        if (v.equals(todayView)) {
            todayView.setTextColor(Color.RED);
            yesterdayView.setTextColor(Color.BLACK);
            dateView.setTextColor(Color.BLACK);
        } else if (v.equals(yesterdayView)) {
            todayView.setTextColor(Color.BLACK);
            yesterdayView.setTextColor(Color.RED);
            dateView.setTextColor(Color.BLACK);
        } else if (v.equals(dateView)) {
            todayView.setTextColor(Color.BLACK);
            yesterdayView.setTextColor(Color.BLACK);
            dateView.setTextColor(Color.RED);
        }
    }
}
