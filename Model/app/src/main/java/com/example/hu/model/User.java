package com.example.hu.model;

import com.google.gson.JsonElement;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import retrofit.http.GET;
import rx.Observable;

/**
 * Created by hu on 14/10/29.
 */
public class User extends RealmObject{

    private int user_id;
    private String name;
    private String last_login_at;
    private AccessToken access_token;
    private RealmList<Post> posts;

    @Ignore
    private String authorization;

    public static boolean isLogin(){
        return Realm.getInstance(ApplicationRunTime.getAppContext()).where(AccessToken.class).count() != 1 ;
    }

    public String getAuthorization(){
        return this.access_token.getToken() + this.access_token.getKey();
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_login_at() {
        return last_login_at;
    }

    public void setLast_login_at(String last_login_at) {
        this.last_login_at = last_login_at;
    }

    public AccessToken getAccess_token() {
        return access_token;
    }

    public void setAccess_token(AccessToken access_token) {
        this.access_token = access_token;
    }

    public RealmList<Post> getPosts() {
        return posts;
    }

    public void setPosts(RealmList<Post> posts) {
        this.posts = posts;
    }

    public interface Action {
            @GET(("/userWithPost.json"))
            Observable<JsonElement> login();
    }
}
