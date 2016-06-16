package com.adplan.smsapplication.gcm;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;

/**
 * Created by IronFactory on 15. 11. 24..
 */
public class ServiceMonitor {

    private static final String TAG = "ServiceMonitor";
    private static ServiceMonitor instance;
    private AlarmManager alarmManager;
    private Intent intent;
    private PendingIntent pendingIntent;
    private int interval = 5000;

    public ServiceMonitor() {
    }

    public static synchronized ServiceMonitor getInstance() {
        if (instance == null)
            instance = new ServiceMonitor();
        return instance;
    }

    public static class MonitorBR extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isRunningService(context, MonitoringService.class)) {
                context.startService(new Intent(context, MonitoringService.class));
                Log.d(TAG, "서비스 재실행");
            }
        }
    }


    public void startMonitoring(Context context) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context, MonitorBR.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), interval, pendingIntent);
    }


    public void stopMonitoring(Context context) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context, MonitorBR.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.cancel(pendingIntent);
        alarmManager = null;
        pendingIntent = null;
    }


    public boolean isMonitoring() {
        return (MonitoringService.thread == null || !MonitoringService.thread.isAlive()) ? false : true;
    }


    public static boolean isRunningService(Context context, Class<?> cls) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> info = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (info != null) {
            for (ActivityManager.RunningServiceInfo serviceInfo : info) {
                ComponentName name = serviceInfo.service;
                String className = name.getClassName();

                if (className.equals(cls.getName())) {
                    isRunning = true;
                    break;
                }
            }
        }
        return isRunning;
    }
}
