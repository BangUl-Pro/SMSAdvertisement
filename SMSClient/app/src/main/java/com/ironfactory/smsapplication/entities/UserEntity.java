package com.ironfactory.smsapplication.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.ironfactory.smsapplication.Sort;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IronFactory on 2016. 2. 6..
 */
public class UserEntity extends Sort.SortTypes implements Parcelable {

    public static final String PROPERTY = "users";
    public static final String PROPERTY_ID = "user_id";
    public static final String PROPERTY_MAIL = "user_mail";
    public static final String PROPERTY_NAME = "user_name";
    public static final String PROPERTY_BIRTH = "user_birth";
    public static final String PROPERTY_COIN = "user_coin";
    public static final String PROPERTY_PHONE = "user_phone";
    public static final String PROPERTY_PW = "user_pw";
    public static final String PROPERTY_MSG = "user_msg";
    public static final String PROPERTY_IS_CONNECTED = "user_is_connected";
    public static final String PROPERTY_AUTH_CHANGE_MSG = "user_authority_change_msg";
    public static final String PROPERTY_AUTH_SHOW_AD_DETAIL = "user_authority_show_ad_detail";
    public static final String PROPERTY_AUTH_SEND_PHONE = "user_authority_send_phone";
    public static final String PROPERTY_AUTH_INFINITE_COIN = "user_authority_infinite_coin";

    public static final int CONNECTED = 1;
    public static final int DISCONNECTED = 2;
    public static final int ABLE = 1;
    public static final int DISABLE = 2;

    private String id;
    private String mail;
    private String name;
    private long birth;
    private int coin = 0;
    private boolean isConnected;
    private boolean ableChangeMsg;
    private boolean ableShowAdDetail;
    private boolean ableSendPhone;
    private boolean infiniteCoin;
    private String phone;
    private String password;
    private List<MsgEntity> msgEntities = new ArrayList<>();

    public UserEntity() {
    }

    public UserEntity(Parcel parcel) {
        readBundle(parcel.readBundle());
    }

//    public UserEntity(String id, String mail, String name, long birth, int coin, boolean isConnected, boolean ableChangeMsg, boolean ableSendPhone, boolean ableShowAdDetail, String phone) {
//        this.id = id;
//        this.mail = mail;
//        this.name = name;
//        this.birth = birth;
//        this.coin = coin;
//        this.isConnected = isConnected;
//        this.ableChangeMsg = ableChangeMsg;
//        this.ableSendPhone = ableSendPhone;
//        this.ableShowAdDetail = ableShowAdDetail;
//        this.phone = phone;
//    }

