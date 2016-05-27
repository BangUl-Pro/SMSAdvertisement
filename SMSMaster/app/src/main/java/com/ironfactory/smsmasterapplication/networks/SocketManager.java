package com.ironfactory.smsmasterapplication.networks;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.ironfactory.smsmasterapplication.Global;
import com.ironfactory.smsmasterapplication.entities.ChargeCoinEntity;
import com.ironfactory.smsmasterapplication.entities.DeleteMemberEntity;
import com.ironfactory.smsmasterapplication.entities.GroupEntity;
import com.ironfactory.smsmasterapplication.entities.MsgEntity;
import com.ironfactory.smsmasterapplication.entities.UserEntity;
import com.ironfactory.smsmasterapplication.utils.Secure;

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

    private static Handler handler = new Handler();
    public static Socket socket;
    private Context context;


    public static void createInstance() {
        try {
            socket = IO.socket(URL);
        } catch (Exception e) {
            Log.e(TAG, "init 에러 = " + e.getMessage());
        }

        socketConnect();

        try {
            JSONObject object = new JSONObject();
            socket.emit("aaa", "");
//            socket.emit("createA", "");
//            object.put(Global.ID, 1);
//            object.put("id", "adplan");
//            object.put("pw", Secure.Sha256Encrypt("tg060811"));
//            object.put("name", "adplan");
//            object.put("phone", "0324668737");
//            socket.emit("signUp", object);
//
//            object = new JSONObject();
//            object.put(Global.GROUP_NAME, "adplanMasters");
//            object.put(Global.ID, 1);
//            socket.emit("insertGroup", object);
//
//            object = new JSONObject();
//            object.put(Global.GROUP_ID, 1);
//            object.put(Global.ID, "adplan");
//            socket.emit(Global.INSERT_MASTER, object);
//            socket.emit(Global.INSERT_MEMBER, object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListener() {
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
    }

    public static void login(String id, String pw, final SocketListener.OnLogin onLogin) {
        try {
            checkSocket();

            JSONObject object = new JSONObject();
            // 암호화
            pw = Secure.Sha256Encrypt(pw);

            object.put(Global.ID, id);
            object.put(Global.PW, pw);
//            object.put(Global.GROUP_ID, 1);
//            socket.emit(Global.INSERT_MASTER, object);
            socket.emit(Global.MASTER_LOGIN, object);
            socket.once(Global.MASTER_LOGIN, new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    // 로그인
                    Log.d(TAG, "로그인 응답");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final JSONObject object = (JSONObject) args[0];
                                final int code = object.getInt(Global.CODE);
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

    public static void setMaxCoin(int groupId, int coin, final SocketListener.OnSetMaxCoin onSetMaxCoin) {
        checkSocket();
        Log.d(TAG, "setMaxCoin");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, groupId);
            object.put(Global.COUNT, coin);
            socket.emit(Global.SET_MAX_COIN, object);
            socket.once(Global.SET_MAX_COIN, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onSetMaxCoin.onSuccess();
                                } else {
                                    onSetMaxCoin.onException();
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


    public static void setSend(String userId, int isAble, final SocketListener.OnSetSend onSetSend) {
        checkSocket();
        Log.d(TAG, "setSend");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, userId);
            object.put(Global.IS_ABLE, isAble);
            socket.emit(Global.SET_SEND, object);
            socket.once(Global.SET_SEND, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onSetSend.onSuccess();
                                } else {
                                    onSetSend.onException();
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

    public static void setShowAdDetail(String userId, int isAble, final SocketListener.OnSetShowAdDetail onSetShowAdDetail) {
        checkSocket();
        Log.d(TAG, "setShowAdDetail");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, userId);
            object.put(Global.IS_ABLE, isAble);
            socket.emit(Global.SET_SHOW_AD_DETAIL, object);
            socket.once(Global.SET_SHOW_AD_DETAIL, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onSetShowAdDetail.onSuccess();
                                } else {
                                    onSetShowAdDetail.onException();
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

    public static void setInfiniteCoin(String userId, int isAble, final SocketListener.OnSetInfiniteCoin onSetInfiniteCoin) {
        checkSocket();
        Log.d(TAG, "setInfiniteCoin");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, userId);
            object.put(Global.IS_ABLE, isAble);
            socket.emit(Global.SET_INFINITE_COIN, object);
            socket.once(Global.SET_INFINITE_COIN, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onSetInfiniteCoin.onSuccess();
                                } else {
                                    onSetInfiniteCoin.onException();
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
                            final List<MsgEntity> msgEntities = new ArrayList<MsgEntity>();
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

    public static void insertMaster(int groupId, String masterId, final SocketListener.OnInsertMaster onInsertMaster) {
        checkSocket();
        Log.d(TAG, "insertMaster");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.GROUP_ID, groupId);
            object.put(Global.ID, masterId);
            socket.emit(Global.INSERT_MASTER, object);
            socket.once(Global.INSERT_MASTER, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        Log.d(TAG, "resObject = " + resObject);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onInsertMaster.onSuccess();
                                } else {
                                    onInsertMaster.onException(code);
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

    public static void deleteMaster(int groupId, String masterId, final SocketListener.OnDeleteMaster onDeleteMaster) {
        checkSocket();
        Log.d(TAG, "deleteMaster");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.GROUP_ID, groupId);
            object.put(Global.ID, masterId);
            socket.emit(Global.DELETE_MASTER, object);
            socket.once(Global.DELETE_MASTER, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onDeleteMaster.onSuccess();
                                } else {
                                    onDeleteMaster.onException(code);
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

    public static void deleteUserReq(String userId, final SocketListener.OnDeleteUserReq onDeleteUserReq) {
        checkSocket();
        Log.d(TAG, "deleteUserReq");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, userId);
            socket.emit(Global.DELETE_USER_REQ, object);
            socket.once(Global.DELETE_USER_REQ, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onDeleteUserReq.onSuccess();
                                } else {
                                    onDeleteUserReq.onException();
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

    public static void deleteUser(String userId, final SocketListener.OnDeleteUser onDeleteUser) {
        checkSocket();
        Log.d(TAG, "deleteUser");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, userId);
            socket.emit(Global.DELETE_USER, object);
            socket.once(Global.DELETE_USER, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        Log.d(TAG, "code = " + code);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onDeleteUser.onSuccess();
                                } else {
                                    onDeleteUser.onException();
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

    public static void cancelDeleteUser(String userId, final SocketListener.OnCancelDeleteUser onCancelDeleteUser) {
        checkSocket();
        Log.d(TAG, "cancelDeleteUser");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, userId);
            socket.emit(Global.CANCEL_DELETE_USER, object);
            socket.once(Global.CANCEL_DELETE_USER, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onCancelDeleteUser.onSuccess();
                                } else {
                                    onCancelDeleteUser.onException();
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

    public static void insertMember(int groupId, String userId, final SocketListener.OnInsertMember onInsertMember) {
        checkSocket();
        Log.d(TAG, "insertMember");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.GROUP_ID, groupId);
            object.put(Global.ID, userId);
            socket.emit(Global.INSERT_MEMBER, object);
            socket.once(Global.INSERT_MEMBER, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        final JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    try {
                                        JSONObject userObject = resObject.getJSONObject(Global.USER);
                                        UserEntity userEntity = new UserEntity(userObject);
                                        onInsertMember.onSuccess(userEntity);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    onInsertMember.onException(code);
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

    public static void updateUserId(String userId, String curUserId, final SocketListener.OnUpdateUserId onUpdateUserId) {
        checkSocket();
        Log.d(TAG, "updateUserId");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.USER, userId);
            object.put(Global.ID, curUserId);
            socket.emit(Global.UPDATE_USER_ID, object);
            socket.once(Global.UPDATE_USER_ID, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onUpdateUserId.onSuccess();
                                } else {
                                    onUpdateUserId.onException();
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

    public static void updateUserPw(String userId, String pw, final SocketListener.OnUpdateUserPw onUpdateUserPw) {
        checkSocket();
        Log.d(TAG, "updateUserPw");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, userId);
            pw = Secure.Sha256Encrypt(pw);
            object.put(Global.PW, pw);
            socket.emit(Global.UPDATE_USER_PW, object);
            socket.once(Global.UPDATE_USER_PW, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onUpdateUserPw.onSuccess();
                                } else {
                                    onUpdateUserPw.onException();
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


    public static void getDeleteUserList(final SocketListener.OnGetDeleteUser onGetDeleteUser) {
        checkSocket();
        Log.d(TAG, "getDeleteUserList");
        socket.emit(Global.GET_DELETE_USER_LIST, "");
        socket.once(Global.GET_DELETE_USER_LIST, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    final JSONObject object = (JSONObject) args[0];
                    final int code = object.getInt(Global.CODE);
                    if (code == Global.SUCCESS) {
                        JSONArray userArray = object.getJSONArray(Global.USER);
                        final List<String> userList = new ArrayList<String>();
                        for (int i = 0; i < userArray.length(); i++) {
                            JSONObject userObject = userArray.getJSONObject(i);
                            userList.add(userObject.getString(Global.DELETE_USER_ID));
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onGetDeleteUser.onSuccess(userList);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onGetDeleteUser.onException();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static void getDeleteMemberList(final SocketListener.OnGetDeleteMember onGetDeleteMember) {
        checkSocket();
        Log.d(TAG, "getDeleteMemberList");
        socket.emit(Global.GET_DELETE_MEMBER_LIST, "");
        socket.once(Global.GET_DELETE_MEMBER_LIST, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    final JSONObject object = (JSONObject) args[0];
                    final int code = object.getInt(Global.CODE);
                    if (code == Global.SUCCESS) {
                        JSONArray userArray = object.getJSONArray(Global.USER);
                        final List<DeleteMemberEntity> userList = new ArrayList<DeleteMemberEntity>();
                        for (int i = 0; i < userArray.length(); i++) {
                            JSONObject userObject = userArray.getJSONObject(i);
                            Log.d(TAG, "userObject = " + userObject);
                            userList.add(new DeleteMemberEntity(userObject));
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onGetDeleteMember.onSuccess(userList);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onGetDeleteMember.onException();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void insertGroup(String groupName, final SocketListener.OnInsertGroup onInsertGroup) {
        checkSocket();
        Log.d(TAG, "insertGroup");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.GROUP_NAME, groupName);
            socket.emit(Global.INSERT_GROUP, object);
            socket.once(Global.INSERT_GROUP, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        final JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        if (code == Global.SUCCESS) {
                            JSONObject groupObject = resObject.getJSONObject(Global.GROUP);
                            final GroupEntity groupEntity = new GroupEntity(groupObject);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onInsertGroup.onSuccess(groupEntity);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onInsertGroup.onException(code);
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

    public static void updateGroupName(int groupId, String name, final SocketListener.OnUpdateGroupName onUpdateGroupName) {
        checkSocket();
        Log.d(TAG, "updateGroupName");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.GROUP_ID, groupId);
            object.put(Global.GROUP_NAME, name);
            socket.emit(Global.UPDATE_GROUP_NAME, object);
            socket.once(Global.UPDATE_GROUP_NAME, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onUpdateGroupName.onSuccess();
                                } else {
                                    onUpdateGroupName.onException();
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

    public static void deleteMemberReq(int groupId, String userId, final SocketListener.OnDeleteMemberReq onDeleteMemberReq) {
        checkSocket();
        Log.d(TAG, "deleteMemberReq");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.GROUP_ID, groupId);
            object.put(Global.ID, userId);
            socket.emit(Global.DELETE_MEMBER_REQ, object);
            socket.once(Global.DELETE_MEMBER_REQ, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onDeleteMemberReq.onSuccess();
                                } else {
                                    onDeleteMemberReq.onException();
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

    public static void deleteMember(int groupId, String userId, final SocketListener.OnDeleteMember onDeleteMember) {
        checkSocket();
        Log.d(TAG, "deleteMember");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.GROUP_ID, groupId);
            object.put(Global.ID, userId);
            socket.emit(Global.DELETE_MEMBER, object);
            socket.once(Global.DELETE_MEMBER, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onDeleteMember.onSuccess();
                                } else {
                                    onDeleteMember.onException();
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

    public static void cancelDeleteMember(int groupId, String userId, final SocketListener.OnCancelDeleteMember onCancelDeleteMember) {
        checkSocket();
        Log.d(TAG, "cancelDeleteMember");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.GROUP_ID, groupId);
            object.put(Global.ID, userId);
            socket.emit(Global.CANCEL_DELETE_MEMBER, object);
            socket.once(Global.CANCEL_DELETE_MEMBER, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onCancelDeleteMember.onSuccess();
                                } else {
                                    onCancelDeleteMember.onException();
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

    public static void chargeCoinReq(String userId, int coin, final SocketListener.OnChargeCoinReq onChargeCoinReq) {
        checkSocket();
        Log.d(TAG, "chargeCoinReq");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, userId);
            object.put(Global.COIN, coin);
            socket.emit(Global.CHARGE_COIN_REQ, object);
            socket.once(Global.CHARGE_COIN_REQ, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onChargeCoinReq.onSuccess();
                                } else {
                                    onChargeCoinReq.onException();
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

    public static void chargeCoin(String userId, int coin, final SocketListener.OnChargeCoin onChargeCoin) {
        checkSocket();
        Log.d(TAG, "chargeCoin");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, userId);
            object.put(Global.COIN, coin);
            socket.emit(Global.CHARGE_COIN, object);
            socket.once(Global.CHARGE_COIN, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onChargeCoin.onSuccess();
                                } else {
                                    onChargeCoin.onException();
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

    public static void cancelChargeCoin(String userId, final SocketListener.OnCancelChargeCoin onCancelChargeCoin) {
        checkSocket();
        Log.d(TAG, "cancelChargeCoin");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, userId);
            socket.emit(Global.CANCEL_CHARGE_COIN, object);
            socket.once(Global.CANCEL_CHARGE_COIN, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onCancelChargeCoin.onSuccess();
                                } else {
                                    onCancelChargeCoin.onException();
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

    public static void getChargeCoinList(final SocketListener.OnGetChargeCoin onGetChargeCoin) {
        checkSocket();
        Log.d(TAG, "getChargeCoinList");
        socket.emit(Global.GET_CHARGE_COIN_LIST, "");
        socket.once(Global.GET_CHARGE_COIN_LIST, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    final JSONObject object = (JSONObject) args[0];
                    final int code = object.getInt(Global.CODE);
                    if (code == Global.SUCCESS) {
                        JSONArray userArray = object.getJSONArray(Global.USER);
                        final List<ChargeCoinEntity> userList = new ArrayList<>();
                        for (int i = 0; i < userArray.length(); i++) {
                            JSONObject userObject = userArray.getJSONObject(i);
                            userList.add(new ChargeCoinEntity(userObject));
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onGetChargeCoin.onSuccess(userList);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onGetChargeCoin.onException();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getAllGroup(final SocketListener.OnGetAllGroup onGetAllGroup) {
        checkSocket();
        Log.d(TAG, "getAllGroup");
        socket.emit(Global.GET_ALL_GROUP, "");
        socket.once(Global.GET_ALL_GROUP, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject resObject = (JSONObject) args[0];
                    int code = resObject.getInt(Global.CODE);
                    if (code == Global.SUCCESS) {
                        final List<GroupEntity> groupEntities = new ArrayList<GroupEntity>();
                        JSONArray groupArray = resObject.getJSONArray(Global.GROUP);
                        Log.d(TAG, "groupArray = " + groupArray);
                        int j = 0;
                        for (int i = 0; i < groupArray.length(); i++) {
                            JSONObject groupObject = groupArray.getJSONObject(i);
                            GroupEntity groupEntity = new GroupEntity(groupObject);
                            Log.d(TAG, "id = " + groupEntity.getId());
                            Log.d(TAG, "name = " + groupEntity.getName());
                            if (i == 0 || !groupEntities.get(j - 1).isSame(groupEntity.getId())) {
                                groupEntities.add(groupEntity);
                                j++;
                            } else {
                                groupEntities.get(j - 1).add(groupEntity);
                            }
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onGetAllGroup.onSuccess(groupEntities);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onGetAllGroup.onException();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getGroupName(int id, final SocketListener.OnGetGroupName onGetGroupName) {
        checkSocket();
        Log.d(TAG, "getGroupName");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, id);
            socket.emit(Global.GET_GROUP_NAME, object);
            socket.once(Global.GET_GROUP_NAME, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        int code = resObject.getInt(Global.CODE);
                        if (code == Global.SUCCESS) {
                            JSONObject groupObject = resObject.getJSONObject(Global.GROUP);
                            final String name = groupObject.getString(Global.GROUP_NAME);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGetGroupName.onSuccess(name);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGetGroupName.onException();
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

    public static void deleteGroup(int groupId, final SocketListener.OnDeleteGroup onDeleteGroup) {
        checkSocket();
        Log.d(TAG, "deleteGroup");

        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, groupId);
            socket.emit(Global.DELETE_GROUP, object);
            socket.once(Global.DELETE_GROUP, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == Global.SUCCESS) {
                                    onDeleteGroup.onSuccess();
                                } else {
                                    onDeleteGroup.onException();
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
}
