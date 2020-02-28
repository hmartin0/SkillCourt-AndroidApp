package com.skillcourt.structures;

import android.util.Log;

import com.skillcourt.services.ConnectionService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Joshua Mclendon on 2/2/18.
 */

public class Pad {

    private static final String TAG = Pad.class.getName();
    private Socket mSocket;
    private BufferedReader mInput;
    private OutputStreamWriter mOutput;
    private String uuid;
    private String hitColor = "";
    private boolean alive = true;
    private ConnectionService mConnectionService;
    private int CurrentStatus;
    private int Order;
    private Thread main;
    private AtomicInteger confirmTimer = new AtomicInteger(1);
    private AtomicInteger game_started = new AtomicInteger(1);
    private AtomicInteger off_confirmed = new AtomicInteger(0);

    //Timer variable
    //ReplyFlag

    public Pad(){

    }
    public Pad(Socket socket, ConnectionService connectionService) {
        mSocket = socket;
        mConnectionService = connectionService;
        game_started.set(0);
    }

    public enum COMMANDS {
        ALIVE("-1"),
        CONNECT("1"),
        START_GAME("2"),
        LIGHT_UP("3"),
        LIGHT_OFF("4"),
        END_GAME("5");

        private String comm;

        COMMANDS(String comm) {
            this.comm = comm;
        }

        public String getCommand() {
            return comm;
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    public String getUuid() {
        return uuid;
    }

    public int getCurrentStatus() {
        return CurrentStatus;
    }

    public int getOrder() {
        return Order;
    }

    public void setOrder(int orderToSet) {
        this.Order = orderToSet;
    }

    public void connect() {
        Log.i(TAG, "calling connect");
        Log.i(TAG, "getting buffer reader " + mSocket.isConnected());
        try {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        try {
                            confirmTimer.set(confirmTimer.get() + 1);
                            Thread.sleep(250);
                            Log.i(TAG, "confirmTimerconfirmTimerconfirmTimerconfirmTimer confirmTimer    " + confirmTimer + " game_started.get() " + game_started.get() + "Pad: " +Pad.this.getUuid());
                            Log.i(TAG, "\n2 seconds without signal xxxxxxxxxxxxxxxxxxxxxxxxxxxx off_confirmed: " + off_confirmed.get());
                            if (confirmTimer.get() > 4) {

                                Log.i(TAG, "2 seconds without signal xxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                                if (game_started.get() == 10) {
                                    mConnectionService.broadcast(Pad.this, "PAD_HIT");
                                    // Thread.currentThread().interrupt();
                                }
                            }
                        } catch (Exception e) {
                            Log.i(TAG, "Exception here-------------------+++++++++++++++++++");
                            shutDown();
                        }
                    }

                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                        mOutput = new OutputStreamWriter(mSocket.getOutputStream(), "UTF-8");
                        sendMessage(COMMANDS.CONNECT.getCommand());
                        Log.i(TAG, "starting thread connect " + mSocket.isConnected());
                        //while(mSocket.isConnected() && replyFlag)git
                        while (!mSocket.isClosed() || !mSocket.isOutputShutdown()) {
                            String data = "";



                            try{
                                data = mInput.readLine();

                                confirmTimer.set(0);
                                // sendMessage("8");
                                // Thread.sleep(50);
                                // sendMessage("8");
                                // Thread.sleep(50);

                            }catch (Exception e){
                                Log.i(TAG, "The 2 writes failed Pad.java");
                                data = "3";
                                shutDown();
                                break;
                            }


                            //String confirmed = "";

                            if (data.length() > 1) {
                                uuid = data;
                                Log.i(TAG, "UUID received " + uuid);
                            }
                            if (data.equalsIgnoreCase("-1")) {
                                alive = true;
                            } else if (data.equalsIgnoreCase("0")) {
                                Log.i(TAG, "Shutdown");
                                shutDown();
                                Thread.currentThread().interrupt();
                            } else if (data.equalsIgnoreCase("1")) {

                                Log.i(TAG, "Connected received");
                                mConnectionService.broadcast(Pad.this, "PAD_CONNECTION_CHANGE");
                                //handshakePollingService();
                            } else if (data.equalsIgnoreCase("9")) {

                                off_confirmed.set(1);
                                Log.i(TAG, "Confirmation  received 999999999999999999999999999999999999999999");


                                //mConnectionService.broadcast(Pad.this, "PAD_HIT");
                            } else if (data.equalsIgnoreCase("3") || data.equalsIgnoreCase(hitColor)) {
                                confirmTimer.set(0);
                                game_started.set(10);


                                Log.i(TAG, "Hit received");
                                //Send confirmation back and and wait for receipt
                                //Thread.sleep(1000);
                                //sendMessage("8");
                                //receive confirmation


                                mConnectionService.broadcast(Pad.this, "PAD_HIT");
                            }
                            //  Log.i(TAG + " Input Thread", "Received: " + data);


                        }
                        //mSocket.close();
                        Log.i(TAG, "Outside of the while loop in the Pad.java");
                    } catch (Exception e) {
                        Log.i(TAG, "interrupt thread from Pad");
                        e.printStackTrace();
                        shutDown();
                        Thread.currentThread().interrupt();
                    }
                }

            }).start();
        } catch (Exception e) {
            Log.i(TAG, "Couldn't start thread Pad.java");
        }
    }

    public void handshakePollingService() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Start Timer var

                while (alive) {
                    sendMessage(COMMANDS.ALIVE.getCommand());
                    long startTime = System.currentTimeMillis();
                    alive = false;
                    while (10 * 1000 < System.currentTimeMillis() - startTime) {

                    }

                }
                shutDown();
                Thread.currentThread().interrupt();

            }

        }).start();

    }

    private void sendMessage(String message) {
        try {
            if (mSocket.isConnected()) {
                Log.i(TAG, "sending message: " + message);
                mOutput.write(message, 0, message.length());
                //mOutput.write(message, 0, message.length());
                mOutput.flush();

            }
        } catch (Exception e) {
            Log.i(TAG, "send message error");
            e.printStackTrace();
        }
    }

    public void shutDown() {
        try {
            Log.i(TAG, "Shutting down thread because pad failed Pad.java");
            if (mSocket.isConnected()) {
                mInput.close();
                mOutput.close();
                mSocket.close();

            }

        } catch (Exception e) {
            Log.i(TAG, "shut down error");
            e.printStackTrace();
        }

        if (mConnectionService.getActivePads().contains(Pad.this)) {
            mConnectionService.removeActivePad(Pad.this);
        }

    }

    public void turnOn() {
        sendMessage(COMMANDS.LIGHT_UP.getCommand());
    }

    public void turnOn(String color) {
        sendMessage(color);
    }

    public void turnOff() {
        sendMessage(COMMANDS.LIGHT_OFF.getCommand());
      }
    public boolean get_off_confirmed(){
        if (off_confirmed.get() == 1)
            return true;
        else
            return false;
    }
    public void set_off_confirmed(){
        off_confirmed.set(0);
    }

    public void startGame() {
        sendMessage(COMMANDS.START_GAME.getCommand());
    }

    public void endGame() {
        sendMessage(COMMANDS.END_GAME.getCommand());
    }
}