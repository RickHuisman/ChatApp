package com.example.rickh.chatapp.models;

public class User {

    private String mUserProfilePicture;
    private String mUserName;
    private String userUid;
    private boolean isFriend;
    private boolean sendFriendRequest;
    private boolean receivedFriendRequest;

    public User(String profilePicture,
                String name,
                String userUid,
                Boolean isFriend,
                Boolean sendFriendRequest,
                Boolean receivedFriendRequest) {
        this.mUserProfilePicture = profilePicture;
        this.mUserName = name;
        this.userUid = userUid;
        this.isFriend = isFriend;
        this.sendFriendRequest = sendFriendRequest;
        this.receivedFriendRequest = receivedFriendRequest;
    }

    public String getmUserProfilePicture() {
        return mUserProfilePicture;
    }

    public void setmUserProfilePicture(String mUserProfilePicture) {
        this.mUserProfilePicture = mUserProfilePicture;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public boolean isSendFriendRequest() {
        return sendFriendRequest;
    }

    public void setSendFriendRequest(boolean sendFriendRequest) {
        this.sendFriendRequest = sendFriendRequest;
    }

    public boolean isReceivedFriendRequest() {
        return receivedFriendRequest;
    }

    public void setReceivedFriendRequest(boolean receivedFriendRequest) {
        this.receivedFriendRequest = receivedFriendRequest;
    }
}
