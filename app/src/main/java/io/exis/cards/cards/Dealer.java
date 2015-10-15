package io.exis.cards.cards;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Dealer.java
 * Manages decks and player points
 * May report corrupted players
 *
 * Created by luke on 10/13/15.
 */
public class Dealer {

    final int ROOMCAP = 6;

    //keep track of players playing
    ArrayList<Player> players = new ArrayList<Player>();

    //keep track of cards in play
    ArrayList<Card> inPlay = new ArrayList<Card>();

    //keep track of cards not in play
    ArrayList<Card> deck = new ArrayList<Card>();

    //dealers initiated with PID = 0
    RiffleSession riffle = new RiffleSession(0 + "");

    //pg13 or R
    boolean rating;
    int dealerID;

    public Dealer(boolean R, int ID){
        rating = R;
        dealerID = ID;
    }

    public Card dealCard(Player player){

        //generate new card to give to player
        Card card = generateCard();

        //remove card from deck
        deck.remove(card);

        //add card to player's hand
        player.hand.add(card);

        //add card to cards in play
        inPlay.add(card);

        //send card to player
        riffle.sendCard(player.getPlayerID());

        return card;

    }//end dealCard method

    public boolean full(){
        return (getGameSize() + 1 > this.ROOMCAP);
    }

    public boolean addPlayer(Player player){

        //max capacity exceeded
        if(full()){
            return false;
        }

        //add to local player list
        players.add(player);

        //deal them 5 cards
        for(int i=0; i<5; i++){
            dealCard(player);
        }

        return true;
    }//end addPlayer method

    public int getGameSize(){
        return players.size();
    }//end getGameSize method

    public boolean getRating(){
        return this.rating;
    }

    public void removePlayer(Player player){
        players.remove(player);
    }//end remove player method

    private Card generateCard(){
        Collections.shuffle(deck);
        return deck.get(0);
    }//end generateCard method

}
