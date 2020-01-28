package com.skillcourt.structures;

import android.util.Log;


/**
 * Created by Joshua Mclendon on 1/29/18.
 * Edited by Brandon Suarez on 12/1/19
 */
public class Game extends Thread {
    private static final String TAG = Game.class.getName();
    private boolean isHit = false;
    private long gameTime;
    private boolean byHits = false;
    private int padLightUpTime, padLightUpTimeDelay;
    private Sequence sequence;

    public Game(Sequence sequence, long gameTime, int padLightUpTime, int padLightUpTimeDelay) {
        this.gameTime = gameTime;
        this.padLightUpTime = padLightUpTime;
        this.padLightUpTimeDelay = padLightUpTimeDelay;
        this.sequence = sequence;
    }


    public void setIsHit(boolean isHit) {
        this.isHit = isHit;
    }

    public void setByHits(boolean byHits) {
        this.byHits = byHits;
    }

    public Sequence getSequence() {

        return sequence;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        for (Pad pad : sequence.getPads()) {
            //sequence.getCurrentLitPad().turnOff();     //Once nessesary to end the game//
            //sequence.getCurrentLitPad().endGame();
            pad.turnOff();
            pad.endGame();
            Log.i(TAG, "Sending game over");
        }
    }

    @Override
    public void run() {
        for (Pad pad : sequence.getPads()) {
            pad.startGame(); //send a game is starting so start sensor reading//
        }
        long startTime = System.currentTimeMillis();
        if (byHits) {
            while (!Thread.interrupted()) {
                playGame(startTime);
            }
            Log.i(TAG, "Sending run interrupt");
            Thread.currentThread().interrupt();      //Interupt after the end of byHits game mode//
        } else {
            while (gameTime > System.currentTimeMillis() - startTime && !Thread.interrupted()) {
                playGame(startTime);
            }
        }
    }

    private void playGame(long startTime) {
        isHit = false;
        sequence.next();
        //if the player specified a time that the pad stays lit
        if (padLightUpTime != 0) {
            long startLightUpTime = System.currentTimeMillis();
            //basically loop for the amount of time in seconds (ex. 5 secs) and if the pad isn't hit
            while (padLightUpTime > System.currentTimeMillis() - startLightUpTime && !isHit) {
                //if the time of the game runs out then the game is over
                //so interrupt if it hasn't already been interrupted
                if (!(gameTime > System.currentTimeMillis() - startTime) && !Thread.currentThread().isInterrupted() && !byHits) {
                        Thread.currentThread().interrupt();
                        Log.i(TAG, "Sending game interrupt 1");  //Log to track what condition ended the game//
                    }
            }
            //if there was a light up time and it expired before the pad was hit then turn it off
            //since the pad only turns off if hit or when we tell it too
            if (!isHit) {
                sequence.getCurrentLitPad().turnOff();
                for (Player player : getSequence().getPlayers()) {   //Added to keep track of Missed hits//
                    player.addMiss();
                    player.removePoints();
                }
            }
        } else {
            //loop until a hit is registered or the game is over
            while (!isHit) {
                if (!(gameTime > System.currentTimeMillis() - startTime) && !Thread.currentThread().isInterrupted() && !byHits) {
                    Thread.currentThread().interrupt();
                    Log.i(TAG, "Sending game interrupt 2");  //Log to track what condition ended the game//
                }
            }

        }
        //if we are in memory game mode then also turn off the pad that is next to be lit
        if (sequence.getNextLitPad() != null) {
            sequence.getNextLitPad().turnOff();
        }
        //if there is a delay time then loop for the amount of time in seconds (ex. 2 secs)
        if (padLightUpTimeDelay != 0) {
            long startLightUpDelayTime = System.currentTimeMillis();
            while (padLightUpTimeDelay > System.currentTimeMillis() - startLightUpDelayTime) {
                //if the time of the game runs out then the game is over
                //so interrupt if it hasn't already been interrupted
                if (!(gameTime > System.currentTimeMillis() - startTime) && !Thread.currentThread().isInterrupted() && !byHits) {
                    Thread.currentThread().interrupt();
                    Log.i(TAG, "Sending game interrupt 3");  //Log to track what condition ended the game//
                }
            }
        }
    }

}