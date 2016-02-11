package io.exis.cards.cards;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

/**
 * Dealer.java
 * Manages decks and player points
 * May report corrupted players
 *
 * Created by luke on 10/13/15.
 */
public class Dealer {

    final int ROOMCAP = 5;

    private ArrayList<Player> players;                      //keep track of players playing
    private ArrayList<Card> inPlay;                         //keep track of cards in play
    private ArrayList<Card> forCzar;                        //cards we send to czar

    //keep track of cards not in play
    private ArrayList<Card> questions;
    private ArrayList<Card> answers;
    private String phase;
    private Player winner;                                   //winner
    private Card questionCard;                              //always know question card
    private boolean rating;                                         //pg13 or R
    int dealerID;
    int czarNum;
    GameTimer timer;
    RiffleSession session;
    String URL;

    public Dealer(boolean R, int ID){
        rating = R;
        dealerID = ID;
        czarNum = 0;
        players  = new ArrayList<>();
        inPlay = new ArrayList<>();
        forCzar = new ArrayList<>();
        questions = new ArrayList<>();
        answers = new ArrayList<>();
        session = new RiffleSession("ws://ec2-52-26-83-61.us-west-2.compute.amazonaws.com:8000/ws");
        //URL = session.getDomain();
        phase = "answering";

        //TODO register all calls with WAMPWrapper
        if(GameActivity.online) {
            session.register("receiveCard");
            session.register("dealCard");
            session.register("czarPick");
            session.register("isCzar");
            session.register("prepareGame");
            session.register("setPlayers");
            session.register("getNewHand");
            session.register("getSubmitted");
            session.register("getQuestion");
            session.register("removePlayer");
            session.register("receiveCard");
            session.register("addPlayer");

            //Exec call registers
            session.register("findDealer");
            session.register("getNewID");
            session.register("addPoint");

            //damouse's riffle calls

            session.register("play");
            session.register("pick");
            session.register("leave");
            session.register("answering");
            session.register("picking");
            session.register("scoring");
            session.register("left");
            session.register("joined");
            session.register("draw");
        }
    }//end Dealer constructor

    public boolean addPlayer(Player player){
        //if max capacity exceeded
        if(full()){
            return false;
        }

        //deal them 5 cards
        for(int i=0; i<5; i++){
            dealCard(player);
        }

        players.add(player);

        return true;
    }//end addPlayer method

    //need to overload for czar situation
    public void czarPick(Card card){
        //can't give a dummy a point!
        if(card.getPID() != -1){
            //give winner a point
            Player winner = getPlayerByID(card.getPID());
            Exec.addPoint(winner);
        }

        updateCzar();
    }

    public Card dealCard(Player player){
        Card card = generateAnswer();                       //generate new card to give to player
        card.PID = player.getPlayerID();
        answers.remove(card);                               //remove card from deck
        player.addCard(card);                               //add card to player's hand
        inPlay.add(card);                                   //add card to cards in play
        return card;
    }//end dealCard method

    //Overloaded for damouse
    public String dealCard(){
        Card card = generateAnswer();
        inPlay.add(card);
        return card.getText();
    }

    public Card drawCard(Player player){
        return dealCard(player);
    }

    public boolean full(){
        return (getGameSize() + 1 > this.ROOMCAP);
    }

    public int getCzarPos(){
        for(int i=0; i<players.size(); i++){
            if(players.get(i).isCzar()){
                return i;
            }
        }
        return 0;
    }//end getCzarPos method

    private Card generateQuestion(){
        Collections.shuffle(questions);
        return questions.get(0);
    }//end generateCard method

    private Card generateAnswer(){
        Collections.shuffle(answers);
        return answers.get(0);
    }//end generateCard method

    //return cards for czar to pick from
    public ArrayList<Card> getCardsForCzar(){
        return forCzar;
    }//end getCardsForCzar method

    public int getGameSize(){
        return players.size();
    }//end getGameSize method

    public int getID(){
        return dealerID;
    }

    public ArrayList<Card> getNewHand(Player player){
        ArrayList<Card> hand = new ArrayList<>();

        for(int i=0; i<5; i++){
            hand.add(dealCard(player));
        }

        return hand;
    }//end getNewHand method

    //returns phase of gameTimer
    public String getPhase(){
        return phase;
    }

