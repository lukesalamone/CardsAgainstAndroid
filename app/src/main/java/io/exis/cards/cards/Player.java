package io.exis.cards.cards;

import java.util.ArrayList;

/**
 * Player.java
 * Controller for a player
 *
 * Created by luke on 10/13/15.
 */
public class Player {

    //unique to every player
    private int playerID;

    //list of a player's cards
    ArrayList<Card> hand = new ArrayList<Card>();
    boolean isCzar;

    private RiffleSession riffle = new RiffleSession();

    public Player(int ID, ArrayList<Card> cards, boolean czar){
        playerID = ID;
        hand = cards;
        boolean isCzar = czar;
    }//end Player constructor

    //allows player to submit to dealer
    public void submitCard(Card card){
        boolean removed = false;

        //get a receipt
        boolean received = riffle.submit(card);
        while(!received){
            if(received){
                removed = removeCard(card);
                break;
            }
        }

        Card newCard = riffle.drawCard(getPlayerID());

        if(removed){
            addCard(newCard);
        } else {
            riffle.reportError(0, getPlayerID(), "Unable to add new card" +
                " due to riffle response error", null, hand);
        }
    }//end submitCard method

    public ArrayList<Card> getCards(){
        return riffle.getCards(this.playerID);
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

    public void leaveRoom(){
        riffle.leave(this);
    }

    public Card czarPicks(ArrayList<Card> czarList){
        int x = 0;

        return czarList.get(x);
    }//end czarPicks method

    //add a card to player's hand at position pos
    private void addCard(Card card){
        hand.add(card);
        if(hand.size() != 5) {
            riffle.reportError(1, getPlayerID(), "Card desync error" +
                " in addCard method!", card, hand);
        }
    }//end addCard method

    //removes card from player's hand
    private boolean removeCard(Card card){
        boolean removed;
        removed = hand.remove(card);

        if(!removed){
            riffle.reportError(2, getPlayerID(), "Unable to remove " +
                    "card from hand.", card, hand);
        }

        return removed;
    }//end removeCard method

}
