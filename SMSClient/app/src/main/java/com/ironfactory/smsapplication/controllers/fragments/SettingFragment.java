package com.ironfactory.smsapplication.controllers.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ironfactory.smsapplication.Global;
import com.ironfactory.smsapplication.R;
import com.ironfactory.smsapplication.controllers.activities.ExceptPhoneListActivity;
import com.ironfactory.smsapplication.controllers.activities.InputActivity;
import com.ironfactory.smsapplication.controllers.activities.LoginActivity;
import com.ironfactory.smsapplication.controllers.adapter.MsgAdapter;
import com.ironfactory.smsapplication.entities.MsgEntity;
import com.ironfactory.smsapplication.entities.UserEntity;
import com.ironfactory.smsapplication.networks.SocketListener;
import com.ironfactory.smsapplication.networks.SocketManager;

import java.util.ArrayList;
import java.util.Iterator;

public class SettingFragment extends Fragment {

    private static final String TAG = "SettingFragment";

    private TextView addView;
    private RecyclerView recyclerView;
    private TextView nameView;
    private TextView phoneView;
    private TextView accountView;
    private TextView exceptPhoneView;
    private TextView logoutView;

    private UserEntity userEntity;
    private MsgAdapter adapter;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment createInstance(UserEntity userEntity) {
        SettingFragment fragment = new SettingFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Global.USER, userEntity);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userEntity = getArguments().getParcelable(Global.USER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        init(view);

        return view;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    private void init(View rootView) {
        addView = (TextView) rootView.findViewById(R.id.fragment_setting_add);
        nameView = (TextView) rootView.findViewById(R.id.fragment_setting_name);
        phoneView = (TextView) rootView.findViewById(R.id.fragment_setting_phone);
        accountView = (TextView) rootView.findViewById(R.id.fragment_setting_account);
        exceptPhoneView = (TextView) rootView.findViewById(R.id.fragment_setting_except_phone_list);
        logoutView = (TextView) rootView.findViewById(R.id.fragment_setting_logout);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_setting_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        nameView.setText(userEntity.getName());
        phoneView.setText(userEntity.getPhone());
        accountView.setText(userEntity.getId());

        insertPastMsg();
        setListener();
    }

    @Override
    public void onResume() {
        super.onResume();

        getMsg();
    }

    private void getMsg() {
        // 메세지 리스트 받아오기
        SocketManager.getMsg(userEntity.getId(), new SocketListener.OnGetMsg() {
            @Override
            public void onSuccess(ArrayList<MsgEntity> msgEntities) {
                Log.d(TAG, "getMsg 성공");
                adapter = new MsgAdapter(getActivity(), msgEntities, userEntity);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onException() {
                Log.d(TAG, "getMsg 실패");
                adapter = new MsgAdapter(getActivity(), new ArrayList<MsgEntity>(), userEntity);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private void setListener() {
        logoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getActivity().getSharedPreferences(Global.APP_NAME, getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.clear();
                editor.commit();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        exceptPhoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExceptPhoneListActivity.class);
                startActivity(intent);
            }
        });

        addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userEntity.isAbleChangeMsg()) {
                    Intent intent = new Intent(getActivity(), InputActivity.class);
                    intent.putExtra(Global.USER, userEntity.getId());
                    getActivity().startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "관리자에게 문구 수정 허용을 요청하십시오", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void insertPastMsg() {
        // 기존에 쓰던 SharedPreference msg를 서버로 전송해야함
        SharedPreferences preferences = getActivity().getSharedPreferences(Global.APP_NAME, Context.MODE_PRIVATE);
        Iterator<String> iterator = preferences.getAll().keySet().iterator();
        while (iterator.hasNext()) {
            try {
                String key = iterator.next();
                String msg = preferences.getString(key, null);

                if (key.equals("id") || key.equals("token") || key.equals("pw"))
                    continue;

                Log.d(TAG, "key = " + key);
                Log.d(TAG, "msg = " + msg);


                MsgEntity msgEntity = new MsgEntity();
                msgEntity.setPort(key);
                msgEntity.setContent(msg);
                msgEntity.setUserId(userEntity.getId());

                SocketManager.insertMsg(msgEntity, new SocketListener.OnInsertMsg() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "기존 SharePreference 서버 입력 성공");
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "기존 SharePreference 서버 입력 실패");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
