package com.example.android.sinebumoviebooker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.sinebumoviebooker.Messages.GetSelectedMovieSeatsMessage;
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

public class AvailableMoviesActivity extends AppCompatActivity {
    ArrayList<AvailableMovie> availableMovies;
    AvailableMovieAdapter adapter;
    String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_movies);
        Intent starterIntent = getIntent();
        availableMovies = (ArrayList<AvailableMovie>)starterIntent.getSerializableExtra("availableMovies");
        String s = availableMovies.get(0).getName();
        date = starterIntent.getStringExtra("date");
        adapter = new AvailableMovieAdapter(this,availableMovies);
        ListView lv = (ListView) findViewById(R.id.availableMovieList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = (Integer) view.getTag();
                AvailableMovie clickedMovie = (AvailableMovie) adapter.getItem(position);
                String s = clickedMovie.getName();
                //MESSAGE INITIALIZATION
                SendSelectedMovieMessage m = new SendSelectedMovieMessage("SELECTED_MOVIE",clickedMovie,date);
                Gson gson = new Gson();
                String message = gson.toJson(m);

                //SEND SELECTED MOVIE INFORMATION TO THE SERVER
               ServerConnection.getServerConnection().sendToServer(message);

                //RETRIEVE SEAT INFORMATION FOR SELECTED MOVIE
                String jsonSeatInfo= "";
                jsonSeatInfo = ServerConnection.getServerConnection().recieveFromServer();

                //OBJECT INITIALIZATION
                GetSelectedMovieSeatsMessage selectedMovieSeatsMessage = gson.fromJson(jsonSeatInfo,GetSelectedMovieSeatsMessage.class);
                int[][] seats = selectedMovieSeatsMessage.seats;

                //CREATE INTENT TO GO MainActivity (Seat Selection)
                Intent intent = new Intent(AvailableMoviesActivity.this,MainActivity.class);
                intent.putExtra("availableMovies",availableMovies);
                intent.putExtra("seats",seats);
                intent.putExtra("date",date);
                intent.putExtra("selectedMovie",clickedMovie);
                startActivity(intent);
            }
        });
    }
}
