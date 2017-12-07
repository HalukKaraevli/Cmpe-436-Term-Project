package com.example.android.sinebumoviebooker.Messages;

import com.example.android.sinebumoviebooker.AvailableMovie;
import com.example.android.sinebumoviebooker.BookingInfo;

import java.util.ArrayList;

/**
 * Created by Wirzourn on 5.12.2017.
 */

public class SendBookingInfoMessage {
    public String type;
    public ArrayList<BookingInfo> bookingInfos;
    public String date;
    public AvailableMovie selectedMovie;

    public SendBookingInfoMessage(String type, ArrayList<BookingInfo> bookingInfos, String date, AvailableMovie selectedMovie){
        this.setType(type);
        this.setBookingInfos(bookingInfos);
        this.setDate(date);
        this.setSelectedMovie(selectedMovie);
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<BookingInfo> getBookingInfos() {
        return bookingInfos;
    }

    public void setBookingInfos(ArrayList<BookingInfo> bookingInfos) {
        this.bookingInfos = bookingInfos;
    }

    public String getDate() {
        return date;
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
