package com.ironfactory.smsapplication.networks;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.ironfactory.smsapplication.Global;
import com.ironfactory.smsapplication.Secure;
import com.ironfactory.smsapplication.entities.GroupEntity;
import com.ironfactory.smsapplication.entities.MsgEntity;
import com.ironfactory.smsapplication.entities.UserEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ironFactory on 2015-08-03.
 */
public class SocketManager {

    private static final String URL = "http://sms-application.herokuapp.com";
//    private static final String URL = "http://sample-sms-application.herokuapp.com";
    private static final String TAG = "SocketManager";


    private static Handler handler;
    public static Socket socket;
    private Context context;

    public static void createInstance() {
        try {
            socket = IO.socket(URL);
        } catch (Exception e) {
            Log.e(TAG, "init 에러 = " + e.getMessage());
        }

        socketConnect();

//        socket.emit("newMsg", "");
    }

    private static void setListener() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // 연결
                Log.d(TAG, "소켓 연결");

            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // 연결 끊김
                Log.d(TAG, "소켓 연결 끊김");
                socketConnect();
            }
        });
    }

    public static Socket getSocket() {
        return socket;
    }


    public static void socketConnect() {
        if (socket != null) {
            socket.open();
            socket.connect();
        }
    }


    private static void checkSocket() {
        if (socket == null) {
            createInstance();
        }

        if (handler == null) {
            try {
                Looper.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler = new Handler();
        }
    }


    /**
     * TODO: 로그인
     * */
    public static void login(String id, String pw, String token, final SocketListener.OnLogin onLogin) {
        try {
            checkSocket();

            JSONObject object = new JSONObject();
            // 암호화
            pw = Secure.Sha256Encrypt(pw);

            object.put(Global.ID, id);
            object.put(Global.PW, pw);
            object.put(Global.TOKEN, token);
            object.put(Global.GROUP_ID, 1);
//            socket.emit(Global.INSERT_MASTER, object);
            socket.emit(Global.LOGIN, object);
            socket.once(Global.LOGIN, new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    // 로그인
                    Log.d(TAG, "로그인 응답");
                    try {
                        final JSONObject object = (JSONObject) args[0];
                        final int code = object.getInt(Global.CODE);
                        Log.d(TAG, "code = " + code);
                        if (code == Global.SUCCESS) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONArray userArray = object.getJSONArray(Global.USER);
                                        UserEntity user = null;
                                        List<GroupEntity> groupEntities = new ArrayList<GroupEntity>();
                                        for (int i = 0; i < userArray.length(); i++) {
                                            JSONObject userObject = userArray.getJSONObject(i);
                                            Log.d(TAG, "userObject = " + userObject);
                                            if (user == null)
                                                user = new UserEntity(userObject);
                                            GroupEntity groupEntity = new GroupEntity(userObject);
                                            if (groupEntity.getName() != null)
                                                groupEntities.add(groupEntity);
                                        }

                                        onLogin.onSuccess(user, groupEntities);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onLogin.onException(code);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * TODO: 로그인
     * */
    public static void signUp(String id, String pw, String name, String phone, final SocketListener.OnSignUp onSignUp) {
        try {
            checkSocket();

            JSONObject object = new JSONObject();
            // 암호화
            pw = Secure.Sha256Encrypt(pw);
            Log.d(TAG, "pw = " + pw);
            object.put(Global.ID, id);
            object.put(Global.PW, pw);
            object.put(Global.NAME, name);
            object.put(Global.PHONE, phone);
            socket.emit(Global.SIGN_UP, object);
            socket.once(Global.SIGN_UP, new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    Log.d(TAG, "회원가입 응답");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject object1 = (JSONObject) args[0];
                                int code = object1.getInt(Global.CODE);
                                String id = null;
                                if (code == Global.SUCCESS) {
                                    id = object1.getString(Global.ID);
                                    onSignUp.onSuccess(id);
                                } else {
                                    onSignUp.onException(code);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void getGroupCount(int groupId, Date day, final SocketListener.OnGetGroupCount onGetGroupCount) {
        try {
            checkSocket();
            Log.d(TAG, "getGroupCount");

            JSONObject object = new JSONObject();
            object.put(Global.GROUP_ID, groupId);
            object.put(Global.DAY, day);
            socket.emit(Global.GET_GROUP_COUNT, object);
            socket.once(Global.GET_GROUP_COUNT, new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    Log.d(TAG, "getGroupCount 응답");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject resObject = (JSONObject) args[0];
                                int code = resObject.getInt(Global.CODE);
                                if (code == Global.SUCCESS) {
                                    JSONObject countObject = resObject.getJSONObject(Global.COUNT);
                                    int count = countObject.getInt("COUNT(*)");
                                    onGetGroupCount.onSuccess(count);
                                } else {
                                    onGetGroupCount.onException();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getUserCount(String userId, Date day, final int position, final SocketListener.OnGetUserCount onGetUserCount) {
        try {
            checkSocket();
            Log.d(TAG, "getUserCount");

            JSONObject object = new JSONObject();
            object.put(Global.ID, userId);
            Log.d(TAG, "id = " + userId);
            object.put(Global.DAY, day);
            socket.emit(Global.GET_USER_COUNT, object);
            socket.once(Global.GET_USER_COUNT, new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    Log.d(TAG, "getUserCount 응답");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject resObject = (JSONObject) args[0];
                                int code = resObject.getInt(Global.CODE);
                                if (code == Global.SUCCESS) {
                                    JSONObject countObject = resObject.getJSONObject(Global.COUNT);
                                    int count = countObject.getInt("COUNT(*)");
                                    onGetUserCount.onSuccess(count, position);
                                } else {
                                    onGetUserCount.onException();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void getGroup(int groupId, final SocketListener.OnGetGroup onGetGroup) {
        checkSocket();
        Log.d(TAG, "getGroup");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, groupId);
            socket.emit(Global.GET_GROUP, object);
            socket.once(Global.GET_GROUP, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        final JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (code == Global.SUCCESS) {
                                        JSONArray groupArray = resObject.getJSONArray(Global.GROUP);
                                        Log.d(TAG, "groupArray = " + groupArray);
                                        GroupEntity groupEntity = new GroupEntity(groupArray);
                                        onGetGroup.onSuccess(groupEntity);
                                    } else {
                                        onGetGroup.onException();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setChangeMsg(String userId, int isAble, final SocketListener.OnSetChangeMsg onSetChangeMsg) {
        checkSocket();
        Log.d(TAG, "setChangeMsg");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, userId);
            object.put(Global.IS_ABLE, isAble);
            socket.emit(Global.SET_CHANGE_MSG, object);
            socket.once(Global.SET_CHANGE_MSG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onSetChangeMsg.onSuccess();
                                } else {
                                    onSetChangeMsg.onException();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void insertMsg(MsgEntity msgEntity, final SocketListener.OnInsertMsg onInsertMsg) {
        checkSocket();
        Log.d(TAG, "insertMsg");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, msgEntity.getUserId());
            object.put(MsgEntity.PROPERTY_PORT, msgEntity.getPort());
            object.put(MsgEntity.PROPERTY_CONTENT, msgEntity.getContent());
            object.put(MsgEntity.PROPERTY_IS_EVERYBODY, msgEntity.getIsEverybody());
            socket.emit(Global.INSERT_MSG, object);
            socket.once(Global.INSERT_MSG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onInsertMsg.onSuccess();
                                } else {
                                    onInsertMsg.onException();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void updateMsg(MsgEntity msgEntity, final SocketListener.OnUpdateMsg onUpdateMsg) {
        checkSocket();
        Log.d(TAG, "updateMsg");
        try {
            JSONObject object = new JSONObject();
            object.put(MsgEntity.PROPERTY_PORT, msgEntity.getPort());
            object.put(MsgEntity.PROPERTY_ID, msgEntity.getId());
            object.put(MsgEntity.PROPERTY_CONTENT, msgEntity.getContent());
            object.put(MsgEntity.PROPERTY_IS_EVERYBODY, msgEntity.getIsEverybody());
            socket.emit(Global.UPDATE_MSG, object);
            socket.once(Global.UPDATE_MSG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onUpdateMsg.onSuccess();
                                } else {
                                    onUpdateMsg.onException();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMsg(MsgEntity msgEntity, final SocketListener.OnDeleteMsg onDeleteMsg) {
        checkSocket();
        Log.d(TAG, "deleteMsg");
        try {
            JSONObject object = new JSONObject();
            object.put(MsgEntity.PROPERTY_ID, msgEntity.getId());
            socket.emit(Global.DELETE_MSG, object);
            socket.once(Global.DELETE_MSG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onDeleteMsg.onSuccess();
                                } else {
                                    onDeleteMsg.onException();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void getMsg(String userId, final SocketListener.OnGetMsg onGetMsg) {
        checkSocket();
        Log.d(TAG, "getMsg");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, userId);
            socket.emit(Global.GET_MSG, object);
            socket.once(Global.GET_MSG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        final JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        if (code == Global.SUCCESS) {
                            JSONArray msgArray = resObject.getJSONArray(Global.MSG);
                            final ArrayList<MsgEntity> msgEntities = new ArrayList<MsgEntity>();
                            for (int i = 0; i < msgArray.length(); i++) {
                                JSONObject msgObject = msgArray.getJSONObject(i);
                                MsgEntity msg = new MsgEntity(msgObject);
                                msgEntities.add(msg);
                            }

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGetMsg.onSuccess(msgEntities);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGetMsg.onException();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void connect(String id) {
        Log.d(TAG, "connect");
        checkSocket();
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, id);
            socket.emit(Global.CONNECTION, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect(String id) {
        Log.d(TAG, "disconnect");
        checkSocket();
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, id);
            socket.emit(Global.DISCONNECTION, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void sendMsg(String id, final SocketListener.OnSendMsg onSendMsg) {
        Log.d(TAG, "sendMsg");
        checkSocket();
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, id);
            socket.emit(Global.SEND_MSG, object);
            socket.once(Global.SEND_MSG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        int code = resObject.getInt(Global.CODE);
                        if (code == Global.SUCCESS) {
                            onSendMsg.onSuccess();
                        } else {
                            onSendMsg.onException();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
