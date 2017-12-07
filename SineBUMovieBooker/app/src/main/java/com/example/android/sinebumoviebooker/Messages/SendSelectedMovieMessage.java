package com.example.android.sinebumoviebooker.Messages;

import com.example.android.sinebumoviebooker.AvailableMovie;

/**
 * Created by Wirzourn on 5.12.2017.
 */

public class SendSelectedMovieMessage {
    public String type;
    public AvailableMovie selectedMovie;
    public String date;

    public SendSelectedMovieMessage(String type, AvailableMovie selectedMovie, String date){
        this.setType(type);
        this.setSelectedMovie(selectedMovie);
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AvailableMovie getSelectedMovie() {
        return selectedMovie;
    }

    public void setSelectedMovie(AvailableMovie selectedMovie) {
        this.selectedMovie = selectedMovie;
    }
    public String getDate(){
        return this.date;
    }
}
