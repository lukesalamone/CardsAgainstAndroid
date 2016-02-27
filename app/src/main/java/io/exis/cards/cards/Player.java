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
    private int score;
    private String domain;
    private boolean online;

    public Player(String domain, int score, boolean czar){
        this.playerID = Integer.parseInt(domain.substring(6, domain.length()));
        this.domain = "player" + playerID;
        this.isCzar = czar;
        this.score = score;
        this.online = GameActivity.online;
        this.hand = new ArrayList<>();

        if(online) {
            this.riffle = new RiffleSession(domain);
            Game.subscribe("answering", Player.class, String.class, Integer.class,
                    (czarPlayer, questionText, duration) -> Log.i("answering sub", "received question " + questionText));
            Game.subscribe("picking", ArrayList.class, Integer.class,
                    (answers, duration) -> Log.i("picking sub", "received answers " + Card.printHand(answers)));
            Game.subscribe("scoring", Player.class, String.class, Integer.class,
                    (winningPlayer, winningCard, duration) -> Log.i("scoring sub", "winning card " + winningCard));
            Game.register(domain + "/draw", Card.class, Object.class, this::draw);

            // join the game
            riffle.join(this);
        }
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
    public Object draw(Card card){
        hand.add(card);
        return null;
    }//end addCard method

    public Card submit(){
        return null;
    }

    // removes card from player's hand
    public boolean removeCard(Card card){
        boolean removed;
        removed = hand.remove(card);
        return removed;
    }// end removeCard method

    // submit card to dealer
    //TODO implement this method
    public void pick(Dealer dealer, Card card){
        if(online) {
            Game.call(dealer.ID() + "/pick", this, card.getText()).then( ()->{} );
        }else{
            dealer.pick(this, card.getText());
        }
    }// end pick method
}