package com.example.android.sinebumoviebooker;


import java.io.Serializable;

/**
 * Created by Wirzourn on 4.12.2017.
 */

public class AvailableMovie implements Serializable{
    public String name; //Name of the movie
    public String time; //Time of the movie. Should be in the HH:MM form
    public AvailableMovie(String name, String time){
        this.name = name;
        this.time = time;
    }

    public String getName(){
        return name;
    }
    public String getTime(){
        return time;
    }
}
