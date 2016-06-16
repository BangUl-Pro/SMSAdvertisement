package com.adplan.smsapplication.controllers.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.adplan.smsapplication.Global;
import com.adplan.smsapplication.R;
import com.adplan.smsapplication.controllers.fragments.CoinFragment;
import com.adplan.smsapplication.controllers.fragments.HelpFragment;
import com.adplan.smsapplication.controllers.fragments.SMSFragment;
import com.adplan.smsapplication.controllers.fragments.SettingFragment;
import com.adplan.smsapplication.entities.GroupEntity;
import com.adplan.smsapplication.entities.UserEntity;
import com.adplan.smsapplication.gcm.ServiceMonitor;
import com.adplan.smsapplication.networks.SocketListener;
import com.adplan.smsapplication.networks.SocketManager;

import java.util.List;

public class TabActivity extends AppCompatActivity {

    private static final String TAG = "TabActivity";

    private SectionsPagerAdapter adapter;
    private ViewPager mViewPager;

    private Button tab1;
    private Button tab2;
    private Button tab3;
    private Button tab4;

    private UserEntity userEntity;
    private GroupEntity groupEntity;

    private ServiceMonitor serviceMonitor = ServiceMonitor.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        if (!serviceMonitor.isMonitoring()) {
            serviceMonitor.startMonitoring(getApplicationContext());
        }
        init();
    }


    private void init() {
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(adapter);

        tab1 = (Button) findViewById(R.id.activity_tab1);
        tab2 = (Button) findViewById(R.id.activity_tab2);
        tab3 = (Button) findViewById(R.id.activity_tab3);
        tab4 = (Button) findViewById(R.id.activity_tab4);

        setListener();
        getIntent(getIntent());
    }


    private void getIntent(Intent intent) {
        userEntity = intent.getParcelableExtra(Global.USER);
        groupEntity = intent.getParcelableExtra(Global.GROUP);
//        userEntity = new UserEntity();
//        groupEntities = new ArrayList<>();
//        groupEntities.add(new GroupEntity(1, "여울컴"));
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences(Global.APP_NAME, MODE_PRIVATE);
        String id = preferences.getString(Global.ID, null);
        String pw = preferences.getString(Global.PW, null);
        String token = preferences.getString(Global.TOKEN, null);
        SocketManager.login(id, pw, token, new SocketListener.OnLogin() {
            @Override
            public void onSuccess(final UserEntity userEntity, List<GroupEntity> groupEntities) {
                TabActivity.this.userEntity = userEntity;
                SocketManager.getGroup(groupEntity.getId(), new SocketListener.OnGetGroup() {
                    @Override
                    public void onSuccess(GroupEntity groupEntity) {
                        TabActivity.this.groupEntity = groupEntity;
                        Log.d(TAG, "user = " + userEntity);
                        Log.d(TAG, "group = " + groupEntity);
                        adapter.setUserEntity(userEntity);
                        adapter.setGroupEntity(groupEntity);
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

    private void setListener() {
        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });

        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
            }
        });

        tab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(2);
            }
        });

        tab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(3);
            }
        });
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SMSFragment smsFragment;
        private CoinFragment coinFragment;
        private SettingFragment settingFragment;
        private HelpFragment helpFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setUserEntity(UserEntity userEntity) {
            if (smsFragment != null)
                smsFragment.setUserEntity(userEntity);
            if (coinFragment != null)
                coinFragment.setUserEntity(userEntity);
            if (settingFragment != null)
                settingFragment.setUserEntity(userEntity);
        }

        public void setGroupEntity(GroupEntity groupEntity) {
            if (smsFragment != null)
                smsFragment.setGroupEntity(groupEntity);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return smsFragment = SMSFragment.createInstance(userEntity, groupEntity);
            else if (position == 1)
                return coinFragment = CoinFragment.createInstance(userEntity);
            else if (position == 2)
                return settingFragment = SettingFragment.createInstance(userEntity);
            else
                return helpFragment = new HelpFragment();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "M# SMS";
                case 1:
                    return "코인";
                case 2:
                    return "환경설정";
                case 3:
                    return "도움말";
            }
            return null;
        }
    }
}
