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
 * TODO implement pub to reject & choose
 *
 * Created by luke on 10/13/15.
 */
public class Player {

    private int ID;
    private String playerID;
    private ArrayList<Card> hand;
    private ArrayList<Card> answers;
    private boolean isCzar;
    private int duration;
    private int score;
    private String dealerDomain;
    private String question;
    private Player winner;
    private String winningCard;
    private Object ret;
    private Card nextCard;
    private Card picked;
    boolean dummy;

    GameActivity activity;                  // TODO get rid of this
    Domain exec;
    private Receiver playerDomain;

    Exec DANGER_EXEC;

    public Player(int ID, Domain app){
        exec = new Domain(dealerDomain, app);
        playerDomain = new Receiver("player" + ID, app);
        playerDomain.player = this;

        this.ID = ID;
        playerID = "player" + ID;
        hand = new ArrayList<>();
        score = 0;
        dummy = false;
    }// end constructor

    // constructor for dummies
    public Player(){
        ID = Exec.getNewID();
        playerID = "dummy" + ID;
        hand = new ArrayList<>();
        score = 0;
        dummy = true;
    }// end dummy constructor

    // Exec calls
    public void join(){
        playerDomain.join();
    }

    //add a card to player's hand
    public Object draw(Card card){
        hand.add(card);
        return null;
    }//end addCard method

    // dealer calls this method on player
    public Card pick(Card newCard){
        hand.add(newCard);
        hand.remove(picked);
        return picked;
    }// end pick method

    public Receiver playerDomain(){
        return playerDomain;
    }

    public ArrayList<Card> hand(){
        return this.hand;
    }//end getCards method

    public Player getWinner(){
        return this.winner;
    }

    public int ID(){
        return this.ID;
    }//end getPlayerID method

    public String playerID(){
        return playerID;
    }

    public ArrayList<Card> answers(){
        return answers;
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

    // removes card from player's hand
    public boolean removeCard(Card card){
        boolean removed;
        removed = hand.remove(card);
        return removed;
    }// end removeCard method

    public void setPicked(int pos){
        picked = hand.get(pos);

        if(GameActivity.phase.equals("choosing")){
//            playerDomain.publish("chose", picked);
            GameActivity.dealer.danger_pub_chose(picked);
        }else{ // picking phase
            playerDomain.publish("picked", picked);
            GameActivity.dealer.danger_pub_picked(picked);
        }
    }

    public void addPoint(){
        score++;
    }

    public String printHand(){
        String s = "";

        for(Card c: hand){
            s += c.getText() + "\n";
        }

        return s;
    }

    public String question(){
        if(question == null || question.equals("")) {
            return Dealer.generateQuestion().getText();
        }else{
            return question;
        }
    }

    public void danger_pub_answering(int czarID, String questionText, int duration){
        Log.i("answering sub", "received question " + questionText);
        this.isCzar = (czarID == this.ID);
        this.question = questionText;
        this.duration = duration;
        activity.runOnUiThread(() -> {
            Log.i("player", "setting question");
            activity.setQuestion();                              //set question TextView
        });
    }

    public void danger_pub_picking(String[] answers, int duration){
        Log.i("picking sub", "received answers " + Card.printHand(answers));
        this.answers = Card.buildHand(answers);
        this.duration = duration;
    }

    public void danger_pub_scoring(Player winningPlayer, String winningCard, int duration){
        Log.i("scoring sub", "winning card " + winningCard);
        this.winner = winningPlayer;
        this.winningCard = winningCard;
        this.duration = duration;
    }

    // Receiver handles riffle calls
    private class Receiver extends Domain{
        private Player player;

/*        public Receiver(String name) {
            super(name);
        }*/

        public Receiver(String name, Domain superdomain) {
            super(name, superdomain );
        }

        @Override
        public void onJoin(){
            String TAG = "Player::onJoin()";
            activity.player = player;

            register("draw", Card.class, Object.class, player::draw);
            register("pick", Card.class, Card.class, player::pick);

            Log.i("Player", "sub to answering");
            subscribe("answering", Integer.class, String.class, Integer.class,
                    (czarID, questionText, duration) -> {
                        Log.i("answering sub", "received question " + questionText);

                        player.isCzar = (czarID == player.ID);
                        player.question = questionText;
                        player.duration = duration;
                        activity.setQuestion();
                    });

            Log.i("Player", "sub to picking");
            subscribe("picking", String[].class, Integer.class,
                    (answers, duration) -> {
                        Log.i("picking sub", "received answers " + Card.printHand(answers));
                        player.answers = Card.buildHand(answers);
                        player.duration = duration;
                    });

            Log.i("Player", "sub to scoring");
            subscribe("scoring", Player.class, String.class, Integer.class,
                    (winningPlayer, winningCard, duration) -> {
                        Log.i("scoring sub", "winning card " + winningCard);
                        player.winner = winningPlayer;
                        player.winningCard = winningCard;
                        player.duration = duration;
                    });

            Object[] playObject = DANGER_EXEC.play();       // TODO

            if(playObject == null){
                Log.wtf(TAG, "play object is null!");
            }

            player.hand = Card.buildHand( (String[])playObject[0] );
            setDealer((String)playObject[3]);

            Log.i(TAG, "onJoin Finished");
            activity.onPlayerJoined(playObject);
        }

    }
}// end Player class

