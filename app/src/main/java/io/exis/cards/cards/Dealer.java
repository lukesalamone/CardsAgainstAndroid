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
 * Several placeholder methods have been used in place
 * of Riffle methods:
 * sendCard()
 * receiveCard()
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

    private Card questionCard;                              //always know question card

    boolean rating;                                         //pg13 or R
    int dealerID;
    int czarNum;
    int numDummies;

    public Dealer(boolean R, int ID){
        rating = R;
        dealerID = ID;
        czarNum = 0;
        players  = new ArrayList<>();
        inPlay = new ArrayList<>();
        forCzar = new ArrayList<>();
        questions = new ArrayList<>();
        answers = new ArrayList<>();
        numDummies = 0;
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

    /*************************************************************************/
    /*                           PLACEHOLDER METHODS                         */
    /*************************************************************************/

    //when players send cards to dealer
    public void receiveCard(Card card){
        //add card to submitted list
        forCzar.add(card);
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

    /*************************************************************************/
    /*                        END PLACEHOLDER METHODS                        */
    /*************************************************************************/

    //add dummies to make sure there are always 5 players
    public void setPlayers(){
        while(players.size() < ROOMCAP){
            //add dummy player
            DummyPlayer dummy = new DummyPlayer(Exec.getNewID(), null, false, this);
            dummy.setHand(getNewHand(dummy));
            players.add(dummy);
            numDummies++;
        }

        for(int i=0; i<players.size(); i++){
            //give everyone 5 cards
            while(players.get(i).getHand().size() < 5){
                Card newCard = dealCard(players.get(i));
                players.get(i).addCard(newCard);
                inPlay.add(newCard);
            }
        }
    }//end setPlayers function

    public void removeDummy(){
        if(full()){
            return;
        }

        //remove first dummy in players list
        players.remove(players.size() - numDummies);
        numDummies--;
    }//end removeDummy function

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

    public ArrayList<Card> getSubmitted(){
        //get choices from dummies if they aren't czars
        for(int i=(players.size()-numDummies - 1); i<players.size(); i++){
            if(!players.get(i).isCzar()){
                players.get(i).submit();
            }
        }

        return forCzar;
    }

    public boolean addPlayer(Player player){

        //max capacity exceeded
        if(full()){
            return false;
        }
        removeDummy();

        //deal them 5 cards
        for(int i=0; i<5; i++){
            dealCard(player);
        }

        //add as last player in player list (but before dummies)
        //i.e. player, player, new_player, dummy, dummy
        players.add((players.size() - numDummies), player);

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
        Log.i("generateAnswer", "answers has length " + answers.size());
        Collections.shuffle(answers);
        return answers.get(0);
    }//end generateCard method

    /*
     * The dummyPlayer class is used when we need to fill a room with players. They can
     * be removed once a human is found to replace them.
     *
     * Functionality: If player, submit random card. If czar, submit random card.
     */
    private class DummyPlayer extends Player{
        private int playerID;                   //unique to every player
        private ArrayList<Card> hand;           //list of a player's cards
        private boolean isCzar;
        private Dealer dealer;

        public DummyPlayer(int ID, ArrayList<Card> cards, boolean czar, Dealer theDealer){
            super(ID, cards, czar);
            playerID = -1;
            dealer = theDealer;
        }

        @Override
        public void setHand(ArrayList<Card> cards){
            hand = cards;
        }

        @Override
        //submit a random card from hand and deal new card
        public Card submit(){
            Card picked;
            int x = getRandom();
            picked =  hand.get(x);
            hand.remove(x);
            hand.add(dealer.dealCard(this));
            return picked;
        }

        @Override
        public Card czarPicks(ArrayList<Card> cards){
            return cards.get(getRandom());
        }

        //return random number from 0 - 5
        private int getRandom(){
            return (int)(Math.random() * 5);
        }

    }//end DummyPlayer subclass

}//end Dealer class