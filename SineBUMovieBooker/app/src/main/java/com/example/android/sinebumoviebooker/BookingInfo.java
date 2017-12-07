package com.example.android.sinebumoviebooker;

/**
 * Created by Wirzourn on 4.12.2017.
 */

public class BookingInfo {
    public String seatID;
    public String name;
    public String surname;
    public int seatRow;
    public int seatColumn;
    BookingInfo(String seatID, String name, String surname, int seatRow, int seatColumn){
        this.setSeatID(seatID);
        this.setName(name);
        this.setSurname(surname);
        this.setSeatRow(seatRow);
        this.setSeatColumn(seatColumn);
    }

    public String getSeatID() {
        return seatID;
    }

    public void setSeatID(String seatID) {
        this.seatID = seatID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(int seatRow) {
        this.seatRow = seatRow;
    }

    public int getSeatColumn() {
        return seatColumn;
    }

    public void setSeatColumn(int seatColumn) {
        this.seatColumn = seatColumn;
    }
}
