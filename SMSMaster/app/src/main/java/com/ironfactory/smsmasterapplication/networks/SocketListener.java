package com.ironfactory.smsmasterapplication.networks;

import com.ironfactory.smsmasterapplication.entities.DeleteMemberEntity;
import com.ironfactory.smsmasterapplication.entities.GroupEntity;
import com.ironfactory.smsmasterapplication.entities.MsgEntity;
import com.ironfactory.smsmasterapplication.entities.UserEntity;

import java.util.List;

/**
 * Created by IronFactory on 2016. 1. 26..
 */
public class SocketListener {

    public interface OnGetAllGroup {
        void onSuccess(List<GroupEntity> groupEntities);
        void onException();
    }

    public interface OnChargeCoin {
        void onSuccess();
        void onException();
    }

    public interface OnCancelChargeCoin {
        void onSuccess();
        void onException();
    }

    public interface OnChargeCoinReq {
        void onSuccess();
        void onException();
    }

    public interface OnDeleteMember {
        void onSuccess();
        void onException();
    }

    public interface OnCancelDeleteMember {
        void onSuccess();
        void onException();
    }

    public interface OnDeleteMemberReq {
        void onSuccess();
        void onException();
    }

    public interface OnUpdateGroupName {
        void onSuccess();
        void onException();
    }

    public interface OnInsertGroup {
        void onSuccess(GroupEntity groupEntity);
        void onException(int code);
    }

    public interface OnCancelDeleteUser {
        void onSuccess();
        void onException();
    }

    public interface OnGetDeleteUser {
        void onSuccess(List<String> userList);
        void onException();
    }

    public interface OnGetDeleteMember {
        void onSuccess(List<DeleteMemberEntity> userList);
        void onException();
    }

    public interface OnGetChargeCoin {
        void onSuccess(List<String> userList);
        void onException();
    }

    public interface OnUpdateUserId {
        void onSuccess();
        void onException();
    }

    public interface OnUpdateUserPw {
        void onSuccess();
        void onException();
    }

    public interface OnInsertMember {
        void onSuccess(UserEntity userEntity);
        void onException(int code);
    }

    public interface OnInsertMaster {
        void onSuccess();
        void onException(int code);
    }

    public interface OnDeleteMaster {
        void onSuccess();
        void onException(int code);
    }

    public interface OnDeleteUserReq {
        void onSuccess();
        void onException();
    }

    public interface OnDeleteUser {
        void onSuccess();
        void onException();
    }

    public interface OnGetGroup {
        void onSuccess(GroupEntity groupEntity);
        void onException();
    }

    public interface OnInsertMsg {
        void onSuccess();
        void onException();
    }

    public interface OnUpdateMsg {
        void onSuccess();
        void onException();
    }

    public interface OnGetMsg {
        void onSuccess(List<MsgEntity> msgEntities);
        void onException();
    }

    public interface OnSetInfiniteCoin {
        void onSuccess();
        void onException();
    }

    public interface OnSetSend {
        void onSuccess();
        void onException();
    }

    public interface OnSetShowAdDetail {
        void onSuccess();
        void onException();
    }

    public interface OnSetChangeMsg {
        void onSuccess();
        void onException();
    }

    public interface OnGetGroupCount {
        void onSuccess(int count);
        void onException();
    }

    public interface OnGetUserCount {
        void onSuccess(int count, int position);
        void onException();
    }

    public interface OnLogin {
        void onSuccess(UserEntity userEntity, List<GroupEntity> groupEntities);
        void onException(int code);
    }

    public interface OnGetGroupName {
        void onSuccess(String name);
        void onException();
    }

    public interface OnDeleteGroup {
        void onSuccess();
        void onException();
    }

    public interface OnSetMaxCoin {
        void onSuccess();
        void onException();
    }
}
