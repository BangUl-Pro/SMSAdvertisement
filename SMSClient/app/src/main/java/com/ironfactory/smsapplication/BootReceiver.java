package com.ironfactory.smsapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ironfactory.smsapplication.gcm.Gcm;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = intent.getAction();
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d(TAG, "폰 켜짐");
            Gcm gcm = new Gcm(context);
            gcm.onResume();
        }
    }
}
