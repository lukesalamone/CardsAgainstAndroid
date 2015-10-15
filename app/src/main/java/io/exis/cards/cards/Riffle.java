package io.exis.cards.cards;

import java.util.ArrayList;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Riffle.java
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
            connection.connect(uri, new WebSocketHandler() {

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
            });
        } catch (WebSocketException e) {
            Log.d(TAG, e.toString());
        }
    }//end start method

    public ArrayList<Card> getCards(int playerID){

    }//end getCards method

    public boolean isCardCzar(){

    }//end isCardCzar method

    public boolean submit(Card card){
        boolean received = true;

        /*
         *
         * Insert magic here...
         *
         */

        return received;
    }//end submit method


    public Card receive(){

    }//end receive method

    //called when player leaves room
    public void leave(Player player){

    }//end leave method

    public void reportError(int errCode, String msg){

    }

    public void reportError(int errCode, String msg, Card card){

    }//end reportError method

    public void reportError(int errCode, String msg, Card card, ArrayList<Card> hand){

    }//end reportError method
}
