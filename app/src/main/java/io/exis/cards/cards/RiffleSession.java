package io.exis.cards.cards;

import android.util.Log;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;
import java.util.ArrayList;
import go.mantle.Mantle;
import com.exis.riffle.Domain;
import com.exis.riffle.Function;

/*
 * RiffleSession.java
 *
 * Created by Luke Salamone on 12/1/2015.
 *
 * Brokers interactions between server-side exec & dealer
 * and client players. Implements thin wrapper around WAMP calls
 *
 * Deferred calls adapted from https://github.com/jdeferred
 *
 * Copyright © 2015 exis. All rights reserved.
 *
 */
@SuppressWarnings("unused")
public class RiffleSession {

    //private WAMPWrapper WAMP;
    protected AndroidDeferredManager manager;
    String URI;
    Player player;
    Dealer dealer;
    Domain app;
    Function handler;

    //Favoring this constructor
    public RiffleSession(String domain){
        URI = domain;
        manager = new AndroidDeferredManager();
        app = new Domain("xs.damouse");
    }

    public String domain(){
        return URI;
    }

    //Call to Dealer::addPlayer
    public void addPlayer(Player player){
        dealer.addPlayer(player);
//        call("addPlayer", player);
    }

    /*
     * Call to Exec::addPoint
     */
    public void addPoint(Player player){
        Exec.addPoint(player);
//      call("addPoint", player);
    }

    /*
     * Called in GameActivity.
     */
    public void czarPick(Card card){
        dealer.czarPick(card);
//        call("czarPick", card);
    }

    /*
     * Called in GameActivity. Returns result of call to Dealer::dealCard
    */
    public Card dealCard(Player player){
        return dealer.dealCard(player);
//        return (Card) call("dealCard", player);
    }

    /*
     * Called in GameActivity. Returns result of call of Exec::findDealer
     */
    public Dealer findDealer(boolean adult){
        return Exec.findDealer();
//        return (Dealer) call("findDealer", adult);
    }//end findDealer method

    /*
     * Calls to Dealer::getNewHand
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Card> getNewHand(Player player){
        return dealer.getNewHand(player);
//        return (ArrayList<Card>) call("getNewHand", player);
    }//end getNewHand method

    /*
     * Called in GameActivity. Must return result of call to Exec::getNewID
     */
    public int getNewID(){
        Log.i("RiffleSession", "getNewID()");
        return Exec.getNewID();
//        return (int) call("getNewID");
    }//end getNewID method

    /*
     * Call to Dealer::getQuetion
    */
    public Card getQuestion(){
        return dealer.getQuestion();
//        return (Card) call("getSubmitted");
    }

    /*
     * Call to Dealer::getSubmitted
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Card> getSubmitted(){
        return dealer.getSubmitted();
//        return (ArrayList<Card>) call("getSubmitted");
    }

    /*
     * Called in GameActivity. Returns Dealer::isCzar
     */
    public boolean isCzar(Player player){
        return dealer.isCzar(player);
//        return (boolean) call("isCzar", player);
    }//end isCzar method

    /*
     * Call to Dealer::PrepareGame
     */
    public void prepareGame(){
        dealer.prepareGame();
//        call("prepareGame");
    }

    /*
     * Called in GameActivity when player sends card to dealer. Returns result of call to
     * Dealer::ReceiveCard(card).
     */
    public void receiveCard(Card card){
        dealer.receiveCard(card);
//        call("receiveCard", card);
    }

    /*
     * Call to Dealer::removePlayer
    */
    public void removePlayer(Player player){
        dealer.removePlayer(player);
//        call("removePlayer", player);
    }

    /*
     * Call to Dealer::setPlayers
     */
    public void setPlayers(){
        dealer.setPlayers();
//        call("setPlayers");
    }

    /************  damouse's methods  **************/
    //Player calls at beginning of game to find dealer. Returns new hand.
    public Object[] play(){
        String[] cards;

        // TODO
        Dealer dealer = Exec.findDealer();
        //dealer = (Dealer) call("findDealer", false);

        String roomName = Integer.toString(dealer.dealerID);
        player = new Player(URI, 0, false);
        addPlayer(player);

        //Returns: string[] cards, Player[] players, string state, string roomName
        return new Object[]{
                getNewHand(player),
                dealer.getPlayers(),
                "answering",                                     //TODO
                roomName};
    }//end play method

    //Tell the Dealer the Player picked a card
    public Object[] pick(String card){
        String[] cards;
        String roomName = "Room " + Exec.findDealer().getID();

        // TODO
        dealer.receiveCard(Card.searchCards(card));
//        call("receiveCard", new Card(0, card, 'a', 0));

        return new Object[]{
                player.getHand(),
                dealer.getPlayers(),
                "",
                roomName};
    }//end pick method

    //player calls upon leaving
    public void leave(){
        //call Dealer::removePlayer()
        dealer.removePlayer(player);
//        call("/removePlayer", player);
    }//end leave method

    //dealer pub
    public void answering(Player czar, String question, int duration){
//        app.publish("/answering", czar, question, duration);                  // TODO
    }

    //dealer pub
    public void picking(String[] answers, int duration){
//        app.publish("/picking", answers, duration);                           // TODO
    }

    //dealer pub
    public void scoring(Player winner, String winningCard, int duration){
//        app.publish("/scoring", winner, winningCard, duration);               // TODO
    }

    //dealer pub
    public void left(Player leavingPlayer){
//        app.publish("/left", leavingPlayer);                                  // TODO
    }

    //dealer pub
    public void joined(Player newPlayer){
//        app.publish("/joined", newPlayer);                                    // TODO
    }

    //called by player at beginning of round
    public String[] draw(){
        return new String[]{dealer.dealCard(player).getText()};
//        return (String[]) call("dealCard");
    }//end draw method

    /***********  End damouse's Methods **********/

    //Allows Dealer and Exec to register their calls.
    public void register(String procedure){
        if(procedure.charAt(0) == '/'){
            app.register(procedure);
        }else{
            app.register("/" + procedure);
        }
    }//end register method

    public void subscribe(String procedure){
        if(procedure.charAt(0) == '/'){
            app.subscribe(procedure, handler);
        } else {
            app.subscribe("/" + procedure, handler);
        }
    }//end subscribe method

    //TODO: app.call is void!
    private Object call(String method, Object...args){
        final ValueHolder<Object> result = new ValueHolder<>();
        final String endpoint;

        if(method.charAt(0) != '/'){
            endpoint = "/" + method;
        } else {
            endpoint = method;
        }

        try {
            manager.when(new DeferredAsyncTask<Void, Integer, String>() {
                @Override
                protected String doInBackgroundSafe(Void... nil) throws Exception {
                    //insert RPC call here
                    Log.i("RiffleSession::call()", "now calling " + method + "method");
//                    result.set(app.call(endpoint));                   // TODO
                    return "Done";
                }
            }).done(result::set)
              .waitSafely();
        } catch (InterruptedException e) {
            // Do nothing
        }

        return result.get();
    }//end call method

}