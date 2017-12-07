package recievedMessages;


import java.util.ArrayList;

import sendedMessages.AvailableMovie;

/**
 * Created by Wirzourn on 5.12.2017.
 */

public class ReleaseSelectedSeatsMessage {
    public String type;
    public ArrayList<int[]> selectedSeats;
    public String date;
    public AvailableMovie selectedMovie;

    public ReleaseSelectedSeatsMessage(String type, ArrayList<int[]> selectedSeats, String date, AvailableMovie selectedMovie){
        this.setType(type);
        this.setselectedSeats(selectedSeats);
        this.setDate(date);
        this.setSelectedMovie(selectedMovie);
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }

    public void setselectedSeats( ArrayList<int[]> selectedSeats){
        this.selectedSeats = selectedSeats;
    }
    public ArrayList<int[]> getSelectedSeats(){
        return this.selectedSeats;

    }
    public String getDate(){
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public AvailableMovie getSelectedMovie() {
        return selectedMovie;
    }

    public void setSelectedMovie(AvailableMovie selectedMovie) {
        this.selectedMovie = selectedMovie;
    }
    }


