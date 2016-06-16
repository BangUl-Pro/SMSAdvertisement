package com.adplan.smsapplication.gcm;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.telephony.SmsManager;
import android.util.Log;

import com.adplan.smsapplication.Global;
import com.adplan.smsapplication.entities.MsgEntity;
import com.adplan.smsapplication.DBManager;
import com.adplan.smsapplication.entities.GroupEntity;
import com.adplan.smsapplication.entities.UserEntity;
import com.adplan.smsapplication.networks.SocketListener;
import com.adplan.smsapplication.networks.SocketManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <service
 android:name=".gcm.GcmListenerService"
 android:enabled="true"
 android:exported="false" >
 <intent-filter>
 <action android:name="com.google.android.c2dm.intent.RECEIVE" />
 </intent-filter>
 </service>
 * */
public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    private static final String TAG = "GcmListenerService";

    public GcmListenerService() {
    }


    @Override
    public void onMessageReceived(String from, Bundle data) {
        String phone = data.getString("title");
        String port = data.getString("message");
        String id = data.getString("id");

        Log.i(TAG, "Phone = " + phone);
        Log.i(TAG, "Port = " + port);
        Log.i(TAG, "Id = " + id);

        sendMsg(phone, port, id);
    }






    /**
     * TODO: 실제 Device에 GCM 메세지를 알려주는 메소드다.
     * */
//    private void sendNotification(String title, String message) {
//        try {
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, StartActivity.class), 0);
//            Notification.Builder builder = new Notification.Builder(this);
//            builder.setSmallIcon(R.mipmap.ic_launcher);
//            builder.setTicker(message);
//            builder.setContentTitle(getResources().getString(R.string.app_name));
//            builder.setContentText(message);
//            builder.setDefaults(Notification.DEFAULT_VIBRATE);
//            builder.setContentIntent(pendingIntent);
//            builder.setAutoCancel(true);
//            NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//            manager.notify(123, builder.build());
//        } catch (Exception e) {
//            Log.e(TAG, "푸시알림에러 = " + e.getMessage());
//        }
//    }


    /**
     * TODO : 메세지 보내기
     * */
    private void sendMsg(final String num, final String port, final String id) {
        try {
            SocketManager.createInstance();
            SocketManager.getMsg(id, new SocketListener.OnGetMsg() {
                @Override
                public void onSuccess(final ArrayList<MsgEntity> msgEntities) {
                    Log.d(TAG, "메세지 받아오기 성공");

                    if (msgEntities.size() == 0)
                        return;

                    int i;
                    for (i = 0; i < msgEntities.size(); i++) {
                        Log.d(TAG, "msgList = " + msgEntities.get(i).getPort());
                        if (msgEntities.get(i).getPort().equals(port))
                            break;
                    }
                    final String content = msgEntities.get(i).getContent();

                    if (content == null)
                        return;

                    final SmsManager smsManager = SmsManager.getDefault();

                    SharedPreferences preferences = getSharedPreferences(Global.APP_NAME, MODE_PRIVATE);
                    String userId = preferences.getString(Global.ID, null);
                    String pw = preferences.getString(Global.PW, null);
                    String token = preferences.getString(Global.TOKEN, null);

                    final int I = i;

                    SocketManager.login(userId, pw, token, new SocketListener.OnLogin() {
                        @Override
                        public void onSuccess(UserEntity userEntity, List<GroupEntity> groupEntities) {
                            Log.d(TAG, "로그인 성공");
                            Log.d(TAG, "send = " + userEntity.isAbleSendPhone());

                            if (msgEntities.get(I).getIsEverybody() == MsgEntity.EVERYONE) {
                                if (userEntity.isAbleSendPhone()) {
                                    SocketManager.sendMsg(id, new SocketListener.OnSendMsg() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d(TAG, "메세지 전송 성공");
                                            // 수신번호 모두에게 보내야 함
                                            List<String> phoneList = getHistory();

                                            for (int j = 0; j < phoneList.size(); j++) {
                                                if (content.length() > 80) {
                                                    ArrayList<String> partMsg = smsManager.divideMessage(content);
                                                    smsManager.sendMultipartTextMessage(phoneList.get(j), null, partMsg, null, null);
                                                } else {
                                                    smsManager.sendTextMessage(phoneList.get(j), null, content, null, null);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onException() {
                                            Log.d(TAG, "메세지 전송 실패");
                                        }
                                    });
                                } else {
                                    Log.d(TAG, "수신번호 모두에게 보내는 기능 허용 받아야함");
                                }
                            } else {
                                SocketManager.sendMsg(id, new SocketListener.OnSendMsg() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d(TAG, "메세지 전송 성공");
                                        // 지정된 사람에게만 보내야함
                                        if (content.length() > 80) {
                                            ArrayList<String> partMsg = smsManager.divideMessage(content);
                                            smsManager.sendMultipartTextMessage(num, null, partMsg, null, null);
                                        } else {
                                            smsManager.sendTextMessage(num, null, content, null, null);
                                        }
                                    }

                                    @Override
                                    public void onException() {
                                        Log.d(TAG, "메세지 전송 실패");
                                    }
                                });
                            }
                        }

                        @Override
                        public void onException(int code) {
                            Log.d(TAG, "로그인 실패");
                        }
                    });
                }

                @Override
                public void onException() {
                    Log.d(TAG, "메세지 받아오기 실패.");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Map<String, Integer> filteringPhone(Map<String, Integer> phoneMap) {
        DBManager dbManager = new DBManager(getApplicationContext(), Global.APP_NAME, null, 1);
        List<String> deleteList = dbManager.getPhone();

        for (String deletePhone:
                deleteList) {
            Log.d(TAG, "deletePhone = " + deletePhone);
            Log.d(TAG, "phoneMap = " + phoneMap.get(deletePhone));
            if (phoneMap.get(deletePhone) != null) {
                phoneMap.remove(deletePhone);
                Log.d(TAG, "지웠다");
                continue;
            }
        }
        return phoneMap;
    }


    private List<String> getHistory() {
        String[] projection = { CallLog.Calls.TYPE, CallLog.Calls.NUMBER, CallLog.Calls.DURATION, CallLog.Calls.DATE };

        Map<String, Integer> phoneMap = new HashMap<>();
        List<String> phoneList = new ArrayList<>();

        try {
            Cursor cur = getApplicationContext().getContentResolver().query(
                    CallLog.Calls.CONTENT_URI, projection,
                    CallLog.Calls.TYPE + "=?",
                    new String[] {CallLog.Calls.INCOMING_TYPE + ""},
                    CallLog.Calls.DATE + " DESC"
            );

            if(cur.moveToFirst() && cur.getCount() > 0) {
                while(cur.isAfterLast() == false) {
                    String originalPhone = cur.getString(cur.getColumnIndex(CallLog.Calls.NUMBER));
                    Log.d(TAG, "originalPhone = " + originalPhone);
                    if (phoneMap.get(originalPhone) == null) {
                        phoneMap.put(originalPhone, 1);
                    }
                    cur.moveToNext();
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        phoneMap = filteringPhone(phoneMap);

        Iterator<String> iterator = phoneMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Log.d(TAG, "key = " + key);
            StringBuilder phoneBuild = new StringBuilder(key);
            phoneBuild.insert(3, "-");
            phoneBuild.insert(8, "-");
            String phone = phoneBuild.toString();
            phoneList.add(phone);
        }

        return phoneList;
    }
}
