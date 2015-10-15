package io.exis.cards.cards;

import java.util.ArrayList;
import java.util.List;

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

    private Riffle riffle = new Riffle();

    public Player(int ID, ArrayList<Card> cards){
        playerID = ID;
        hand = cards;
    }//end Player constructor

    //allows player to submit to dealer
    public void submitCard(Card card){
        int nullPos = -1;
        boolean removed = false;


        //get a receipt
        boolean received = riffle.submit(card);
        while(!received){
            if(received){
                removed = removeCard(card);
                break;
            }
        }

        Card newCard = riffle.receive();

        if(removed){
            addCard(newCard);
        } else {
            riffle.reportError(0, "Unable to add new card" +
                " due to riffle response error", null, hand);
        }

        return;

    }//end submitCard method

    public ArrayList<Card> getCards(){
        return riffle.getCards(this.playerID);
    }//end getCards method

    public int getPlayerID(){
        return playerID;
    }//end getPlayerID method

    public void leaveRoom(){
        riffle.leave(this);
    }

    //add a card to player's hand at position pos
    private void addCard(Card card){
        hand.add(card);
        if(hand.size() != 5) {
            riffle.reportError(1, "Card desync error" +
                " in addCard method!", card, hand);
        }
        return;
    }//end addCard method

    //removes card from player's hand, replaced with null
    //returns position of null card
    private boolean removeCard(Card card){
        boolean removed;
        removed = hand.remove(card);

        if(!removed){
            riffle.reportError(2, "Unable to removed " +
                    "card from hand.", card, hand);
        }

        return removed;
    }//end removeCard method

}
