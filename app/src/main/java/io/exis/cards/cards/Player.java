package io.exis.cards.cards;

import android.util.Log;

import com.exis.riffle.Domain;

import java.util.ArrayList;

/**
 * Player.java
 * Controller for a player
 *
 * All players are PG13 as of Dec 17
 *
 * Created by luke on 10/13/15.
 */
public class Player {

    private int playerID;                   //unique to every player
    private ArrayList<Card> hand;           //list of a player's cards
    private boolean isCzar;                 //whether the player is card czar
    private RiffleSession riffle;
    private String URL;
    private Domain Game;

    public Player(int ID, ArrayList<Card> cards, boolean czar){
        playerID = ID;
        hand = cards;
        boolean isCzar = czar;
        riffle = new RiffleSession("ws://ec2-52-26-83-61.us-west-2.compute.amazonaws.com:8000/ws");

    }//end Player constructor

    // damouse's player object
    public Player(String domain, int score, boolean czar){
        URL = domain;
        riffle = new RiffleSession(domain);
        isCzar = czar;
        playerID = createID();

        //TODO do something with this info
        Game.subscribe("answering", Player.class, String.class, Integer.class,
                (czarPlayer, questionText, duration) -> Log.i("answering sub", "received question " + questionText));
        Game.subscribe("picking", ArrayList.class, Integer.class,
                (answers, duration)->Log.i("picking sub", "received answers " +  Card.printHand(answers)) );
        //Game.publish("scoring", winner, Dealer.getWinningCard().getText(), 10);
        Game.subscribe("scoring", Player.class, String.class, Integer.class,
                (winningPlayer, winningCard, duration)->Log.i("scoring sub", "winning card " + winningCard));

        //TODO register all endpoints
//        Game.register(domain + "/draw", Card.class, Player::draw);

        // join the game
        riffle.join(this);

    }

    // TODO
    public Domain getGame(){
        return this.Game;
    }

    public String getDomain(){
        return this.URL;
    }

    public ArrayList<Card> getHand(){
        return this.hand;
    }//end getCards method

    public int ID(){
        return this.playerID;
    }//end getPlayerID method

    public boolean isCzar(){
        return this.isCzar;
    }

    public void setCzar(boolean isCzar){
        this.isCzar = isCzar;
    }

    public void setHand(ArrayList<Card> hand){
        this.hand = hand;
    }

    public Card czarPicks(ArrayList<Card> czarList){
        int x = 0;

        return czarList.get(x);
    }//end czarPicks method

    //add a card to player's hand
    public void draw(Card card){
        hand.add(card);
    }//end addCard method

    public Card submit(){
        return null;
    }

    //removes card from player's hand
    public boolean removeCard(Card card){
        boolean removed;
        removed = hand.remove(card);
        return removed;
    }//end removeCard method

    private int createID(){
        return (int) (Math.random() * Integer.MAX_VALUE);
    }

    // submit card to dealer
    //TODO implement this method
    public void pick(Dealer dealer, Card card){
        Game.call(dealer.ID() + "/pick", this, card.getText()).then(()->{});
    }
}