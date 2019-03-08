package com.example.dldmd.gtalk.chat;

public class ChatroomVO {
    public String userUid;


    public ChatroomVO(String userUid) {
        this.userUid = userUid;

    }

    public ChatroomVO(){

    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }


}
