package com.example.android.sinebumoviebooker;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

/**
 * Created by Wirzourn on 4.12.2017.
 */

public class AvailableMovieAdapter extends ArrayAdapter {
    public AvailableMovieAdapter(Activity context, ArrayList<AvailableMovie> availableMovies){
        super(context,0,availableMovies);
        //this.context = context;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.available_movie_cell_layout,parent,false);
        }

        AvailableMovie currentMovie = (AvailableMovie)getItem(position);
        TextView movieName = (TextView) listItemView.findViewById(R.id.movieName);
        movieName.setText(currentMovie.getName()); // Set name of the movie in visual
        TextView movieTime = (TextView) listItemView.findViewById(R.id.movieTime);
        movieTime.setText(currentMovie.getTime()); // Set time of the movie in visual

        //IMPLEMENTING ON CLICK LISTENER FOR EVERY ELEMENT THAT WILL RESULT IN CHANGING ACTIVITY IF DONE CORRECTLY
        listItemView.setTag(position);
        /*
        EditText name = (EditText) listItemView.findViewById(R.id.editTextName);
        EditText surname = (EditText) listItemView.findViewById(R.id.editTextSurname);
        */
        return listItemView;
    }
}
