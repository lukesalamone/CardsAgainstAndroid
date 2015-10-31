package io.exis.cards.cards;

import java.util.ArrayList;
import java.util.Collections;

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
    ArrayList<Player> players = new ArrayList<>();

    //keep track of cards in play
    ArrayList<Card> inPlay = new ArrayList<>();

    //keep track of cards not in play
    ArrayList<Card> questions = new ArrayList<>();
    ArrayList<Card> answers = new ArrayList<>();

    RiffleSession riffle = new RiffleSession();

    //always know question card
    Card questionCard;

    //pg13 or R
    boolean rating;
    int dealerID;

    //seconds remaining
    long timeRemaining;

    CountDownTimer timer = new CountDownTimer(15000, 1000) {

        public void onTick(long millisUntilFinished) {
            timeRemaining = (millisUntilFinished / 1000);
        }

        public void onFinish() {
            //Do something...
        }
    };

    public Dealer(boolean R, int ID){
        rating = R;
        dealerID = ID;
        beginGame();
    }

    public void beginGame(){

        //load all questions into questions ArrayList
        questions = Card.getQuestions(rating);

        //load all answers into answers ArrayList
        answers = Card.getAnswers(rating);

        //set first player as czar
        int czarNum = getCzarPos();
        players.get(czarNum).setCzar(true);

        //game continues until taken down by exec
        while(true) {
            //make sure all players have 5 cards
            for(int i=0; i<players.size(); i++){
                while(players.get(i).hand.size() < 5){
                    Card newCard = dealCard(players.get(i));
                    players.get(i).hand.add(newCard);
                    inPlay.add(newCard);
                }
            }

            //set question card
            questionCard = generateQuestion();

            //players submit cards
            timer.start();
            while(getTimeRemaining() != 0){
                ArrayList<Card> submitted = new ArrayList<>();
                //Card card = riffle.receiveCard();

                Card card = receiveCard(generateAnswer());
                submitted.add(card);

                //send card to czar
                //riffle.sendCard(players.get(czarNum).getPlayerID(), card);

                sendCard(players.get(czarNum).getPlayerID(), card);
            }

            //czar picks card
            timer.start();

            Card picked = null;

            //announce winner & give a point
            while(getTimeRemaining() != 0){
                picked = receiveCard(generateAnswer());
            }

            //give that player a point
            if (picked != null){
                Player winner = getPlayerByID(picked.getPID());
                Exec.addPoint(winner);
            }

            //set czar to next player
            players.get(czarNum).setCzar(false);
            czarNum++;
            czarNum = czarNum % getGameSize();
            players.get(czarNum).setCzar(true);
        }
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

    //placeholder method for now...
    public Card receiveCard(Card card){
        return card;
    }

    //placeholder method for now...
    //send card to player
    public void sendCard(int PID, Card card){

    }

    /*************************************************************************/
    /*                        END PLACEHOLDER METHODS                        */
    /*************************************************************************/

    public Card getQuestion(){
        return questionCard;
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
