package com.ironfactory.smsmasterapplication.entities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by IronFactory on 2016. 5. 14..
 */
public class ChargeCoinEntity {

    private String PROPERTY_CHARGE_COIN_ID = "charge_coin_id";
    private String PROPERTY_CHARGE_COIN_PRICE = "charge_coin_price";

    private String id;
    private int price;

    public ChargeCoinEntity(JSONObject object) {
        try {
            if (!object.get(PROPERTY_CHARGE_COIN_ID).equals(null))
                id = object.getString(PROPERTY_CHARGE_COIN_ID);
            if (!object.get(PROPERTY_CHARGE_COIN_PRICE).equals(null))
                price = object.getInt(PROPERTY_CHARGE_COIN_PRICE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ChargeCoinEntity(String id, int price) {
        this.id = id;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
