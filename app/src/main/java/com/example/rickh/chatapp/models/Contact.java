package com.example.rickh.chatapp.models;

import java.io.Serializable;

public class Contact implements Serializable {
    private int id;
    private String userUid;
    private String mContactProfilePicture;
    private String mContactName;
    private boolean isImageChanged;

    public Contact(int id, String userUid, String profilePicture, String name) {
        this.id = id;
        this.userUid = userUid;
        this.mContactProfilePicture = profilePicture;
        this.mContactName = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getmContactProfilePicture() {
        return mContactProfilePicture;
    }

    public String getmContactName() {
        return mContactName;
    }

    public void setmContactProfilePicture(String mContactProfilePicture) {
        this.mContactProfilePicture = mContactProfilePicture;
    }

    public void setmContactName(String mContactName) {
        this.mContactName = mContactName;
    }

    public boolean isImageChanged() {
        return isImageChanged;
    }

    public void setImageChanged(boolean imageChanged) {
        isImageChanged = imageChanged;
    }
}