    public UserEntity(JSONObject object) {
        try {
            if (!object.get(PROPERTY_ID).equals(null))
                id = object.getString(PROPERTY_ID);
            if (!object.get(PROPERTY_MAIL).equals(null))
                mail = object.getString(PROPERTY_MAIL);
            if (!object.get(PROPERTY_NAME).equals(null))
                name = object.getString(PROPERTY_NAME);
            if (!object.get(PROPERTY_BIRTH).equals(null))
                birth = object.getLong(PROPERTY_BIRTH);
            if (!object.get(PROPERTY_COIN).equals(null))
                coin = object.getInt(PROPERTY_COIN);

            int conn = 0;
            if (!object.get(PROPERTY_IS_CONNECTED).equals(null))
                conn = object.getInt(PROPERTY_IS_CONNECTED);
            isConnected = (conn == CONNECTED ? true : false);

            int changeMsg = 0;
            if (!object.get(PROPERTY_AUTH_CHANGE_MSG).equals(null))
                changeMsg = object.getInt(PROPERTY_AUTH_CHANGE_MSG);
            ableChangeMsg = (changeMsg == ABLE ? true : false);

            int sendPhone = 0;
            if (!object.get(PROPERTY_AUTH_SEND_PHONE).equals(null))
                sendPhone = object.getInt(PROPERTY_AUTH_SEND_PHONE);
            ableSendPhone = (sendPhone == ABLE ? true : false);

            int showDetail = 0;
            if (!object.get(PROPERTY_AUTH_SHOW_AD_DETAIL).equals(null))
                showDetail = object.getInt(PROPERTY_AUTH_SHOW_AD_DETAIL);
            ableShowAdDetail = (showDetail == ABLE ? true : false);

            if (!object.get(PROPERTY_PHONE).equals(null))
                phone = object.getString(PROPERTY_PHONE);

            if (!object.get(PROPERTY_PW).equals(null))
                password = object.getString(PROPERTY_PW);

            int infinite = 0;
            if (!object.get(PROPERTY_AUTH_INFINITE_COIN).equals(null))
                infinite = object.getInt(PROPERTY_AUTH_INFINITE_COIN);
            infiniteCoin = (infinite == ABLE ? true : false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static Creator<UserEntity> CREATOR = new Creator<UserEntity>() {
        @Override
        public UserEntity createFromParcel(Parcel source) {
            return new UserEntity(source);
        }

        @Override
        public UserEntity[] newArray(int size) {
            return new UserEntity[size];
        }
    };

    public Bundle writeBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(PROPERTY_ID, id);
        bundle.putString(PROPERTY_MAIL, mail);
        bundle.putString(PROPERTY_NAME, name);
        bundle.putLong(PROPERTY_BIRTH, birth);
        bundle.putInt(PROPERTY_COIN, coin);
        bundle.putBoolean(PROPERTY_IS_CONNECTED, isConnected);
        bundle.putBoolean(PROPERTY_AUTH_CHANGE_MSG, ableChangeMsg);
        bundle.putBoolean(PROPERTY_AUTH_SEND_PHONE, ableSendPhone);
        bundle.putBoolean(PROPERTY_AUTH_SHOW_AD_DETAIL, ableShowAdDetail);
        bundle.putBoolean(PROPERTY_AUTH_INFINITE_COIN, infiniteCoin);
        bundle.putString(PROPERTY_PHONE, phone);
        bundle.putString(PROPERTY_PW, password);
        bundle.putParcelableArrayList(PROPERTY_MSG, new ArrayList<Parcelable>(msgEntities));
        return bundle;
    }

    public void readBundle(Bundle bundle) {
        id = bundle.getString(PROPERTY_ID);
        mail = bundle.getString(PROPERTY_MAIL);
        name = bundle.getString(PROPERTY_NAME);
        birth = bundle.getLong(PROPERTY_BIRTH);
        coin = bundle.getInt(PROPERTY_COIN);
        isConnected = bundle.getBoolean(PROPERTY_IS_CONNECTED, false);
        ableChangeMsg = bundle.getBoolean(PROPERTY_AUTH_CHANGE_MSG, false);
        ableSendPhone = bundle.getBoolean(PROPERTY_AUTH_SEND_PHONE, false);
        ableShowAdDetail = bundle.getBoolean(PROPERTY_AUTH_SHOW_AD_DETAIL, false);
        infiniteCoin = bundle.getBoolean(PROPERTY_AUTH_INFINITE_COIN, false);
        phone = bundle.getString(PROPERTY_PHONE);
        password = bundle.getString(PROPERTY_PW);
        msgEntities = bundle.getParcelableArrayList(PROPERTY_MSG);
    }

    public List<MsgEntity> getMsgEntities() {
        return msgEntities;
    }

    public void setMsgEntities(List<MsgEntity> msgEntities) {
        this.msgEntities = msgEntities;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getBirth() {
        return birth;
    }

    public void setBirth(long birth) {
        this.birth = birth;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isAbleChangeMsg() {
        return ableChangeMsg;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isInfiniteCoin() {
        return infiniteCoin;
    }

    public void setInfiniteCoin(boolean infiniteCoin) {
        this.infiniteCoin = infiniteCoin;
    }

    public void setAbleChangeMsg(boolean ableChangeMsg) {
        this.ableChangeMsg = ableChangeMsg;
    }

    public boolean isAbleShowAdDetail() {
        return ableShowAdDetail;
    }

    public void setAbleShowAdDetail(boolean ableShowAdDetail) {
        this.ableShowAdDetail = ableShowAdDetail;
    }

    public boolean isAbleSendPhone() {
        return ableSendPhone;
    }

    public void setAbleSendPhone(boolean ableSendPhone) {
        this.ableSendPhone = ableSendPhone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(writeBundle());
    }

    @Override
    public int sortValue() {
        return 0;
    }
}
