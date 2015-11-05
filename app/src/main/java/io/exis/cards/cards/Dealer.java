package io.exis.cards.cards;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.os.CountDownTimer;

/**
 * Dealer.java
 * Manages decks and player points
 * May report corrupted players
 *
 * Several placeholder methods have been used in place
 * of Riffle methods:
 * sendCard()
 * receiveCard()
 *
 * Created by luke on 10/13/15.
 */
public class Dealer {

    final int ROOMCAP = 6;

    //keep track of players playing
    ArrayList<Player> players;

    //keep track of cards in play
    ArrayList<Card> inPlay;

    ArrayList<Card> forCzar;

    //keep track of cards not in play
    ArrayList<Card> questions;
    ArrayList<Card> answers;

    //RiffleSession riffle = new RiffleSession();

    //always know question card
    Card questionCard;

    boolean rating;             //pg13 or R
    int dealerID;
    int czarNum;

    Context context;

    //seconds remaining
    long timeRemaining;

    public Dealer(boolean R, int ID){
        rating = R;
        dealerID = ID;
        czarNum = 0;
        players  = new ArrayList<>();
        inPlay = new ArrayList<>();
        forCzar = new ArrayList<>();
        questions = new ArrayList<>();
        answers = new ArrayList<>();
    }

    public void setContext(Context c){
        context = c;
    }

    public void prepareGame(Context c){
        context = c;                                            //I hate this line
        questions = MainActivity.getQuestions();                //load all questions
        answers = MainActivity.getAnswers();                      //load all answers
    }

    public Card dealCard(Player player){

        //generate new card to give to player
        Card card = generateAnswer();

        card.PID = player.getPlayerID();

        //remove card from deck
        answers.remove(card);

        //add card to player's hand
        player.hand.add(card);

        //add card to cards in play
        inPlay.add(card);

        //send card to player
        sendCard(player.getPlayerID(), card);

        return card;

    }//end dealCard method

    public boolean full(){
        return (getGameSize() + 1 > this.ROOMCAP);
    }

    /*************************************************************************/
    /*                           PLACEHOLDER METHODS                         */
    /*************************************************************************/

    //when players send cards to dealer
    public Card receiveCard(Player player, Card card){
        //add card to submitted list
        forCzar.add(card);

        return dealCard(player);
    }

    //need to overload for czar situation
    public void czarPick(Card card){
        //give winner a point
        Player winner = getPlayerByID(card.getPID());
        Exec.addPoint(winner);

        //set czar to next player
        players.get(czarNum).setCzar(false);
        czarNum++;
        czarNum = czarNum % getGameSize();
        players.get(czarNum).setCzar(true);
    }

    //placeholder method for now...
    //send card to player
    public void sendCard(int PID, Card card){

    }

    /*************************************************************************/
    /*                        END PLACEHOLDER METHODS                        */
    /*************************************************************************/

    //make sure all players have 5 cards
    public void setPlayers(){
        for(int i=0; i<players.size(); i++){
            while(players.get(i).hand.size() < 5){
                Card newCard = dealCard(players.get(i));
                players.get(i).hand.add(newCard);
                inPlay.add(newCard);
            }
        }
    }

    public ArrayList<Card> getNewHand(Player player){
        ArrayList<Card> hand = new ArrayList<>();

        for(int i=0; i<5; i++){
            hand.add(this.dealCard(player));
        }

        return hand;
    }//end getNewHand method

    public Card getQuestion(){
        return questionCard;
    }

    public ArrayList<Card> getSubmitted(){
        return forCzar;
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

    public long getTimeRemaining(){
        return timeRemaining;
    }

    public int getCzarPos(){
        for(int i=0; i<players.size(); i++){
            if(players.get(i).isCzar()){
                return i;
            }
        }
        return 0;
    }//end getCzarPos method

    public Player getPlayerByID(int PID){
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

}//end Dealer class
