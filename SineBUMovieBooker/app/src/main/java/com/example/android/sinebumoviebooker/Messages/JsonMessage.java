package com.example.android.sinebumoviebooker.Messages;

/**
 * Created by Wirzourn on 5.12.2017.
 */

public class JsonMessage {
    public String type;
    public String data;
    public JsonMessage(String type, String data){
        this.type = type;
        this.data = data;
    }

    public String getType(){
        return type;
    }
    public String getData(){
        return data;
    }
}
