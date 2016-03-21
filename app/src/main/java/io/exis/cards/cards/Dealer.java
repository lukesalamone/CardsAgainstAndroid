package io.exis.cards.cards;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.os.CountDownTimer;
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

    private ArrayList<Player> players;                      //keep track of players playing
    private ArrayList<Card> forCzar;                        //cards we send to czar

    //keep track of cards not in play
    private ArrayList<Card> questions;
    private ArrayList<Card> answers;
    private String phase;
    private static Player winner;                           //winner
    private static Card winningCard;
    private Card questionCard;                              //always know question card
    private String dealerID;
    int czarNum;
    GameTimer timer;
    RiffleSession session;
    private boolean online;
    private int dummyCount;
    private int playerCount;
    private int duration = 10;                              //

    public Dealer(int ID){
        super("dealer" + ID, new Domain("xs.damouse.CardsAgainst"));
        dealerID = ID + "";
        czarNum = 0;
        players  = new ArrayList<>();
        forCzar = new ArrayList<>();
        questions = MainActivity.getQuestions();
        questionCard = generateQuestion();

        answers = MainActivity.getAnswers();
        online = GameActivity.online;
        dummyCount = 0;
        playerCount = 0;
        phase = "answering";

        //fill room with players
        addDummies();

        Looper.prepare();
        timer = new GameTimer(15000, 1000);
        timer.setType("answering");
        Log.i("dealer", "starting Dealer timer...");
        timer.start();
    }//end Dealer constructor

    // riffle calls
    @Override
    public void onJoin(){
        register("pick", Player.class, String.class, Object.class, this::pick);
        register("left", Player.class, Object.class, this::removePlayer);
        publish("answering", czar().ID(), getQuestion().getText(), 10);
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
        for(int i=0; i<players.size(); i++){
            if(players.get(i).isCzar()){
                players.get(i);
            }
        }
        return null;
    }

    public Card dealCard(Player player){

        Card card = generateAnswer();                       //generate new card to give to player
        card.PID = player.ID();

        if(online) {
            session.draw(player, card);
        }else{
            player.draw(card);                            //add card to player's hand
        }
        return card;
    }//end dealCard method

    public Card drawCard(Player player){
        return dealCard(player);
    }

    public boolean full() {
        if (playerCount == ROOMCAP && players.size() == ROOMCAP){
            return true;
        }else{
            return false;
        }
    }
/*

    public int getCzarPos(){
        for(int i=0; i<players.size(); i++){
            if(players.get(i).isCzar()){
                return i;
            }
        }
        return 0;
    }//end getCzarPos method
*/

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

    public Card getCardFromString(String cardString){
        // iterate over cards in play
        for(Card c: answers){
            if(c.getText().equals(cardString)){
                return c;
            }
        }

        // iterate over cards for czar
        for(Card c : questions){
            if(c.getText().equals(cardString)){
                return c;
            }
        }

        //otherwise return errCard
        return Card.getErrorCard(cardString);
    }

    public int getGameSize(){
        return players.size();
    }//end getGameSize method

    public ArrayList<Card> getNewHand(){
        ArrayList<Card> hand = new ArrayList<>();

        for(int i=0; i<5; i++){
            hand.add(generateAnswer());
        }

        return hand;
    }// end getNewHand method

    public Player getPlayerByID(int PID){

        // dummy player has PID = -1. This should never happen.
        if(PID == -1){
            Log.wtf("Dealer::getPlayerByID", "Error player detected");
            return new Player(-1, null);
        }

        for(int i=0; i<players.size(); i++){
            if(players.get(i).ID() == PID){
                return players.get(i);
            }
        }
        return null;
    }// end getPlayerByID

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
        if(questions == null) {
            questions = MainActivity.getQuestions();                //load all questions
        }
        if(answers == null) {
            answers = MainActivity.getAnswers();                    //load all answers
        }
        Log.i("prepareGame", "questions has size " + questions.size() +
                ", answers has size " + answers.size());
    }

    // add dummies to fill room
    public void addDummies(){
        while(!full() && players.size() < ROOMCAP){
            Player dummy = new Player(Exec.getNewID(), null);
            addPlayer(dummy);
            dummyCount++;
            Log.i("add dummies", "dummy count: " + dummyCount);
        }

        if(!online){
            Log.i("add dummies", "setting dummy as czar");
            players.get(4).setCzar(true);
        }
    }

    //when players send cards to dealer
    public void receiveCard(Card card){
        //add card to submitted list
        forCzar.add(card);
    }

    public Object removePlayer(Player player){
        players.remove(player);
        session.leave();
        return null;
    }//end remove player method

    // deal cards to all players
    public void setPlayers(){
        for(int i=0; i<players.size(); i++){
            //give everyone 5 cards
            while(players.get(i).getHand().size() < 5){
                Card newCard = generateAnswer();
                if(online){
                    session.draw(players.get(i), newCard);
                } else {
                    players.get(i).draw(newCard);
                }
            }
        }
    }//end setPlayers method

    //update czar to next player
    private void updateCzar(){
        players.get(czarNum).setCzar(false);
        czarNum++;
        czarNum = czarNum % getGameSize();
        players.get(czarNum).setCzar(true);
    }//end updateCzar method

    public void start(){



    }//end start method

    public Object[] play(){
        //Returns: string[] cards, Player[] players, string state, string roomName

        return new Object[]{
                Card.handToStrings( getNewHand() ),
                this.getPlayers(),
                phase,
                dealerID};
    }

    /* TODO cannot use player.isCzar on submitted player object!
     *
     * @param   player player that is submitting a card
     * @param   card Card czar has chosen
     */
    public Object pick(Player player, String cardString){

        Card card = getCardFromString(cardString);

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
            winningCard = card;

            //update winner
            for(Player p : players){
                if(card.getPID() == p.ID()){
                    winner = p;
                    session.scoring(winner, cardString, 10);
                    return null;
                }
            }
            winner = null;
        }

        return null;
    }//end pick method

    /*
     * GameTimer handles 3 game phases: answering, picking, and scoring
     *
     * Answering - players submit cards to dealer
     * Picking - Czar picks winner
     * Scoring - Dealer gives point to winner
     *
     */
    private class GameTimer extends CountDownTimer {
        private String type;
        private GameTimer next;
        private long timeRemaining;

        public GameTimer(long duration, long interval){
            super(duration, interval);
        }

        @Override
        public void onFinish(){
            switch (type){
                case "answering": // end of answering phase
                    phase = "picking";

                    if(online) {
                        publish("picking", Card.handToStrings(answers), 10);
                    } else {
                        //pad hand for czar with dummy cards
                        while(forCzar.size() != 5){
                            receiveCard(generateAnswer());
                        }
                    }
                    setNextTimer("picking");
                    break;
                case "picking": // end of picking phase
                    forCzar.clear();
                    updateCzar();
                    questionCard = generateQuestion();              //update question

                    phase = "scoring";
                    if(online){
                        publish("scoring", winner, winningCard.getText(), 10);
                    }
                    setNextTimer("scoring");
                    break;
                case "scoring": // end of scoring phase
                    //TODO give point to winner

                    phase = "answering";
                    if(online){
                        publish("answering", czar(), getQuestion().getText(), 10);
                    }
                    setPlayers();                    // deal cards back to each player
                    setNextTimer("answering");
                    break;
            }// end switch

            next.start();
        }// end onFinish method

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