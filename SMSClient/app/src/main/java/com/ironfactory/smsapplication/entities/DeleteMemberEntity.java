package com.ironfactory.smsapplication.entities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by IronFactory on 2016. 2. 14..
 */
public class DeleteMemberEntity {

    public static final String PROPERTY = "delete_members";
    public static final String PROPERTY_USER_ID = "delete_member_id";
    public static final String PROPERTY_GROUP_ID = "delete_member_group_id";

    private String userId;
    private int groupId;

    public DeleteMemberEntity() {

    }

    public DeleteMemberEntity(JSONObject object) {
        try {
            userId = object.getString(PROPERTY_USER_ID);
            groupId = object.getInt(PROPERTY_GROUP_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
