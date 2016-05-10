package com.ironfactory.smsapplication.controllers.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.ironfactory.smsapplication.Global;
import com.ironfactory.smsapplication.R;
import com.ironfactory.smsapplication.controllers.adapter.SelectGroupAdapter;
import com.ironfactory.smsapplication.entities.GroupEntity;
import com.ironfactory.smsapplication.entities.UserEntity;

import java.util.List;

public class SelectGroupActivity extends AppCompatActivity {

    private static final String TAG = "SelectGroupActivity";

    private RecyclerView recyclerView;
    private SelectGroupAdapter adapter;

    private List<GroupEntity> groupEntities;
    private UserEntity userEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);

        init();
    }

    private void init() {
        setSupportActionBar((Toolbar) findViewById(R.id.activity_select_group_toolbar));
        setTitle("그룹 선택");

        userEntity = getIntent().getParcelableExtra(Global.USER);
        groupEntities = getIntent().getParcelableArrayListExtra(Global.GROUP);

        recyclerView = (RecyclerView) findViewById(R.id.activity_select_group_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new SelectGroupAdapter(getApplicationContext(), groupEntities, userEntity);
        recyclerView.setAdapter(adapter);
    }
}
