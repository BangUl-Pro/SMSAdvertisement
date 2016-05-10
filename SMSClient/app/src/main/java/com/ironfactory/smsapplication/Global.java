package com.ironfactory.smsapplication;

/**
 * Created by IronFactory on 15. 10. 20..
 */
public class Global {
    public static final String APP_NAME = "smsApplication";

    public static final String COMMAND = "command";
    public static final String LOGIN = "login";
    public static final String SEND_MSG = "sendMsg";
    public static final String SIGN_UP = "signUp";
    public static final String GET_COUNT = "getCount";
    public static final String GET_GROUP = "getGroup";
    public static final String INSERT_MSG = "insertMsg";
    public static final String UPDATE_MSG = "updateMsg";
    public static final String DELETE_MSG = "deleteMsg";
    public static final String GET_MSG = "getMsg";
    public static final String GET_GROUP_COUNT = "getGroupCount";
    public static final String GET_USER_COUNT = "getUserCount";
    public static final String SET_CHANGE_MSG = "setChangeMsg";
    public static final String CONNECTION = "connection";
    public static final String DISCONNECTION = "disconnection";

    public static final String ID = "id";
    public static final String PW = "pw";
    public static final String TOKEN = "token";
    public static final String CODE = "code";
    public static final String CONTENT = "content";
    public static final String NUM = "num";
    public static final String KEY = "key";
    public static final String DAY = "day";
    public static final String GROUP_ID = "group_id";
    public static final String COUNT = "count";
    public static final String GROUP = "group";
    public static final String USER = "user";
    public static final String MSG = "msg";
    public static final String PHONE = "phone";
    public static final String NAME = "name";
    public static final String IS_ABLE = "isAble";
    public static final String INSERT_MASTER = "insertMaster";

    public static final String SENT_BROADCAST = "sent msg";

    public static final String DELIVERED_BROADCAST = "delivered msg";

    // 성공
    public static final int SUCCESS = 200;
    // 로그인 중 DB 쿼리 에러 (네트워크)
    public static final int CODE_LOGIN_DB_ERR = 325;
    // 로그인 중 일치하는 아이디가 없음
    public static final int CODE_LOGIN_NO_ID = 326;
    // 로그인 중 소켓 정보 미등록 (네트워크)
    public static final int CODE_LOGIN_NO_SOCKET = 327;
    // 로그인 중 값 누락
    public static final int CODE_LOGIN_NOT_ENOUGH_DATA = 328;
    // 회원가입 중 값이 누락 됨
    public static final int CODE_SIGN_UP_NOT_ENOUGH_DATA = 500;
    // 회원가입 중 아이디 중복검사 에러
    public static final int CODE_SIGN_UP_FAIL_TO_CHECK_ID = 501;
    // 회원가입 중 아이디 중복
    public static final int CODE_SIGN_UP_OVERLAP_ID = 502;
    // 회원가입 중 DB 입력 에러
    public static final int CODE_SIGN_UP_FAIL_TO_WRITE_DB = 503;
}
