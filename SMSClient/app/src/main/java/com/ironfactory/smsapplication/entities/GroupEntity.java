package com.ironfactory.smsapplication.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IronFactory on 2016. 2. 6..
 */
public class GroupEntity implements Parcelable {

    private static final String PROPERTY = "groups";
    private static final String PROPERTY_ID = "group_id";
    private static final String PROPERTY_NAME = "group_name";
    private static final String PROPERTY_MASTER = "group_master";
    private static final String PROPERTY_MASTER_ID = "master_id";
    private static final String PROPERTY_MEMBER = "group_member";
    private static final String PROPERTY_COIN = "group_coin";
    private static final String PROPERTY_MEMBER_ID = "member_user_id";
    private static final String PROPERTY_MEMBER_GROUP_ID = "member_group_id";

    private int id;
    private String name;
    private int maxCoin;
    private List<String> masters;
    private List<UserEntity> members;

    public static final Creator<GroupEntity> CREATOR = new Creator<GroupEntity>() {
        @Override
        public GroupEntity createFromParcel(Parcel source) {
            return new GroupEntity(source);
        }

        @Override
        public GroupEntity[] newArray(int size) {
            return new GroupEntity[size];
        }
    };

    public GroupEntity() {
        masters = new ArrayList<>();
        members = new ArrayList<>();
    }

//    public GroupEntity(int id, String name) {
//        this.id = id;
//        this.name = name;
//        this.masters = masters;
//        this.members = members;
//    }

    public GroupEntity(Parcel parcel) {
        readBundle(parcel.readBundle());
    }

    public GroupEntity(JSONObject object) {
        try {
            if (!object.get(PROPERTY_ID).equals(null))
                id = object.getInt(PROPERTY_ID);
            if (!object.get(PROPERTY_NAME).equals(null))
                name = object.getString(PROPERTY_NAME);
            if (!object.get(PROPERTY_COIN).equals(null))
                maxCoin = object.getInt(PROPERTY_COIN);
            members = new ArrayList<>();
            masters = new ArrayList<>();
            if (!object.get(PROPERTY_MEMBER_ID).equals(null)) {
                UserEntity userEntity = new UserEntity(object);
                userEntity.setId(object.getString(PROPERTY_MEMBER_ID));
                userEntity.setName(object.getString(PROPERTY_MEMBER_GROUP_ID));
                members.add(userEntity);
            }

            if (!object.get(PROPERTY_MASTER_ID).equals(null)) {
                masters.add(object.getString(PROPERTY_MASTER_ID));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public GroupEntity(JSONArray array) {
        try {
            JSONObject resObject = array.getJSONObject(0);
            if (!resObject.get(PROPERTY_ID).equals(null))
                id = resObject.getInt(PROPERTY_ID);
            if (!resObject.get(PROPERTY_NAME).equals(null))
                name = resObject.getString(PROPERTY_NAME);
            if (!resObject.get(PROPERTY_COIN).equals(null))
                maxCoin = resObject.getInt(PROPERTY_COIN);
            members = new ArrayList<>();
            masters = new ArrayList<>();
            boolean timeToAddMaster = false;
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (!object.get(PROPERTY_MASTER_ID).equals(null)) {
                    String master = object.getString(PROPERTY_MASTER_ID);
                    if (masters.size() > 0) {
                        if (!masters.get(masters.size() - 1).equals(master)) {
                            // 앞의 마스터와 같지 않다면 더해라
                            masters.add(master);
                            timeToAddMaster = true;
                        }
                    } else {
                        masters.add(master);
                    }
                }
                if (!timeToAddMaster && !object.get(PROPERTY_MEMBER_ID).equals(null)) {
                    UserEntity userEntity = new UserEntity(object);
                    if (userEntity.getId() == null)
                        userEntity.setId(object.getString(PROPERTY_MEMBER_ID));
                    Log.d("getGroup", "name = " + userEntity.getName());
                    members.add(userEntity);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addMember(UserEntity userEntity) {
        members.add(userEntity);
    }

    public void addMaster(String id) {
        masters.add(id);
    }

    public void add(GroupEntity groupEntity) {
        if (groupEntity.getMembers().size() > 0)
            members.add(groupEntity.getMembers().get(0));

        if (groupEntity.getMasters().size() > 0)
            masters.add(groupEntity.getMasters().get(0));
    }

    public boolean isSame(int groupId) {
        if (id == groupId)
            return true;
        return false;
    }

    public Bundle writeBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(PROPERTY_ID, id);
        bundle.putString(PROPERTY_NAME, name);
        bundle.putStringArrayList(PROPERTY_MASTER, new ArrayList<String>(masters));
        bundle.putParcelableArrayList(PROPERTY_MEMBER, new ArrayList<Parcelable>(members));
        bundle.putInt(PROPERTY_COIN, maxCoin);
        return bundle;
    }

    public void readBundle(Bundle bundle) {
        bundle.setClassLoader(GroupEntity.class.getClassLoader());
        id = bundle.getInt(PROPERTY_ID);
        name = bundle.getString(PROPERTY_NAME);
        masters = bundle.getStringArrayList(PROPERTY_MASTER);
        members = bundle.getParcelableArrayList(PROPERTY_MEMBER);
        maxCoin = bundle.getInt(PROPERTY_COIN);
    }

    public int getMaxCoin() {
        return maxCoin;
    }

    public void setMaxCoin(int maxCoin) {
        this.maxCoin = maxCoin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMasters() {
        return masters;
    }

    public void setMasters(List<String> masters) {
        this.masters = masters;
    }

    public List<UserEntity> getMembers() {
        return members;
    }

    public void setMembers(List<UserEntity> members) {
        this.members = members;
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
