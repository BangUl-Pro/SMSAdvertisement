package com.ironfactory.smsapplication.gcm;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;

public class MonitoringService extends Service {

    private static final String TAG = "MonitoringService";

    public static Thread thread;

    private ComponentName componentName;
    private ActivityManager activityManager;

    private boolean isRunning = false;

    public MonitoringService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();

        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        isRunning = true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (thread == null) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isRunning) {
                        List<ActivityManager.RecentTaskInfo> info = activityManager.getRecentTasks(1, Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (info != null) {
                            ActivityManager.RecentTaskInfo recent = info.get(0);
                            Intent intent1 = recent.baseIntent;
                            ComponentName name = intent1.getComponent();

                            if (name.equals(componentName)) {
//                                Log.d(TAG, "=== recentApp is sameApp");
                            } else {
                                componentName = name;
                                Log.d(TAG, "=== 잡앗다!" + name);
                            }
                        }
                        SystemClock.sleep(2000);
                    }
                }
            });
            thread.start();
        } else if (!thread.isAlive()) {
            thread.start();
        }

        Log.d(TAG, "Gcm 재실행");
        Gcm gcm = new Gcm(getApplicationContext());
        gcm.onResume();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
