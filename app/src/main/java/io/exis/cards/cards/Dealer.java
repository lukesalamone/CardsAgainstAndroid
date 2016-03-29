package io.exis.cards.cards;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.exis.riffle.Domain;
import com.exis.riffle.Riffle;

/**
 * Dealer.java
 * Manages decks and player points
 * May report corrupted players
 *
 * Created by luke on 10/13/15.
 */
public class Dealer extends Domain{

    final int ROOMCAP = 5;

    private ArrayList<Player> players;                      // keep track of players playing
    private ArrayList<Card> answers;                        // cards sent to czar

    //keep track of cards not in play
    private static ArrayList<Card> questionDeck;
    private ArrayList<Card> answerDeck;
    private String phase;
    private static Player winner;                           // winner
    private static Card winningCard;
    private Card questionCard;                              // always know question card
    private String dealerID;
    int czarNum;
    GameTimer timer;
    RiffleSession session;
    private boolean online;
    private int dummyCount;
    private int playerCount;
    private int duration;

    public Dealer(int ID){
        super("dealer" + ID, new Domain("xs.damouse.CardsAgainst"));
        session = new RiffleSession(this);
        dealerID = ID + "";
        czarNum = 0;
        players  = new ArrayList<>();
        answerDeck = MainActivity.getAnswers();
        questionDeck = MainActivity.getQuestions();
        questionCard = generateQuestion();


        answers = new ArrayList<>();
        online = GameActivity.online;
        dummyCount = 0;
        playerCount = 0;
        duration = 10;
        phase = "answering";

        //fill room with players
        addDummies();
        updateCzar();

        Looper.prepare();
//        timer = new GameTimer(15000, 1000);
    }//end Dealer constructor

    // riffle calls
    @Override
    public void onJoin(){
        // TODO register methods joined and closed
        register("leave", Player.class, Object.class, this::leave);

        subscribe("picked", Card.class, (c)->{
            Log.i("picked listener", "received card " + c.getText());
            answers.add(c);
        });

        // pub: current czar, current question & round duration
        publish("answering", players.get(czarNum).ID(), getQuestion().getText(), 10);
    }

    public String ID(){
        return this.dealerID;
    }

    public void addPlayer(Player player){
        //if max capacity exceeded
        if(full()){
            return;
        }

        //deal them 5 cards
        for(int i=0; i<5; i++){
            dealCard(player);
        }

        playerCount++;
        players.add(player);
    }//end addPlayer method

    // returns current czar
    private Player czar(){
        return players.get(czarNum);
    }

    public Card dealCard(Player player){

        Card card = generateAnswer();                       //generate new card to give to player

        if(online && !player.dummy) {
            session.draw(card);
        }else{
            player.draw(card);                            //add card to player's hand
        }
        return card;
    }//end dealCard method

    public boolean full() {
        if (playerCount == ROOMCAP && players.size() == ROOMCAP){
            return true;
        }else{
            return false;
        }
    }

    public static Card generateQuestion(){
        Collections.shuffle(questionDeck);
        return questionDeck.get(0);
    }//end generateCard method

    private Card generateAnswer(){
        Collections.shuffle(answerDeck);
        return answerDeck.get(0);
    }//end generateCard method

    public ArrayList<Card> getNewHand(){
        ArrayList<Card> hand = new ArrayList<>();

        for(int i=0; i<5; i++){
            hand.add(generateAnswer());
        }

        return hand;
    }// end getNewHand method

    // Not a fan of this method
    public Player[] getPlayers(){
        return players.toArray(new Player[players.size()]);
    }//end getPlayers method

    public Card getQuestion(){
        if(questionCard == null){
            questionCard = generateQuestion();
        }
        return questionCard;
    }

    //used for keeping GameActivity timer synchronized
    public long getTimeRemainingInPhase(){
        return timer.getTimeRemaining();
    }//end getTimeRemainingInPhase method

    public Player getWinner(){
        return winner;
    }//end getWinner method

    public static Card getWinningCard(){
        return winningCard;
    }

    public boolean isCzar(Player player){
        for(Player p : players){
            if(p.ID() == player.ID()){
                return p.isCzar();
            }
        }
        //hopefully we found the player, but...
        return false;
    }//end isCzar method

    public void prepareGame(){
        if(questionDeck == null) {
            questionDeck = MainActivity.getQuestions();                //load all questions
        }
        if(answerDeck == null) {
            answerDeck = MainActivity.getAnswers();                    //load all answers
        }
        Log.i("prepareGame", "questions has size " + questionDeck.size() +
                ", answers has size " + answerDeck.size());
    }

    // add dummies to fill room
    public void addDummies(){
        while(players.size() < ROOMCAP){
            addPlayer(new Player());
            dummyCount++;
            Log.i("add dummies", "dummy count: " + dummyCount);
        }

        if(!online){
            Log.i("add dummies", "setting dummy as czar");
            players.get(4).setCzar(true);
        }
    }

    public Object leave(Player player){
        players.remove(player);
        return null;
    }//end remove player method

    // deal cards to all players
    public void setPlayers(){
        for(int i=0; i<players.size(); i++){
            //give everyone 5 cards
            while(players.get(i).hand().size() < 5){
                dealCard(players.get(i));
            }
        }
    }//end setPlayers method

