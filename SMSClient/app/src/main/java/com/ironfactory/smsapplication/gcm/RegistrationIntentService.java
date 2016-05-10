package com.ironfactory.smsapplication.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.ironfactory.smsapplication.R;


/**
 * <service
 android:name=".gcm.RegistrationIntentService"
 android:enabled="true"
 android:exported="false" >
 </service>


 <receiver
 android:name="com.google.android.gms.gcm.GcmReceiver"
 android:exported="true"
 android:permission="com.google.android.c2dm.permission.SEND" >
 <intent-filter>
 <action android:name="com.google.android.c2dm.intent.RECEIVE" />

 <category android:name="net.saltfactory.demo.gcm" />
 </intent-filter>
 </receiver>

 * */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegistIntentService";


    public RegistrationIntentService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        // Gcm Instance ID의 토큰을 가져오는 작업이 시작되면 LocalBroadcast로 Generating 액션 실행한다.
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(Gcm.REGISTRATION_GENERATING));


        // Gcm을 위한 Instance ID를 가져온다.
        InstanceID instanceID = InstanceID.getInstance(this);
        String token = null;
        try {
            synchronized (TAG) {
                // Gcm 앱을 등록하고 획득한 설정파일인 google-services.json을 기반으로 자동으로 SenderID를 가져온다.
                // R.string.gcm_defaultSenderId 에러 발생시 build.gradle에 apply plugin: 'com.google.gms.google-services' 추가
                String defaultSenderId = getString(R.string.gcm_defaultSenderId);
                String scope = GoogleCloudMessaging.INSTANCE_ID_SCOPE;
                token = instanceID.getToken(defaultSenderId, scope, null);
                Log.i(TAG, "token = " + token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // GCM Instance ID에 해당하는 토큰을 획득하면 LocalBoardcast에 COMPLETE 액션을 알린다.
        Intent registrationComplete = new Intent(Gcm.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
