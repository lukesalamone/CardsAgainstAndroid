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
public class Player extends Domain {

    private int ID;                         //unique to every player
    private String playerID;
    private ArrayList<Card> hand;           //list of a player's cards
    private boolean isCzar;                 //whether the player is card czar
    private int score;
    private String dealerDomain;
    private boolean online;
    private Player czar;
    private String question;
    private int duration;
    private ArrayList<Card> answers;
    private Player winner;
    private String winningCard;
    private Object ret;
    private Card nextCard;

    GameActivity activity;
    Domain exec;

    Exec DANGER_EXEC;

    public Player(int ID, Domain app){
        super("player" + ID, app);
        exec = new Domain(dealerDomain, app);

        this.ID = ID;         // numbers only
        this.playerID = "player" + ID;
        this.online = GameActivity.online;
        this.hand = new ArrayList<>();
    }// end constructor

    @Override
    public void onJoin(){
        String TAG = "Player::onJoin()";

        Log.i(TAG, "creating new riffle session");
        activity.player = this;
        activity.riffle = new RiffleSession(this, exec);



        Log.i("Player", "sub to answering");
        subscribe("answering", String.class, String.class, Integer.class,
                (czarPlayer, questionText, duration) -> {
                    Log.i("answering sub", "received question " + questionText);
                    if (czarPlayer.equals(playerID)) {
                        this.setCzar(true);
                    }
                    this.question = questionText;
                    activity.setQuestion();
                    this.duration = duration;
                });

        Log.i("Player", "sub to picking");
        subscribe("picking", ArrayList.class, Integer.class,
                (answers, duration) -> {
                    Log.i("picking sub", "received answers " + Card.printHand(answers));
                    this.answers = answers;
                    this.duration = duration;
                });

        Log.i("Player", "sub to scoring");
        subscribe("scoring", Player.class, String.class, Integer.class,
                (winningPlayer, winningCard, duration) -> {
                    Log.i("scoring sub", "winning card " + winningCard);
                    this.winner = winningPlayer;
                    this.winningCard = winningCard;
                    this.duration = duration;
                });

        Log.i("Player", "reg draw");
        register("draw", Card.class, Object.class, this::draw);

        // TODO should not be using DANGER_EXEC
        Object[] playObject = DANGER_EXEC.play();

        if(playObject == null){
            Log.wtf(TAG, "play object is null!");
        }

        try {
            activity.hand = (String[]) playObject[0];
        }catch(NullPointerException e){
            Log.wtf(TAG, "hand is null!");
        }

        activity.players = (Player[]) playObject[1];
        activity.state = (String) playObject[2];
        activity.roomName = (String) playObject[3];

        setDealer(activity.roomName);

        Log.i(TAG, "onJoin Finished");
        activity.onPlayerJoined();
    }

    public Domain domain(){
        return this;
    }

    public ArrayList<Card> getHand(){
        return this.hand;
    }//end getCards method

    public int ID(){
        return this.ID;
    }//end getPlayerID method

    public String playerID(){
        return playerID;
    }

    public boolean isCzar(){
        return this.isCzar;
    }

    public void setCzar(boolean isCzar){
        this.isCzar = isCzar;
    }

    public void setHand(ArrayList<Card> hand){
        this.hand = hand;
    }

    public void setDealer(String dealerDomain){
        this.dealerDomain = dealerDomain;
    }

    //add a card to player's hand
    public Object draw(Card card){
        hand.add(card);
        return null;
    }//end addCard method

    // removes card from player's hand
    public boolean removeCard(Card card){
        boolean removed;
        removed = hand.remove(card);
        return removed;
    }// end removeCard method

    // submit card to dealer
    // dealer calls this method on player
    public void pick(Dealer dealer, Card card){
        if(online) {
            call("pick", this, card.getText()).then(Object.class, this::setRet);
            nextCard = (Card) getRet();
        }else{
            dealer.pick(this, card.getText());
        }
    }// end pick method

    public String printHand(){
        String s = "";

        for(Card c: hand){
            s += c.getText() + "\n";
        }

        return s;
    }

    private void setRet(Object o){
        ret = o;
    }

    // getter methods
    private Object getRet(){
        return ret;
    }

    public String question(){
        return question;
    }
}