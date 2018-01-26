package com.example.mahmoudahmed.caht.Models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class Client implements Parcelable {
    public static final Creator<Client> CREATOR = new Creator<Client>() {
        @Override
        public Client createFromParcel(Parcel source) {
            return new Client(source);
        }

        @Override
        public Client[] newArray(int size) {
            return new Client[size];
        }
    };
    public String id;
    public String email;
    public String username;
    private ArrayList<String> sentRequests;
    private ArrayList<String> receivedRequests;
    private ArrayList<String> friendsIds;
    private ArrayList<String> channelsIds;

    public Client(Parcel input) {
        id = input.readString();
        email = input.readString();
        username = input.readString();
        sentRequests = input.readArrayList(null);
        receivedRequests = input.readArrayList(null);
        friendsIds = input.readArrayList(null);
        channelsIds = input.readArrayList(null);
    }

    public Client() {
        super();
    }

    public Client(String id, String email, String displayName) {
        this.id = id;
        this.email = email;
        this.username = displayName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(email);
        parcel.writeString(username);
        parcel.writeList(sentRequests);
        parcel.writeList(receivedRequests);
        parcel.writeList(friendsIds);
        parcel.writeList(channelsIds);
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getReceivedRequests() {
        return receivedRequests;
    }

    public void setReceivedRequests(ArrayList<String> receivedRequests) {
        this.receivedRequests = receivedRequests;
    }

    public ArrayList<String> getSentRequests() {
        return sentRequests;
    }

    public void setSentRequests(ArrayList<String> sentRequests) {
        this.sentRequests = sentRequests;
    }

    public ArrayList<String> getFriendsIds() {
        return friendsIds;
    }

    public void setFriendsIds(ArrayList<String> friendsIds) {
        this.friendsIds = friendsIds;
    }

    public ArrayList<String> getChannelsIds() {
        return channelsIds;
    }

    public void setChannelsIds(ArrayList<String> channelsIds) {
        this.channelsIds = channelsIds;
    }

}
