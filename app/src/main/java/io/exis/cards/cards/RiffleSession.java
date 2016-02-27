package io.exis.cards.cards;

import android.util.Log;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;
import java.util.ArrayList;
import go.mantle.Mantle;

import com.exis.riffle.CallDeferred;
import com.exis.riffle.Domain;
import com.exis.riffle.Function;
import com.exis.riffle.cumin.Handler;

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
    Dealer dealer;
    Domain app;
    Function handler;
    Player player;
    Object ret;

    //Favoring this constructor
    public RiffleSession(String uri, Player player){
        URI = uri;
        manager = new AndroidDeferredManager();
        app = Exec.getGame();
        this.player = player;
    }

    public RiffleSession(String uri){
        URI = uri;
        manager = new AndroidDeferredManager();
        app = Exec.getGame();
    }

    /* damouse's methods
     *
     * For now, these methods are a thin wrapper around luke's original methods
     * TODO unwrap methods to use damouse
     */

    //server-side
    //Player calls at beginning of game to find dealer. Returns new hand.
    public Object[] play(){
        String[] cards;

        // TODO
        Dealer dealer = Exec.findDealer(true);// TODO
        addPlayer(this.player);

        // TODO
        app.call(dealer.ID() + "/play", this.player).then(Object[].class, this::setRet);
        return (Object[]) getRet();
    }//end play method

    //TODO
    // player calls pick to tell dealer that Player picked a card
    public Object[] pick(Player player, String card){
        String[] cards;
        String roomName = "Room " + Exec.findDealer(true).ID();

        // TODO
        dealer.receiveCard(Card.searchCards(card));

        return new Object[]{
                player.getHand(),
                dealer.getPlayers(),
                "",
                roomName};
    }//end pick method

    // player calls Dealer::removePlayer() upon leaving
    public void leave(){
        app.call("removePlayer", player).then(()->{});
    }//end leave method

    // player calls when starting game
    public void join(Player player){
        app.call("/join", Player.class).then(()->{});
    }

    //dealer pub
    public void answering(Player czar, String question, int duration){
        app.publish("answering", czar, question, duration);
    }

    //dealer pub
    public void picking(String[] answers, int duration){
        app.publish("picking", answers, duration);
    }

    //dealer pub
    public void scoring(Player winner, String winningCard, int duration){
        app.publish("scoring", winner, winningCard, duration);
    }

    //dealer pub
    public void left(Player leavingPlayer){
        app.publish("left", leavingPlayer);
    }

    //dealer pub
    public void joined(Player newPlayer){
        app.publish("joined", newPlayer);
    }

    // dealer calls Player::draw to give card to player
    public void draw(Player player, Card card){
        String endpoint = player.getDomain() + "/draw";
        app.call(endpoint, card.getText()).then(()->{});
    }//end draw method

    /*     end damouse's methods    */

    public void setPlayer(Player player){
        this.player = player;
    }

    public String domain(){
        return URI;
    }

    //Call to Dealer::addPlayer
    public void addPlayer(Player player){
        dealer.addPlayer(player);
    }

    /*
     * Call to Exec::addPoint
     */
    public void addPoint(Player player){
        Exec.addPoint(player);
    }

    // Called in GameActivity. Returns result of call to Dealer::dealCard
    public Card dealCard(Player player){
        return dealer.dealCard(player);
    }

    /*
     * Called in GameActivity. Returns result of call of Exec::findDealer
     */
    public Dealer findDealer(){
        return Exec.findDealer(true);
    }//end findDealer method

    /*
     * Calls to Dealer::getNewHand
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Card> getNewHand(Player player){
        return dealer.getNewHand(player);
    }//end getNewHand method

    /*
     * Called in GameActivity. Must return result of call to Exec::getNewID
     */
    public int getNewID(){
        return Exec.getNewID();
    }//end getNewID method

    /*
     * Call to Dealer::getQuetion
    */
    public Card getQuestion(){
        return dealer.getQuestion();
    }

    /*
     * Call to Dealer::getSubmitted
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Card> getSubmitted(){
        return dealer.getSubmitted();
    }

    /*
     * Called in GameActivity. Returns Dealer::isCzar
     */
    public boolean isCzar(Player player){
        return dealer.isCzar(player);
    }//end isCzar method

    /*
     * Call to Dealer::PrepareGame
     */
    public void prepareGame(){
        dealer.prepareGame();
    }

    /*
     * Called in GameActivity when player sends card to dealer. Returns result of call to
     * Dealer::ReceiveCard(card).
     */
    private void receiveCard(Card card){
        dealer.receiveCard(card);
    }

    /*
     * Call to Dealer::removePlayer
    */
    private void removePlayer(Player player){
        dealer.removePlayer(player);
    }

    private void setRet(Object o){
        this.ret = o;
    }

    private Object getRet(){
        return this.ret;
    }
}