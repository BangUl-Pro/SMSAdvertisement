package com.ironfactory.smsapplication.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by IronFactory on 2016. 2. 6..
 */
public class CountEntity implements Parcelable {

    public static final String PROPERTY_USER_ID = "count_user_id";
    public static final String PROPERTY_GROUP_ID = "count_group_id";
    public static final String PROPERTY_DATE = "count_date";

    private String userId;
    private String groupId;
    private Date date;

    public CountEntity() {
    }

    public CountEntity(String userId, String groupId, Date date) {
        this.userId = userId;
        this.groupId = groupId;
        this.date = date;
    }

    public CountEntity(JSONObject object) {
        try {
            userId = object.getString(PROPERTY_USER_ID);
            groupId = object.getString(PROPERTY_GROUP_ID);
            date = (Date) object.get(PROPERTY_DATE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public CountEntity(Parcel parcel) {
        readBundle(parcel.readBundle());
    }

    public static Creator<CountEntity> CREATOR = new Creator<CountEntity>() {
        @Override
        public CountEntity createFromParcel(Parcel source) {
            return new CountEntity(source);
        }

        @Override
        public CountEntity[] newArray(int size) {
            return new CountEntity[size];
        }
    };


    public Bundle writeBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(PROPERTY_USER_ID, userId);
        bundle.putString(PROPERTY_GROUP_ID, groupId);
        bundle.putLong(PROPERTY_DATE, date.getTime());
        return bundle;
    }

    public void readBundle(Bundle bundle) {
        userId = bundle.getString(PROPERTY_USER_ID);
        groupId = bundle.getString(PROPERTY_GROUP_ID);
        long dateTime = bundle.getLong(PROPERTY_DATE);
        date = new Date(dateTime);
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(writeBundle());
    }
}
