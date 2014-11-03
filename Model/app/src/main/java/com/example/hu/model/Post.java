package com.example.hu.model;

import io.realm.RealmObject;

/**
 * Created by hu on 14/11/3.
 */
public class Post extends RealmObject{
    private String title;
    private Comment comment;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
