package com.ironfactory.smsapplication;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class ScreenService extends Service {

    private ScreenReceiver screenReceiver;

    public ScreenService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        screenReceiver = new ScreenReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent.getAction() != null) {
            if (screenReceiver == null) {
                init();
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
