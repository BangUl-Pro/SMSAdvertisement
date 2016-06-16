package com.adplan.smsapplication.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by IronFactory on 2016. 2. 8..
 */
public class MsgEntity implements Parcelable {

    public static final int EVERYONE = 1;
    public static final int NOT_EVERYONE = 2;

    public static final String PROPERTY = "msgs";
    public static final String PROPERTY_ID = "msg_id";
    public static final String PROPERTY_USER_ID = "msg_user_id";
    public static final String PROPERTY_PORT = "msg_port";
    public static final String PROPERTY_CONTENT = "msg_content";
    public static final String PROPERTY_IS_EVERYBODY = "msg_is_everybody";

    private int id;
    private String userId;
    private String port;
    private String content;
    private int isEverybody;

    public MsgEntity() {
    }

    public MsgEntity(Parcel parcel) {
        readBundle(parcel.readBundle());
    }

    public MsgEntity(JSONObject object) {
        try {
            id = object.getInt(PROPERTY_ID);
            userId = object.getString(PROPERTY_USER_ID);
            port = object.getString(PROPERTY_PORT);
            content = object.getString(PROPERTY_CONTENT);
            if (object.get(PROPERTY_IS_EVERYBODY) != null)
                isEverybody = object.getInt(PROPERTY_IS_EVERYBODY);
            else
                isEverybody = NOT_EVERYONE;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static Creator<MsgEntity> CREATOR = new Creator<MsgEntity>() {
        @Override
        public MsgEntity createFromParcel(Parcel source) {
            return new MsgEntity(source);
        }

        @Override
        public MsgEntity[] newArray(int size) {
            return new MsgEntity[size];
        }
    };

    public Bundle writeBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(PROPERTY_ID, id);
        bundle.putString(PROPERTY_USER_ID, userId);
        bundle.putString(PROPERTY_PORT, port);
        bundle.putString(PROPERTY_CONTENT, content);
        bundle.putInt(PROPERTY_IS_EVERYBODY, isEverybody);
        return bundle;
    }

    public void readBundle(Bundle bundle) {
        id = bundle.getInt(PROPERTY_ID);
        userId = bundle.getString(PROPERTY_USER_ID);
        port = bundle.getString(PROPERTY_PORT);
        content = bundle.getString(PROPERTY_CONTENT);
        isEverybody = bundle.getInt(PROPERTY_IS_EVERYBODY);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(writeBundle());
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIsEverybody() {
        return isEverybody;
    }

    public void setIsEverybody(int isEverybody) {
        this.isEverybody = isEverybody;
    }
}
