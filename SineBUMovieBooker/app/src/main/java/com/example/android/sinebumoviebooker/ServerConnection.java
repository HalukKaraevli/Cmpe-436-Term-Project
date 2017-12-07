package com.example.android.sinebumoviebooker;

import android.os.AsyncTask;
import android.os.Build;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;


/**
 * Created by Wirzourn on 4.12.2017.
 */

public class ServerConnection {
    private static  ServerConnection serverConnection = null;
    private static final String SERVER_IP = "18.216.53.253"; //REQUIRES IP ADRESS
    private static final int PORT_NUM = 2020;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ServerConnection(){

    }

    public static ServerConnection getServerConnection(){
        if(serverConnection == null){
            serverConnection = new ServerConnection();
        }
        return serverConnection;
    }

    public synchronized void setConnectionSocket(){
        new Thread(new serverConnectionThread()).start();
        try {
            wait();
        } catch (InterruptedException e) {
        }
    }
    public synchronized void continueInitializing(){
        notify();
    }
    public Socket getConnectionSocket(){
        return socket;
    }

    private class serverConnectionThread implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress a = InetAddress.getByName("0.tcp.ngrok.io");
                socket = new Socket(a, 10744);
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)), true);
                    } else {
                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8")), true);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                    }else{
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
                    }
                }catch(IOException e1){
                    e1.printStackTrace();
                }
                getServerConnection().continueInitializing();
            }catch(UnknownHostException e1){
                e1.printStackTrace();
            }catch(IOException e1){
                e1.printStackTrace();
            }
        }
    }
    public void sendToServer(String s){
        new AsyncTaskServerSend().execute(s);
    }

    public String recieveFromServer(){
        String response = null;
        try {
            response = new AsyncTaskServerReceive().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return response;
    }

    private class AsyncTaskServerSend extends AsyncTask<String, String, String> {
        @Override
        public String doInBackground(String... strings) {
            out.println(strings[0]);
            return "";
        }
    }

    private class AsyncTaskServerReceive extends AsyncTask<String, String, String> {
        @Override
        public String doInBackground(String... strings) {
            String message = null;
            try {
                message = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return message;
        }
    }
}
