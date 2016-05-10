package com.ironfactory.smsapplication.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.ironfactory.smsapplication.Global;
import com.ironfactory.smsapplication.R;
import com.ironfactory.smsapplication.ScreenService;
import com.ironfactory.smsapplication.controllers.activities.LoginActivity;
import com.ironfactory.smsapplication.networks.SocketManager;

/**
 * Created by ironFactory on 2015-09-25.
 */
public class Gcm {

    private static final String TAG = "GCM";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String REGISTRATION_READY = "registrationReady";
    public static final String REGISTRATION_GENERATING = "registrationGenerating";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    private BroadcastReceiver registrationBroadcastReceiver;

    private OnGcmHandler sender;
    private Context context;

    public Gcm(Context context) {
        this.context = context;
        try {
            sender = (OnGcmHandler) context;
        } catch (Exception e) {
            e.printStackTrace();
        }

        registBroadcast();
        if (checkPlayService()) {
            getInstanceToken();
        }
    }

    /**
     * TODO : Instance Id를 이용해 디바이스 토큰을 가져오는 RegistrationIntentService 실행
     * */
    public void getInstanceToken() {
        Intent intent = new Intent(context, RegistrationIntentService.class);
        context.startService(intent);
    }


    /**
     * TODO: LocalBroadcastReceiver를 정의 한다. 토큰 획득을 위해 READY, GENERATING, COMPLETE 액션 취한다.
     * */
    private void registBroadcast() {
        registrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(REGISTRATION_READY)) {
                    // 액션이 READY 일 경우
                    Log.d(TAG, "READY");
                } else if (action.equals(REGISTRATION_GENERATING)) {
                    // 액션이 GENERATING 일 경우
                    Log.d(TAG, "GENERATING");
                } else if (action.equals(REGISTRATION_COMPLETE)) {
                    // 액션이 COMPLETE 일 경우
                    Log.d(TAG, "COMPLETE");
                    String token = intent.getStringExtra("token");
                    Log.i(TAG, "token = " + token);
                    if (sender != null)
                        sender.onGetToken(token);
                }
            }
        };
    }


    /**
     * TODO: ACTION 정의
     * */
    public void onResume() {
        LocalBroadcastManager.getInstance(context).registerReceiver(
                registrationBroadcastReceiver,
                new IntentFilter(REGISTRATION_READY));

        LocalBroadcastManager.getInstance(context).registerReceiver(
                registrationBroadcastReceiver,
                new IntentFilter(REGISTRATION_GENERATING));

        LocalBroadcastManager.getInstance(context).registerReceiver(
                registrationBroadcastReceiver,
                new IntentFilter(REGISTRATION_COMPLETE));

        SharedPreferences preferences = context.getSharedPreferences(Global.APP_NAME, Context.MODE_PRIVATE);
        String id = preferences.getString(Global.ID, null);

        if (id != null) {
            SocketManager.connect(id);

            setNoti(true);

            ServiceMonitor serviceMonitor = ServiceMonitor.getInstance();

            Intent intent = new Intent(context, ScreenService.class);
            context.startService(intent);

            if (serviceMonitor.isMonitoring()) {
                serviceMonitor.startMonitoring(context);
            }
        }
    }


    public void onPause() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(registrationBroadcastReceiver);

        SharedPreferences preferences = context.getSharedPreferences(Global.APP_NAME, Context.MODE_PRIVATE);
        String id = preferences.getString(Global.ID, null);

        if (id != null) {
            SocketManager.disconnect(id);

            setNoti(false);
        }
    }

    private void setNoti(boolean isShow) {
        if (isShow) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("M# SMS");
            builder.setContentText("문자 전송 가능");
            builder.setSmallIcon(R.drawable.icon);
            builder.setAutoCancel(true);

            Intent intent2 = new Intent(context, LoginActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0);
            builder.setContentIntent(pendingIntent);

            Notification notification = builder.build();
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, notification);
        } else {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancelAll();
        }
    }


    /**
     * TODO: 구글 플레이 서비스를 사용 가능한지 확인
     * */
    public boolean checkPlayService() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }


    public interface OnGcmHandler {
        void onGetToken(String token);
    }
}
