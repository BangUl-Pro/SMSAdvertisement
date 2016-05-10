package com.ironfactory.smsapplication.networks;

import com.ironfactory.smsapplication.entities.GroupEntity;
import com.ironfactory.smsapplication.entities.MsgEntity;
import com.ironfactory.smsapplication.entities.UserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IronFactory on 2016. 1. 26..
 */
public class SocketListener {

    public interface OnGetGroupCount {
        void onSuccess(int count);
        void onException();
    }

    public interface OnGetUserCount {
        void onSuccess(int count, int position);
        void onException();
    }

    public interface OnDeleteMsg {
        void onSuccess();
        void onException();
    }

    public interface OnUpdateMsg {
        void onSuccess();
        void onException();
    }

    public interface OnGetMsg {
        void onSuccess(ArrayList<MsgEntity> msgEntities);
        void onException();
    }

    public interface OnInsertMsg {
        void onSuccess();
        void onException();
    }

    public interface OnSetChangeMsg {
        void onSuccess();
        void onException();
    }

    public interface OnSendMsg {
        void onSuccess();
        void onException();
    }

    public interface OnSignUp {
        void onSuccess(String id);
        void onException(int code);
    }

    public interface OnGetCount {
        void onSuccess(int count);
        void onException();
    }

    public interface OnGetGroup {
        void onSuccess(GroupEntity groupEntity);
        void onException();
    }

    public interface OnLogin {
        void onSuccess(UserEntity userEntity, List<GroupEntity> groupEntities);
        void onException(int code);
    }
}
