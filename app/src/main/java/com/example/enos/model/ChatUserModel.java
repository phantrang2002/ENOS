package com.example.enos.model;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class ChatUserModel {

    private String id, lastMessage, profileImage;
    private List<String> uid;

    @ServerTimestamp
    private Date time;


    public ChatUserModel() {
    }

    public ChatUserModel(String id, String lastMessage,String profileImage, List<String> uid, Date time) {
        this.id = id;
        this.lastMessage = lastMessage;
        this.uid = uid;
        this.time = time;
        this.profileImage = profileImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public List<String> getUid() {
        return uid;
    }

    public void setUid(List<String> uid) {
        this.uid = uid;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}