package io.exis.cards.cards;

import android.util.Log;
import java.util.ArrayList;
import go.mantle.Mantle;
import com.exis.riffle.CallDeferred;
import com.exis.riffle.Domain;
import com.exis.riffle.Function;
import com.exis.riffle.Riffle;
import com.exis.riffle.cumin.Handler;

/*
 * RiffleSession.java
 *
 * Created by Luke Salamone on 12/1/2015.
 *
 * Brokers interactions between server-side exec & dealer
 * and client players. Implements thin wrapper around riffle.Domain calls
 *
 * Copyright © 2015 exis. All rights reserved.
 *
 */
@SuppressWarnings("unused")
public class RiffleSession {
    Dealer dealer;
    Domain app;
    Function handler;
    Player player;
    Object ret;

    public RiffleSession(Domain app){
        this.app = app;
    }

    /* damouse's methods
     *
     * For now, these methods are a thin wrapper around Luke's original methods
     * TODO unwrap methods to use damouse
     */

    //server-side
    //Player calls Exec at beginning of game to find dealer. Returns new hand.
    public Object[] play(){
        app.call("play").then(Object[].class, this::setRet);
        return (Object[]) getRet();
    }//end play method

    /* dealer calls pick to receive picked card from player
     *
     * @param card New card for player
    */
    public void pick(Card card) {
        app.call("pick", card).then(Card.class, player::pick);
    }//end pick method

    // player calls Dealer::removePlayer() upon leaving
    public void leave(){
        app.call("leave", player).then(() -> {
        });
    }//end leave method

    // player calls when starting game
    public void join(Player player){
        app.call("join", Player.class).then(() -> {
        });
    }

    //dealer pub
    public void answering(Player czar, String question, int duration){
        app.publish("answering", czar, question, duration);
    }

    //dealer pub
    public void picking(String[] answers, int duration){
        app.publish("picking", answers, duration);
    }

    // dealer pub TODO args
    public void choosing(){
        app.publish("choosing");
    }

    //dealer pub
    public void scoring(Player winner, String winningCard, int duration){
        app.publish("scoring", winner, winningCard, duration);
    }

    // dealer pub TODO args
    public void tick(){
        app.publish("tick");
    }

    // dealer pub TODO args
    public void current(Player czar){
        app.publish("current", czar);
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
    public void draw(Card card){
        app.call("draw", card.getText()).then(()->{});
    }//end draw method

    /*     end damouse's methods    */

    public void setPlayer(Player player){
        this.player = player;
    }

    // Called in GameActivity. Returns result of call to Dealer::dealCard
    public Card dealCard(Player player){
        return dealer.dealCard(player);
    }

    /*
     * Called in GameActivity. Returns result of call of Exec::findDealer
     */
    public Dealer findDealer(){
        return Exec.findDealer();
    }//end findDealer method

    /*
     * Calls to Dealer::getNewHand
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Card> getNewHand(Player player){
        return dealer.getNewHand();
    }//end getNewHand method

    /*
     * Called in GameActivity. Must return result of call to Exec::getNewID
     */
    public int getNewID(){
        return Exec.getNewID();
    }//end getNewID method

    /*
     * Call to Dealer::getQuestion
    */
    public Card getQuestion(){
        return dealer.getQuestion();
    }

    /*
     * Call to Dealer::PrepareGame
     */
    public void prepareGame(){
        dealer.prepareGame();
    }

    /*
     * Call to Dealer::removePlayer
    */
    private void removePlayer(Player player){
        dealer.leave(player);
    }

    private void setRet(Object o){
        if(o == null){
            Riffle.warn("Return object is null!");
        }
        this.ret = o;
    }

    private Object getRet(){
        return this.ret;
    }
}