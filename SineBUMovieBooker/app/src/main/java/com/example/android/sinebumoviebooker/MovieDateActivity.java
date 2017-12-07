package com.example.android.sinebumoviebooker;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.android.sinebumoviebooker.Messages.AvailableMoviesMessage;
import com.example.android.sinebumoviebooker.Messages.SendDateMessage;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MovieDateActivity extends AppCompatActivity {
    private String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_date);
        ServerConnection.getServerConnection().setConnectionSocket();
        Button button = findViewById(R.id.dateSenderButton);
        CalendarView datePicker = findViewById(R.id.DatePicker);
        datePicker.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                date = "";
                if(dayOfMonth < 10){
                    date = "0";
                }
                date = date + dayOfMonth + "-";
                if(month+1 < 10){
                    date = date + 0;
                }
                date = date + (month+1) + "-" + year;
            }
        });
        //SET DEFAULT DATE
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(datePicker.getDate());
        String dateString = dateFormat.format(date);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendDateMessage dm = new SendDateMessage("DATE", MovieDateActivity.this.date);
                Gson gson = new Gson();
                String dateMessage = gson.toJson(dm);

                //SEND DATE INFORMATION TO THE SERVER
                ServerConnection.getServerConnection().sendToServer(dateMessage);

                //RETRIEVE MOVIE INFORMATION ON THAT DATE
                String jsonMovieInfos = null;
                jsonMovieInfos = ServerConnection.getServerConnection().recieveFromServer();


                //GSON PART - JSON STRING SHOULD BE ABLE TO DESERIALIZE INTO THE FORMAT OF ArrayList<AvailableMovie>
                AvailableMoviesMessage availableMoviesMessage = gson.fromJson(jsonMovieInfos,AvailableMoviesMessage.class);
                ArrayList<AvailableMovie> availableMovies = availableMoviesMessage.getAvailableMovies();

                if(availableMovies.size()>0){
                    Intent i = new Intent(MovieDateActivity.this,AvailableMoviesActivity.class);
                    i.putExtra("availableMovies", availableMovies);
                    i.putExtra("date",MovieDateActivity.this.date);
                    startActivity(i);
                }else{
                    Toast.makeText(MovieDateActivity.this,"There isn't any movie on that day. Please select another day.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


