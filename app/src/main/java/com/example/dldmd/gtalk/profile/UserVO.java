package com.example.dldmd.gtalk.profile;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserVO implements Serializable {
    String userUid;
    String userNick;
    String userName;
    String userLang;
    String userImg;


    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference setUser = databaseReference.child("USER");
    FirebaseAuth firebaseAuth;
    public UserVO(String userUid, String userNick, String userName,String userLang, String userImg) {
        this.userUid = userUid;
        this.userNick = userNick;
        this.userName = userName;
        this.userLang = userLang;
        this.userImg = userImg;
    }




    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserLang() {
        return userLang;
    }

    public void setUserLang(String userLang) {
        this.userLang = userLang;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }
}
