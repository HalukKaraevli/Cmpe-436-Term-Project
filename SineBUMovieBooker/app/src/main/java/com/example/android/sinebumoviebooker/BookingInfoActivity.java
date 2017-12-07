package com.example.android.sinebumoviebooker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.sinebumoviebooker.Messages.GetSelectedMovieSeatsMessage;
import com.example.android.sinebumoviebooker.Messages.ReleaseSelectedSeatsMessage;
import com.example.android.sinebumoviebooker.Messages.RequestStatusMessage;
import com.example.android.sinebumoviebooker.Messages.SendBookingInfoMessage;
import com.example.android.sinebumoviebooker.Messages.SendSelectedMovieMessage;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class BookingInfoActivity extends AppCompatActivity {
    private Socket connectionSocket;
    private ArrayList<AvailableMovie> availableMovies;
    private AvailableMovie selectedMovie;
    private ArrayList<int[]> selectedSeats;
    private ArrayList<BookingInfo> bookingInfos;
    private String date;
    private BookingInfoAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_info);
        bookingInfos = new ArrayList<BookingInfo>();
        Intent starterIntent = getIntent();
        availableMovies = (ArrayList<AvailableMovie>) starterIntent.getSerializableExtra("availableMovies");
        selectedMovie = (AvailableMovie) starterIntent.getSerializableExtra("selectedMovie");
        date = starterIntent.getStringExtra("date");
        selectedSeats = (ArrayList<int[]>) starterIntent.getSerializableExtra("selectedSeats");
        for(int[] seat : selectedSeats){
            bookingInfos.add(new BookingInfo("Seat: "+(seat[0]+1)+"-"+(seat[1]+1),"","", seat[0], seat[1]));
        }
        adapter = new BookingInfoAdapter(BookingInfoActivity.this,bookingInfos);
        ListView lv = findViewById(R.id.bookingInfoList);
        lv.setAdapter(adapter);

        Button bookIt = findViewById(R.id.bookIt);
        bookIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<BookingInfo> infos = adapter.getBookingInfos();
                for(BookingInfo info: infos){
                    if(info.getName().equals("") || info.getSurname().equals("")){
                        Toast.makeText(BookingInfoActivity.this,"You need to fill every name and surname fields.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //EVERY NAME AND SURNAME IS FILLED AT THIS POINT
                //READY MESSAGE
                SendBookingInfoMessage message = new SendBookingInfoMessage("BOOKING_INFOS",bookingInfos,date,selectedMovie);
                Gson gson = new Gson();
                String msg = gson.toJson(message);
                //SEND MESSAGE TO SERVER
                ServerConnection.getServerConnection().sendToServer(msg);

                //RETRIEVE REQUEST STATUS INFORMATION
                String jsonStatusInfo= ServerConnection.getServerConnection().recieveFromServer();

                RequestStatusMessage statusMessage = gson.fromJson(jsonStatusInfo,RequestStatusMessage.class);
                if(statusMessage.getStatus().equals("OK")){
                    //SEND TO MOVIE DATE ACTIVITY AND PRINT YOU HAVE SUCCESSFULY BOOKED YOUR SEATS
                    Toast.makeText(BookingInfoActivity.this,"You have succesfully booked all requested seats.",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(BookingInfoActivity.this,MovieDateActivity.class);
                    startActivity(i);
                }
            }
        });

        Button releaseIt = findViewById(R.id.ReleaseIt);
        releaseIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //READY MESSAGE
                ReleaseSelectedSeatsMessage message = new ReleaseSelectedSeatsMessage("RELEASE_SEATS",selectedSeats,date,selectedMovie);
                Gson gson = new Gson();
                String msg = gson.toJson(message);
                //SEND MESSAGE TO SERVER
                ServerConnection.getServerConnection().sendToServer(msg);

                //RETRIEVE REQUEST STATUS INFORMATION
                String jsonStatusInfo= ServerConnection.getServerConnection().recieveFromServer();
                RequestStatusMessage statusMessage = gson.fromJson(jsonStatusInfo,RequestStatusMessage.class);

                if(statusMessage.getStatus().equals("OK")){ //GET UPDATED SEAT INFO
                    SendSelectedMovieMessage m = new SendSelectedMovieMessage("SELECTED_MOVIE",selectedMovie,date);
                    String updateSeatsMessage = gson.toJson(m);
                    //SEND TO SERVER
                    ServerConnection.getServerConnection().sendToServer(updateSeatsMessage);
                    //RECIEVE SEATS INFO FROM SERVER
                    String updatedSeatsInfo = ServerConnection.getServerConnection().recieveFromServer();
                    GetSelectedMovieSeatsMessage updSeat = gson.fromJson(updatedSeatsInfo, GetSelectedMovieSeatsMessage.class);
                    int[][] seats = updSeat.getSeats();

                    //GO BACK TO MOVIE SEAT SELECTION
                    Intent intent = new Intent(BookingInfoActivity.this,MainActivity.class);
                    intent.putExtra("availableMovies",availableMovies);
                    intent.putExtra("seats",seats);
                    intent.putExtra("date",date);
                    intent.putExtra("selectedMovie",selectedMovie);
                    finish();
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        ReleaseSelectedSeatsMessage message = new ReleaseSelectedSeatsMessage("RELEASE_SEATS",selectedSeats,date,selectedMovie);
        Gson gson = new Gson();
        String msg = gson.toJson(message);
        //SEND MESSAGE TO SERVER
        ServerConnection.getServerConnection().sendToServer(msg);

        //RETRIEVE REQUEST STATUS INFORMATION
        String jsonStatusInfo= ServerConnection.getServerConnection().recieveFromServer();
        RequestStatusMessage statusMessage = gson.fromJson(jsonStatusInfo,RequestStatusMessage.class);

        if(statusMessage.getStatus().equals("OK")){ //GET UPDATED SEAT INFO
            SendSelectedMovieMessage m = new SendSelectedMovieMessage("SELECTED_MOVIE",selectedMovie,date);
            String updateSeatsMessage = gson.toJson(m);
            //SEND TO SERVER
            ServerConnection.getServerConnection().sendToServer(updateSeatsMessage);
            //RECIEVE SEATS INFO FROM SERVER
            String updatedSeatsInfo = ServerConnection.getServerConnection().recieveFromServer();
            GetSelectedMovieSeatsMessage updSeat = gson.fromJson(updatedSeatsInfo, GetSelectedMovieSeatsMessage.class);
            int[][] seats = updSeat.getSeats();

            //GO BACK TO MOVIE SEAT SELECTION
            Intent intent = new Intent(BookingInfoActivity.this,MainActivity.class);
            intent.putExtra("availableMovies",availableMovies);
            intent.putExtra("seats",seats);
            intent.putExtra("date",date);
            intent.putExtra("selectedMovie",selectedMovie);
            finish();
            startActivity(intent);
        }
    }
}
