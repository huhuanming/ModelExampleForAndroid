package com.example.hu.model;

import io.realm.RealmObject;

/**
 * Created by hu on 14/11/3.
 */
public class Comment extends RealmObject{
    private String context;

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
