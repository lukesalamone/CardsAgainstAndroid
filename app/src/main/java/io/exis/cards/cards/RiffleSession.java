package io.exis.cards.cards;

import java.util.ArrayList;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * RiffleSession.java
 * Broker between dealer and player
 *
 * Created by luke on 10/13/15.
 */
public class RiffleSession {

    public RiffleSession(String playerID){
        final WebSocketConnection connection = new WebSocketConnection();
        start(playerID);
    }//end Riffle constructor

    public void start(final String TAG) {

        final String uri = "ws://ubuntu@ec2-52-26-83-61.us-west-2.compute.amazonaws.com:8000/ws";

        try {
            this.connection.connect(uri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + uri);
                    connection.sendTextMessage("Hello, world!");
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.d(TAG, "Got echo: " + payload);
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(TAG, "Connection lost.");
                }

                //allows player to request his hand
                public ArrayList<Card> getHand(){

                }//end getHand method

                //allows players and dealers to play cards
                public boolean sendCard(Card card, int playerID){

                }//end sendCard method

            });
        } catch (WebSocketException e) {
            Log.d(TAG, e.toString());
        }
    }//end start method

    public ArrayList<Card> getCards(int playerID) {

    }//end getCards method

    public boolean isCardCzar(){

    }//end isCardCzar method

    //called when a player submits his card
    public boolean submit(Card card){
        boolean received = true;

        /*
         *
         * Insert magic here...
         *
         */

        return received;
    }//end submit method


    public Card sendCard(int PID){

    }//end receive method

    //called when player leaves room
    public void leave(Player player){

    }//end leave method

    public int getNewID(){

    }

    public void rejectPlayer(Player player){

    }

    public void reportError(int errCode, int PID, String msg){

    }

    public void reportError(int errCode, int PID, String msg, Card card){

    }//end reportError method

    public void reportError(int errCode, int PID, String msg, Card card, ArrayList<Card> hand){

    }//end reportError method
}
