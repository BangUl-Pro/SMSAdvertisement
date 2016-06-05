package com.ironfactory.smsmasterapplication;

/**
 * Created by IronFactory on 2016. 2. 7..
 */
public class Global {
    public static final String APP_NAME = "SMS MASTER APP";
    public static final int MASTER_GROUP_ID = 1;
    public static final String MASTER_GROUP_NAME = "adplanMasters";
    public static final String SUPER_MASTER_ID = "adplan";

    // command
    public static final String MASTER_LOGIN = "masterLogin";
    public static final String GET_GROUP_COUNT = "getGroupCount";
    public static final String GET_GROUP_NAME = "getGroupName";
    public static final String GET_USER_COUNT = "getUserCount";
    public static final String SET_CHANGE_MSG = "setChangeMsg";
    public static final String SET_MAX_COIN = "setMaxCoin";
    public static final String SET_SEND = "setSend";
    public static final String SET_SHOW_AD_DETAIL = "setShowAdDetail";
    public static final String SET_INFINITE_COIN = "setInfiniteCoin";
    public static final String INSERT_MSG = "insertMsg";
    public static final String UPDATE_MSG = "updateMsg";
    public static final String DELETE_MSG = "deleteMsg";
    public static final String GET_MSG = "getMsg";
    public static final String GET_GROUP = "getGroup";
    public static final String INSERT_MASTER = "insertMaster";
    public static final String DELETE_MASTER = "deleteMaster";
    public static final String DELETE_GROUP = "deleteGroup";
    public static final String DELETE_USER_REQ = "deleteUserReq";
    public static final String DELETE_USER = "deleteUser";
    public static final String CANCEL_DELETE_USER = "cancelDeleteUser";
    public static final String INSERT_MEMBER = "insertMember";
    public static final String UPDATE_USER_ID = "updateUserId";
    public static final String UPDATE_USER_PW = "updateUserPw";
    public static final String UPDATE_GROUP_NAME = "updateGroupName";
    public static final String GET_DELETE_USER_LIST = "getDeleteUserList";
    public static final String GET_DELETE_MEMBER_LIST = "getDeleteMemberList";
    public static final String GET_CHARGE_COIN_LIST = "getChargeCoinList";
    public static final String INSERT_GROUP = "insertGroup";
    public static final String DELETE_MEMBER_REQ = "deleteMemberReq";
    public static final String DELETE_MEMBER = "deleteMember";
    public static final String CANCEL_DELETE_MEMBER = "cancelDeleteMember";
    public static final String CHARGE_COIN_REQ = "chargeCoinReq";
    public static final String CHARGE_COIN = "chargeCoin";
    public static final String CANCEL_CHARGE_COIN = "cancelChargeCoin";
    public static final String GET_ALL_GROUP = "getAllGroup";


    // key or value
    public static final String CODE = "code";
    public static final String ID = "id";
    public static final String PW = "pw";
    public static final String USER = "user";
    public static final String GROUP = "group";
    public static final String GROUP_ID = "group_id";
    public static final String GROUP_NAME = "group_name";
    public static final String DAY = "day";
    public static final String COUNT = "count";
    public static final String COIN = "coin";
    public static final String IS_ABLE = "isAble";
    public static final String MSG = "msg";
    public static final String MASTER = "master";
    public static final String MEMBER = "member";
    public static final String DELETE_USER_ID = "delete_user_id";
    public static final String CHARGE_COIN_ID = "charge_coin_id";

    public static final int SUCCESS = 200;
    public static final int CODE_LOGIN_NO_ID = 326;

    public static int USER_TYPE;
    public static final int TYPE_MASTER = 1;
    public static final int TYPE_SUPER_MASTER = 2;
}
