package io.exis.cards.cards;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
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
    private Player dummy;                                   //dummy player for dealing dummy cards
    private Card questionCard;                              //always know question card
    boolean rating;                                         //pg13 or R
    int dealerID;
    int czarNum;
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
        dummy = new Player(-1, null, false);
        session = new RiffleSession();
        URL = session.getDomain();

        //TODO register all calls with WAMPWrapper
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

        /*  Micky's riffle calls

        session.register("play");
        session.register("pick");
        session.register("leave");
        session.register("answering");
        session.register("picking");
        session.register("scoring");
        session.register("left");
        session.register("joined");
        session.register("draw");

         */
    }

    public void prepareGame(){
        questions = MainActivity.getQuestions();                //load all questions
        answers = MainActivity.getAnswers();                    //load all answers
        Log.i("prepareGame", "questions has size " + questions.size() +
                ", answers has size " + answers.size());

        //add dummies to fill room

    }

    public Card dealCard(Player player){
        Card card = generateAnswer();                       //generate new card to give to player
        card.PID = player.getPlayerID();
        answers.remove(card);                               //remove card from deck
        player.addCard(card);                               //add card to player's hand
        inPlay.add(card);                                   //add card to cards in play
        return card;
    }//end dealCard method

    public boolean full(){
        return (getGameSize() + 1 > this.ROOMCAP);
    }

    //Overloaded for damouse
    public String dealCard(){
        Card card = generateAnswer();
        inPlay.add(card);
        return card.getText();
    }

    //when players send cards to dealer
    public void receiveCard(Card card){
        //add card to submitted list
        forCzar.add(card);
    }

    //need to overload for czar situation
    public void czarPick(Card card){
        //can't give a dummy a point!
        if(card.getPID() != -1){
            //give winner a point
            Player winner = getPlayerByID(card.getPID());
            Exec.addPoint(winner);
        }

        //set czar to next player
        players.get(czarNum).setCzar(false);
        czarNum++;
        czarNum = czarNum % getGameSize();

        //pub here
        players.get(czarNum).setCzar(true);
    }

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
    }//end setPlayers function

    public ArrayList<Card> getNewHand(Player player){
        ArrayList<Card> hand = new ArrayList<>();

        for(int i=0; i<5; i++){
            hand.add(dealCard(player));
        }

        return hand;
    }//end getNewHand method

    public Card getQuestion(){
        if(questionCard == null){
            questionCard = generateQuestion();
        }
        return questionCard;
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

    public int getGameSize(){
        return players.size();
    }//end getGameSize method

    public boolean getRating(){
        return this.rating;
    }

    public void removePlayer(Player player){
        players.remove(player);
    }//end remove player method

    public int getCzarPos(){
        for(int i=0; i<players.size(); i++){
            if(players.get(i).isCzar()){
                return i;
            }
        }
        return 0;
    }//end getCzarPos method

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

    public boolean isCzar(Player player){
        for(Player iterator : players){
            if(iterator.getPlayerID() == player.getPlayerID()){
                return iterator.isCzar();
            }
        }
        //hopefully we found the player, but...
        return false;
    }//end isCzar method

    private Card generateQuestion(){
        Collections.shuffle(questions);
        return questions.get(0);
    }//end generateCard method

    private Card generateAnswer(){
        Collections.shuffle(answers);
        return answers.get(0);
    }//end generateCard method

    //Not a fan
    public Player[] getPlayers(){
        return players.toArray(new Player[players.size()]);
    }//

}//end Dealer class