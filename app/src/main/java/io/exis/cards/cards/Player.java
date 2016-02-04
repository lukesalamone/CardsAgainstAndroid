package io.exis.cards.cards;

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

    public Player(int ID, ArrayList<Card> cards, boolean czar){
        playerID = ID;
        hand = cards;
        boolean isCzar = czar;
        riffle = new RiffleSession("ws://ec2-52-26-83-61.us-west-2.compute.amazonaws.com:8000/ws");
    }//end Player constructor

    //damouse's player object
    public Player(String domain, int score, boolean czar){
        URL = domain;
        riffle = new RiffleSession(domain);
        score = 0;                                  //players are not persistent :(
        isCzar = czar;
    }

    public String getDomain(){
        return URL;
    }

    public ArrayList<Card> getHand(){
        return hand;
    }//end getCards method

    public int getPlayerID(){
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
    public void addCard(Card card){
        hand.add(card);
    }//end addCard method

    public Card submit(){
        return null;
    }

    //removes card from player's hand
    public boolean removeCard(Card card){
        boolean removed;
        removed = hand.remove(card);

        if(!removed){
            //riffle.reportError(2, getPlayerID(), "Unable to remove " +
            //        "card from hand.", card, hand);
        }

        return removed;
    }//end removeCard method
}