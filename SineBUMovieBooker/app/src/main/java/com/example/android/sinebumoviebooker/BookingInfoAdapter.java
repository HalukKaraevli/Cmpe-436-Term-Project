package com.example.android.sinebumoviebooker;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.app.Activity;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
/**
 * Created by Wirzourn on 4.12.2017.
 */

public class BookingInfoAdapter extends ArrayAdapter{
    public ArrayList<BookingInfo> bookingInfos;
    public BookingInfoAdapter(Activity context,ArrayList<BookingInfo> bookingInfos){
        super(context,0,bookingInfos);
        this.bookingInfos = bookingInfos;
        //this.context = context;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.booking_info_cell_layout,parent,false);
        }
        listItemView.setTag(position);
        final BookingInfo currentInfo = (BookingInfo)getItem(position);
        TextView seatView = listItemView.findViewById(R.id.SeatId);
        seatView.setText(currentInfo.getSeatID());

        EditText name = listItemView.findViewById(R.id.editTextName);
        name.setTag(position);
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    final int position = (int)view.getTag();
                    BookingInfo bookingInfo = bookingInfos.get(position);
                    bookingInfo.setName(((EditText)view).getText().toString());
                }
            }
        });

        EditText surname = listItemView.findViewById(R.id.editTextSurname);
        surname.setTag(position);
        surname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    final int position = (int)view.getTag();

                    BookingInfo bookingInfo = bookingInfos.get(position);
                    bookingInfo.setSurname(((EditText)view).getText().toString());
                }
            }
        });
        return listItemView;
    }

    public ArrayList<BookingInfo> getBookingInfos(){
        return bookingInfos;
    }
}
