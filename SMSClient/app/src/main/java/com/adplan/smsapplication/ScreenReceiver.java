package com.adplan.smsapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.adplan.smsapplication.gcm.Gcm;

public class ScreenReceiver extends BroadcastReceiver {
    public ScreenReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Gcm gcm = new Gcm(context);
            gcm.onResume();
        }
    }
}