    // TODO this picks a random player for now
    private void setWinner(){
        int num = (int) (Math.random()*5);

        if(!players.get(num).isCzar()){
            winner = players.get(num);
        }else{
            setWinner();
        }
    }

    //update czar to next player
    private void updateCzar(){
        players.get(czarNum).setCzar(false);
        czarNum++;
        czarNum = czarNum % players.size();
        players.get(czarNum).setCzar(true);
    }//end updateCzar method

    /* @param   player player that is submitting a card
     * @param   card Card czar has chosen
     */
    private void pick(Player player, Card card){
        if(online){
            // call player::pick
            session.pick(card);
        }else{
            player.pick(card);
        }
    }//end pick method

    public Object[] play(){
        //Returns: string[] cards, Player[] players, string state, string roomName

        return new Object[]{
                Card.handToStrings( getNewHand() ),
                this.getPlayers(),
                phase,
                dealerID};
    }

    public void start(){
        Log.i("dealer", "entered start()");
        Handler handler = new Handler();
        int delay = 15000;

        handler.postDelayed(new Runnable(){
            public void run(){
                Log.i("dealer", "starting " + phase + " phase");
                playGame(phase);
                handler.postDelayed(this, delay);
            }
        }, delay);
    }//end start method

    /*
     * Main game logic.
     *
     * Answering - players submit cards to dealer
     * Picking - Czar picks winner
     * Scoring - Dealer announces winner
     *
     */
    private void playGame(String type){
        String TAG = "playGame";
        switch(type){
            case "answering":
                if(online){
                    Log.i(TAG, "publishing [answering, " +
                            czar().playerID() + ", " +
                            getQuestion().getText() + ", " +
                            duration + "]");
                    publish("answering", czar(), getQuestion().getText(), duration);
                }

                setPlayers();                    // deal cards back to each player
                phase = "picking";
                break;
            case "picking":
                // pad pile for czar
                while(answers.size() != 5){
                    answers.add(generateAnswer());
                }

                if(online) {
                    Log.i(TAG, "publishing [picking, " +
                            Card.printHand(answers) + ", " +
                            duration + "]");
                    publish("picking", Card.handToStrings(answers), duration);
                }

                phase = "scoring";
                break;
            case "scoring":
                answers.clear();
                updateCzar();
                questionCard = generateQuestion();              //update question
                setWinner();

                if(online){
                    Log.i(TAG, "publishing [scoring, " +
                            winner + ", " +
                            winningCard.getText() + ", " +
                            duration + "]");
                    publish("scoring", winner, winningCard.getText(), duration);
                }

                phase = "answering";
                break;
        }
    }// end playGame method




    /*
     * GameTimer handles 3 game phases: answering, picking, and scoring
     *
     * Answering - players submit cards to dealer
     * Picking - Czar picks winner
     * Scoring - Dealer gives point to winner
     *
     */
    private class GameTimer extends CountDownTimer {
        String type;
        private GameTimer next;
        private long timeRemaining;
        String TAG = "Dealer";

        public GameTimer(long duration, long interval){
            super(duration, interval);
            Log.i(TAG, "Dealer timer init");
        }

        @Override
        public void onFinish(){
            switch (type){
                case "answering": // next phase will be picking
                    phase = "picking";

                    // pad pile for czar
                    while(answers.size() != 5){
                        answers.add(generateAnswer());
                    }

                    if(online) {
                        Log.i(TAG, "publishing [picking, " +
                                Card.printHand(answers) + ", " +
                                duration + "]");
                        publish("picking", Card.handToStrings(answers), duration);
                    }

                    setNextTimer("picking");
                    break;
                case "picking": // next phase will be scoring
                    answers.clear();
                    updateCzar();
                    questionCard = generateQuestion();              //update question

                    phase = "scoring";
                    setWinner();
                    if(online){
                        Log.i(TAG, "publishing [scoring, " +
                                winner + ", " +
                                winningCard.getText() + ", " +
                                duration + "]");
                        publish("scoring", winner, winningCard.getText(), duration);
                    }
                    setNextTimer("scoring");
                    break;
                case "scoring": // next phase will be answering
                    phase = "answering";

                    if(online){
                        Log.i(TAG, "publishing [answering, " +
                                czar().playerID() + ", " +
                                getQuestion().getText() + ", " +
                                duration + "]");
                        publish("answering", czar(), getQuestion().getText(), duration);
                    }
                    setPlayers();                    // deal cards back to each player
                    setNextTimer("answering");
                    break;
            }// end switch

            next.start();
        }// end onFinish method

        @Override
        public void onTick(long millisUntilFinished){
            Log.i(TAG, "time remaining: " + timeRemaining);
            timeRemaining = millisUntilFinished;
            publish("tick", (int) (timeRemaining/1000));
        }//end onTick method

        public long getTimeRemaining(){
            return timeRemaining;
        }

        private void setNextTimer(String nextType){
            GameTimer nextTimer = new GameTimer(15000, 1000);
            nextTimer.setType(nextType);
            next = nextTimer;
        }

        public void setType(String timerType){
            type = timerType;
        }
    }//end GameTimer subclass

}//end Dealer class