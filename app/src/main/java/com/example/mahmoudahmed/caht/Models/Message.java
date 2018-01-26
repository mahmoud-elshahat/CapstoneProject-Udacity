package com.example.mahmoudahmed.caht.Models;

/**
 * Created by PC on 1/5/2017.
 */

public class Message {
    public String date;
    public boolean fromSender;
    private String content;
    private String reciver;
    private String sender;
    private boolean read;

    public Message(String content, String reciver, String sender, String date)

    {
        fromSender = true;
        this.content = content;
        this.reciver = reciver;
        this.sender = sender;
        this.date = date;
        this.read = false;
    }

    public Message() {
        fromSender = true;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReciver() {
        return reciver;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