    public Player getPlayerByID(int PID){
        //dummy player has PID = -1. This should never happen.
        if(PID == -1){
            return new Player(-1, null, false);
        }

        for(int i=0; i<players.size(); i++){
            if(players.get(i).getPlayerID() == PID){
                return players.get(i);
            }
        }
        return null;
    }//end getPlayerByID

    //Not a fan of this method
    public Player[] getPlayers(){
        return players.toArray(new Player[players.size()]);
    }//end getPlayers method

    public Card getQuestion(){
        if(questionCard == null){
            questionCard = generateQuestion();
        }
        return questionCard;
    }

    public boolean getRating(){
        return this.rating;
    }

    //return a list of cards for czar to pick from
    //adds dummy cards to
    public ArrayList<Card> getSubmitted(){
        //deal dummy cards until pile forCzar is 5 cards
        while(forCzar.size() < 5){
            forCzar.add( generateAnswer() );
        }

        return forCzar;
    }

    //used for keeping GameActivity timer synchronized
    public long getTimeRemainingInPhase(){
        return timer.getTimeRemaining();
    }//end getTimeRemainingInPhase method

    public Player getWinner(){
        return winner;
    }//end getWinner method

    public boolean isCzar(Player player){
        for(Player iterator : players){
            if(iterator.getPlayerID() == player.getPlayerID()){
                return iterator.isCzar();
            }
        }
        //hopefully we found the player, but...
        return false;
    }//end isCzar method

    public void prepareGame(){
        questions = MainActivity.getQuestions();                //load all questions
        answers = MainActivity.getAnswers();                    //load all answers
        Log.i("prepareGame", "questions has size " + questions.size() +
                ", answers has size " + answers.size());

        //add dummies to fill room

    }

    //when players send cards to dealer
    public void receiveCard(Card card){
        //add card to submitted list
        forCzar.add(card);
    }

    public void removePlayer(Player player){
        players.remove(player);
    }//end remove player method

    //deal cards to all players
    public void setPlayers(){
        for(int i=0; i<players.size(); i++){
            //give everyone 5 cards
            while(players.get(i).getHand().size() < 5){
                Card newCard = dealCard(players.get(i));
                players.get(i).addCard(newCard);
                inPlay.add(newCard);
            }
        }
    }//end setPlayers method

    //update czar to next player
    private void updateCzar(){
        //set czar to next player
        players.get(czarNum).setCzar(false);
        czarNum++;
        czarNum = czarNum % getGameSize();

        //pub here
        players.get(czarNum).setCzar(true);
    }//end updateCzar method

    public void start(){
        timer = new GameTimer(15000, 1000);
        timer.setType("answering");
        timer.start();
    }//end start method


    public void pick(Player player, Card card){
        //czar can't submit during answering phase
        if(phase.equals("answering") && !player.isCzar()){
            //can't submit 2 cards in a round
            int i = 0;
            for(Card c : forCzar){
                if(c.getPID() == card.getPID()){
                    forCzar.remove(i);
                    break;
                }
                i++;
            }
            forCzar.add(card);
        } else if(phase.equals("picking") && player.isCzar()){
            //update winner
            for(Player p : players){
                if(card.getPID() == p.getPlayerID()){
                    winner = p;
                    //TODO pub winner
                    return;
                }
            }
            winner = null;
        }
    }//end pick method

    /*
     * GameTimer handles 3 game phases: answering, picking, and scoring
     *
     * Answering - players submit cards to dealer
     * Picking - Czar picks winner
     * Scoring - Dealer gives point to winner
     *
     */
    public class GameTimer extends CountDownTimer {
        private String type;
        private GameTimer next;
        private long timeRemaining;

        public GameTimer(long duration, long interval){
            super(duration, interval);
        }

        @Override
        public void onFinish(){
            switch (type){
                case "answering":
                    phase = "picking";
                    setNextTimer("picking");
                    break;
                case "picking":
                    forCzar.clear();
                    updateCzar();
                    questionCard = generateQuestion();              //update question
                    phase = "scoring";
                    setNextTimer("scoring");
                    break;
                case "scoring":
                    //give point to winner

                    phase = "answering";
                    setNextTimer("answering");
                    break;
            }

            next.start();
        }

        @Override
        public void onTick(long millisUntilFinished){
            timeRemaining = millisUntilFinished;
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