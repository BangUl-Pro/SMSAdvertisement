package com.ironfactory.smsapplication.gcm;

import android.content.Intent;
import android.os.IBinder;


/**
 * <service
 android:name=".gcm.InstanceIDListenerService"
 android:enabled="true"
 android:exported="false" >
 <intent-filter>
 <action android:name="com.google.android.gms.iid.InstanceID" />
 </intent-filter>
 </service>
 * */
public class InstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {

    private static final String TAG = "InstanceIdLSNService";

    public InstanceIDListenerService() {
    }


    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
