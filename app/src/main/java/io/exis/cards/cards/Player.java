package io.exis.cards.cards;

import java.util.ArrayList;

/**
 * Player.java
 * Controller for a player
 *
 * Created by luke on 10/13/15.
 */
public class Player {

    private int playerID;                   //unique to every player
    private ArrayList<Card> hand;           //list of a player's cards
    private boolean isCzar;                 //whether the player is card czar
    //private RiffleSession riffle = new RiffleSession();
    private Dealer dealer;

    public Player(int ID, ArrayList<Card> cards, boolean czar){
        playerID = ID;
        hand = cards;
        boolean isCzar = czar;
        dealer = Exec.findDealer(true);
    }//end Player constructor

    /*
    //allows player to submit to dealer
    public void submitCard(Card card){
        boolean removed = false;



        //get a receipt
        boolean received = dealer.submit(card);
        while(!received){
            if(received){
                removed = removeCard(card);
                break;
            }
        }

        Card newCard = dealer.dealCard(this);

        if(removed){
            addCard(newCard);
        } else {
            //riffle.reportError(0, getPlayerID(), "Unable to add new card" +
            //    " due to riffle response error", null, hand);
        }
    }//end submitCard method
*/
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

    public void leaveRoom(){
        //riffle.leave(this);
        dealer.removePlayer(this);
    }

    public Card czarPicks(ArrayList<Card> czarList){
        int x = 0;

        return czarList.get(x);
    }//end czarPicks method

    //add a card to player's hand
    public void addCard(Card card){
        hand.add(card);
    }//end addCard method

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
