package com.example.dldmd.gtalk.chat;

public class ChatVO {

    public String userUid;
    public String message;
    public String nowTime;

    public ChatVO(String userUid, String message,String nowTime) {
        this.userUid = userUid;
        this.message = message;
        this.nowTime = nowTime;
    }

    public ChatVO(){

    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNowTime() {
        return nowTime;
    }

    public void setNowTime(String nowTime) {
        this.nowTime = nowTime;
    }
}
