package io.exis.cards.cards;

import java.util.ArrayList;

/**
 * Riffle.java
 * Talks to dealer
 *
 * Created by luke on 10/13/15.
 */
public class Riffle {

    public Riffle(){

    }//end Riffle constructor

    public ArrayList<Card> getCards(int playerID){

    }//end getCards method

    public boolean isCardCzar(){

    }//end isCardCzar method

    public boolean submit(Card card){
        boolean received = true;

        /*
         *
         * Insert magic here...
         *
         */

        return received;
    }//end submit method


    public Card receive(){

    }//end receive method

    //called when player leaves room
    public void leave(Player player){

    }//end leave method

    public void reportError(int errCode, String msg){

    }

    public void reportError(int errCode, String msg, Card card){

    }//end reportError method

    public void reportError(int errCode, String msg, Card card, ArrayList<Card> hand){

    }//end reportError method
}
