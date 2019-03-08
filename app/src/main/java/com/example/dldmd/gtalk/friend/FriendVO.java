package com.example.dldmd.gtalk.friend;

import com.example.dldmd.gtalk.profile.UserVO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FriendVO {

    private String friendUid;
    private String friendName;
    private String friendImg;


    public FriendVO(String friendUid, String friendName, String friendImg) {
        this.friendUid = friendUid;
        this.friendName = friendName;
        this.friendImg = friendImg;
    }


    public FriendVO(){

    }

    public String getFriendUid() {
        return friendUid;
    }

    public void setFriendUid(String friendUid) {
        this.friendUid = friendUid;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendImg() {
        return friendImg;
    }

    public void setFriendImg(String friendImg) {
        this.friendImg = friendImg;
    }
}
