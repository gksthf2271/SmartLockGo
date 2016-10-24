package com.eum.ssrgo;

import com.google.firebase.database.IgnoreExtraProperties;

//파이어베이스 유저 모델
@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String userId;

    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User(String username, String email, String userId) {
        this.username = username;
        this.email = email;
        this.userId = userId;
    }
}
// [END blog_user_class]
