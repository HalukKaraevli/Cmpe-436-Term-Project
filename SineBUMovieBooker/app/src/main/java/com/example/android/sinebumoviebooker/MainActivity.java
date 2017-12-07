package com.example.android.sinebumoviebooker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sinebumoviebooker.Messages.CheckSelectedSeatsMessage;
import com.example.android.sinebumoviebooker.Messages.GetSelectedMovieSeatsMessage;
import com.example.android.sinebumoviebooker.Messages.RequestStatusMessage;
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

public class MainActivity extends AppCompatActivity {
    private int[][] seatsStatus; // 0 -> Available, 1 -> Taken, 2->Selected
    private ArrayList<AvailableMovie> availableMovies;
    private AvailableMovie selectedMovie;
    private String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent starterIntent = getIntent();
        availableMovies = (ArrayList<AvailableMovie>) starterIntent.getSerializableExtra("availableMovies");
        selectedMovie = (AvailableMovie) starterIntent.getSerializableExtra("selectedMovie");
        seatsStatus = (int[][]) starterIntent.getSerializableExtra("seats");
        date = starterIntent.getStringExtra("date");

        //INITIALIZING DEFAULT STATUS OF THE SEATS AND MOVIE HEADER
        TextView movieName = findViewById(R.id.movieName);
        movieName.setText(selectedMovie.getName());
        TextView movieTime = findViewById(R.id.movieTime);
        movieTime.setText(selectedMovie.getTime());
        TextView movieDate = findViewById(R.id.movieDate);
        movieDate.setText(date);

        LinearLayout cinemaSeats = findViewById(R.id.cinemaSeats);
        int rowsNum = cinemaSeats.getChildCount();
        for(int i=0; i<rowsNum; i++){
            LinearLayout ithRow = (LinearLayout)cinemaSeats.getChildAt(i);
            int seatsNum = ithRow.getChildCount();
            for(int j=0; j<seatsNum; j++) {
                ImageView seat = (ImageView) ithRow.getChildAt(j);
                //PUTTING ROW COLUMN INFO TO IMAGEVIEWS TAG. WILL REQUIRE IN THE EVENT OF IMAGEVIEW
                int[] rowColumn = new int[2];
                rowColumn[0]=i; //Row
                rowColumn[1]=j; //Column
                seat.setTag(rowColumn);
                //ADDING ONCLICKLISTENER IF THE CORRESPONDING SEAT IS NOT TAKEN, IF TAKEN CHANGING ITS IMAGE TO unavailable_seat
                if(seatsStatus[i][j]==1) {
                    seat.setImageResource(R.mipmap.unavailable_seat);
                }else{
                    seat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int[] rowColumn = (int[])view.getTag();
                            if(seatsStatus[rowColumn[0]][rowColumn[1]]==0){
                                seatsStatus[rowColumn[0]][rowColumn[1]] = 2;
                                ((ImageView)view).setImageResource(R.mipmap.selected_seat);
                            }else{
                                seatsStatus[rowColumn[0]][rowColumn[1]] = 0;
                                ((ImageView)view).setImageResource(R.mipmap.available_seat);
                            }
                        }
                    });
                }
            }
        }

        Button bookingButton = findViewById(R.id.bookingButton);
        bookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<int[]> selectedSeats = new ArrayList<int[]>();
                int rows = seatsStatus.length;
                int columns = seatsStatus[0].length;
                for (int i = 0; i < rows; i++){
                    for(int j=0; j< columns; j++){
                        if(seatsStatus[i][j]==2){
                            int[] seat = new int[2];
                            seat[0] = i;
                            seat[1] = j;
                            selectedSeats.add(seat);
                        }
                    }
                }

                if(selectedSeats.size()>0){
                    //CHECK WHETHER SELECTED SEATS ARE TAKEN OR NOT
                    CheckSelectedSeatsMessage m = new CheckSelectedSeatsMessage("SELECTED_SEATS",selectedSeats,date,selectedMovie);
                    Gson gson = new Gson();
                    String message = gson.toJson(m);
                    //INITIALIZE SOCKET WRITER AND READER
                    ServerConnection.getServerConnection().sendToServer(message);

                    //RETRIEVE REQUEST STATUS INFORMATION FOR SELECTED SEATS
                    String jsonStatusInfo = ServerConnection.getServerConnection().recieveFromServer();

                    RequestStatusMessage statusMessage = gson.fromJson(jsonStatusInfo,RequestStatusMessage.class);
                    if(statusMessage.getStatus().equals("OK")){ //IF NOT TAKEN
                        //CONTINUE TO NEXT PAGE
                        Intent intent = new Intent(MainActivity.this,BookingInfoActivity.class);
                        intent.putExtra("availableMovies",availableMovies);
                        intent.putExtra("selectedMovie",selectedMovie);
                        intent.putExtra("selectedSeats",selectedSeats);
                        intent.putExtra("date",date);
                        finish();
                        startActivity(intent);
                    }else{//IF ONE OF THEM IS TAKEN ALREADY
                        Toast.makeText(MainActivity.this,"One of the selected seats has already been taken. Refreshing...",Toast.LENGTH_LONG).show();
                        //SendSelectedMovieMessage TO GET UPDATED SEATS
                        message = gson.toJson(new SendSelectedMovieMessage("SELECTED_MOVIE",selectedMovie,date));
                        ServerConnection.getServerConnection().sendToServer(message);
                        String jsonSeatsInfo ="";
                        jsonSeatsInfo = ServerConnection.getServerConnection().recieveFromServer();

                        //GET UPDATED SEATS AND REFRESH THE ACTIVITY
                        GetSelectedMovieSeatsMessage movieSeatsMessage = gson.fromJson(jsonSeatsInfo,GetSelectedMovieSeatsMessage.class);
                        Intent intent = new Intent(MainActivity.this,MainActivity.class);
                        intent.putExtra("availableMovies",availableMovies);
                        intent.putExtra("selectedMovie",selectedMovie);
                        intent.putExtra("seats",movieSeatsMessage.getSeats());
                        intent.putExtra("date",date);
                        finish();
                        startActivity(intent);
                    }
                }else{
                    Toast.makeText(MainActivity.this,"You need to select at least one seat.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
