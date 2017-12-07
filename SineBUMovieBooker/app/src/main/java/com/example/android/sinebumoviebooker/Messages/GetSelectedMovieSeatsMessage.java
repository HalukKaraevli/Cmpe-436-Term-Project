package com.example.android.sinebumoviebooker.Messages;

/**
 * Created by Wirzourn on 5.12.2017.
 */

public class GetSelectedMovieSeatsMessage {
    public String type;
    public int[][] seats;

    public GetSelectedMovieSeatsMessage(String type, int[][] seats){
        this.setType(type);
        this.setSeats(seats);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int[][] getSeats() {
        return seats;
    }

    public void setSeats(int[][] seats) {
        this.seats = seats;
    }
}
