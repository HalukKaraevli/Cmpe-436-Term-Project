package com.example.android.sinebumoviebooker.Messages;

/**
 * Created by Wirzourn on 5.12.2017.
 */

public class SendDateMessage {
    String date;
    String type;
    public SendDateMessage(String type, String date){
        this.setDate(date);
        this.setType(type);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
