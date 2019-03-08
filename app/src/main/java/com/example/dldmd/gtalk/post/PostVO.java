package com.example.dldmd.gtalk.post;


import com.example.dldmd.gtalk.profile.UserVO;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class PostVO {
    String userUid;
    String userName;
    String userImg;
    String postContents;
    String postContentsId;
    String postContentsImg;
    String nowTime;



    public PostVO(String userUid, String userName, String userImg, String postContents, String postContentsId,String postContentsImg, String nowTime) {
        this.userUid = userUid;
        this.userName = userName;
        this.userImg = userImg;
        this.postContents = postContents;
        this.postContentsId = postContentsId;
        this.postContentsImg = postContentsImg;
        this.nowTime = nowTime;
    }

    public PostVO(String userUid, String userName, String userImg, String postContents, String postContentsId, String nowTime){
        this.userUid = userUid;
        this.userName = userName;
        this.userImg = userImg;
        this.postContents = postContents;
        this.postContentsId = postContentsId;
        this.nowTime = nowTime;
    }

    public PostVO(){

    }

    public String getPostContentsImg() {
        return postContentsImg;
    }

    public void setPostContentsImg(String postContentsImg) {
        this.postContentsImg = postContentsImg;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getPostContents() {
        return postContents;
    }

    public void setPostContents(String postContents) {
        this.postContents = postContents;
    }

    public String getPostContentsId() {
        return postContentsId;
    }

    public void setPostContentsId(String postContentsId) {
        this.postContentsId = postContentsId;
    }

    public String getNowTime() {
        return nowTime;
    }

    public void setNowTime(String nowTime) {
        this.nowTime = nowTime;
    }
}
