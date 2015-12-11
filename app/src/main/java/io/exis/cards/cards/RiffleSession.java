package io.exis.cards.cards;

import android.util.Log;

import junit.framework.Assert;
import org.jdeferred.DoneCallback;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;
import com.thetransactioncompany.jsonrpc2.*;
import com.thetransactioncompany.jsonrpc2.client.*;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/*
 * RiffleSession.java
 *
 * Let's try this again with jdeferred.
 *
 * Brokers interactions between server-side exec & dealer
 * and client players.
 *
 * Adapted from https://github.com/jdeferred by Luke Salamone on 12/1/2015.
 *
 * Copyright © 2015 exis. All rights reserved.
 *
 */

public class RiffleSession {

    private WAMPWrapper session;
    protected AndroidDeferredManager manager;

    //Constructor
    RiffleSession(){
        String URI = "ws://ec2-52-26-83-61.us-west-2.compute.amazonaws.com:8000/ws";
        manager = new AndroidDeferredManager();
        session = new WAMPWrapper(URI);
    }
    /*
     * Call to Dealer::addPlayer
     */
    public void addPlayer(Player player){
        call("addPlayer", player);
    }

    /*
     * Call to Exec::addPoint
     */
    public void addPoint(Player player){
        call("addPoint", player);
    }

    /*
     * Called in GameActivity.
     */
    public void czarPick(Card card){
        call("czarPick", card);
    }

    /*
     * Called in GameActivity. Returns result of call to Dealer::dealCard
    */
    public Card dealCard(Player player){
        return (Card) call("dealCard", player);
    }

    /*
     * Called in GameActivity. Returns result of call of Exec::findDealer
     */
    Dealer findDealer(boolean adult){
        return (Dealer) call("findDealer", adult);
    }//end findDealer method

    /*
     * Calls to Dealer::getNewHand
     */
    ArrayList<Card> getNewHand(Player player){
        return (ArrayList<Card>) call("getNewHand", player);
    }//end getNewHand method

    /*
     * Called in GameActivity. Must return result of call to Exec::getNewID
     */
    public int getNewID(){
        return (int) call("getNewID");
    }//end getNewID method

    /*
     * Call to Dealer::getQuetion
    */
    public Card getQuestion(){
        return (Card) call("getSubmitted");
    }

    /*
     * Call to Dealer::getSubmitted
     */
    public ArrayList<Card> getSubmitted(){
        return (ArrayList<Card>) call("getSubmitted");
    }

    /*
     * Called in GameActivity. Returns Dealer::isCzar
     */
    public boolean isCzar(Player player){
        return (boolean) call("isCzar", player);
    }//end isCzar method

    /*
     * Call to Dealer::PrepareGame
     */
    void prepareGame(){
        call("prepareGame");
    }

    /*
     * Called in GameActivity when player sends card to dealer. Returns result of call to
     * Dealer::ReceiveCard(card).
     */
    public void receiveCard(Card card){
        call("receiveCard", card);
    }

    /*
     * Call to Dealer::removePlayer
    */
    public void removePlayer(Player player){
        call("removePlayer", player);
    }

    /*
     * Call to Dealer::setPlayers
     */
    void setPlayers(){
        call("setPlayers");
    }

    /*
     * Allows Dealer and Exec to register their calls.
     */
    private void register(String method){
        session.register(method);
    }

    private Object call(String method, Object...args){
        final ValueHolder<Object> result = new ValueHolder<>();

        try {
            manager.when(new DeferredAsyncTask<Void, Integer, String>() {
                @Override
                protected String doInBackgroundSafe(Void... nil) throws Exception {
                    //insert RPC call here
                    result.set(session.call(method, args));

                    return "Done";
                }
            }).done(res -> {
                //do nothing
            }).waitSafely();
        } catch (InterruptedException e) {
            // Do nothing
        }

        return result.get();
    }

}