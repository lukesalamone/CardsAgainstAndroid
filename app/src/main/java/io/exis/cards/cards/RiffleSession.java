package io.exis.cards.cards;

import android.util.Log;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;
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
@SuppressWarnings("unused")
public class RiffleSession {

    private WAMPWrapper WAMP;
    protected AndroidDeferredManager manager;
    String URI;
    Dealer dealer;

    //Constructor
    RiffleSession() {
        URI = "ws://ec2-52-26-83-61.us-west-2.compute.amazonaws.com:8000/ws";
        manager = new AndroidDeferredManager();
        WAMP = new WAMPWrapper(URI);
    }

    //Favoring this constructor
    public RiffleSession(String domain){
        URI = domain;
        manager = new AndroidDeferredManager();
        WAMP = new WAMPWrapper(URI);
    }

    public String getDomain(){
        return URI;
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
    public Dealer findDealer(boolean adult){
        return (Dealer) call("findDealer", adult);
    }//end findDealer method

    /*
     * Calls to Dealer::getNewHand
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Card> getNewHand(Player player){
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
    @SuppressWarnings("unchecked")
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
    public void prepareGame(){
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
    public void setPlayers(){
        call("setPlayers");
    }

    /************  damouse's Methods  **************/
    //Player calls at beginning of game to find dealer. Returns new hand.
    public Object[] play(){
        String[] cards;
        //Dealer dealer = Exec.findDealer(true);
        dealer = (Dealer) call("findDealer", false);
        String roomName = Integer.toString( dealer.dealerID );
        Player player = new Player(URI, 0, false);
        addPlayer(player);

        return new Object[]{
                getNewHand(player),
                dealer.getPlayers(),
                "",                                     //TODO
                roomName};
    }//end play method

    //Tell the Dealer the Player picked a card
    public Object[] pick(String card){
        String[] cards;
        String roomName = Exec.findDealer(true).toString();
        Player player = new Player(URI, 0, false);
        call("receiveCard", new Card(0, card, 'a', 0));

        return new Object[]{
                player.getHand(),
                dealer.getPlayers(),
                "",
                roomName};
    }//end pick method

    //player calls upon leaving
    public void leave(){

    }//end leave method

    //dealer pub
    public void answering(Player czar, String question, int duration){

    }

    //dealer pub
    public void picking(String[] answers, int duration){

    }

    public void scoring(Player winner, String winningCard, int duration){

    }

    public void left(Player leavingPlayer){

    }

    public void joined(Player newPlayer){

    }

    //called by player at beginning at round
    public String[] draw(){
        return (String[]) call("dealCard");
    }//end draw method

    /***********  End damouse's Methods **********/

    /*
     * Allows Dealer and Exec to register their calls.
     */
    public void register(String method){
        WAMP.register(method);
    }//end register method

    private Object call(String method, Object...args){
        final ValueHolder<Object> result = new ValueHolder<>();

        try {
            manager.when(new DeferredAsyncTask<Void, Integer, String>() {
                @Override
                protected String doInBackgroundSafe(Void... nil) throws Exception {
                    //insert RPC call here
                    result.set(WAMP.call(method, args));
                    Log.i("RiffleSession::call()", "done calling " + method + "method");

                    return "Done";
                }
            }).done(res -> {
                //do nothing
            }).waitSafely();
        } catch (InterruptedException e) {
            // Do nothing
        }

        return result.get();
    }//end call method

}