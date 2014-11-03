package com.example.hu.model;

import io.realm.RealmObject;

/**
 * Created by hu on 14/10/31.
 */
public class AccessToken extends RealmObject {
    private String token;
    private String key;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
