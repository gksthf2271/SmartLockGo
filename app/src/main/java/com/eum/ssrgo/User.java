package com.eum.ssrgo;

import com.google.firebase.database.IgnoreExtraProperties;

//파이어베이스 유저 모델
@IgnoreExtraProperties
public class User {

    public String username;
    public String email;

    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

}
// [END blog_user_class]
