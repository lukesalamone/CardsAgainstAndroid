package io.exis.cards.cards;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.content.Context;
import java.util.ArrayList;

/**
 * GameActivity.java
 *
 * Manages game screen
 *
 * Created by luke on 10/22/15.
 */
public class GameActivity extends Activity {

    private Context context;
    private boolean adult;
    private Player player;
    private Dealer dealer;
    private Chronometer chronometer;
    private boolean finished;

    TextView card1;
    TextView card2;
    TextView card3;
    TextView card4;
    TextView card5;

    public GameActivity(){
        adult = MainActivity.adult;

        Log.i("GameActivity", "Setting adult to " + adult);

        context = MainActivity.getAppContext();

        player = new Player(
                Exec.getNewID(),
                new ArrayList<Card>(),
                false
        );

        //gets a dealer for the player
        dealer = Exec.findDealer(adult);

        //load questions and answers
        dealer.prepareGame(context);

        //adds player to dealer
        Exec.addPlayer(player, dealer);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.i("onCreate", "Setting views");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        card1 = (TextView) findViewById(R.id.card1);
        card2 = (TextView) findViewById(R.id.card2);
        card3 = (TextView) findViewById(R.id.card3);
        card4 = (TextView) findViewById(R.id.card4);
        card5 = (TextView) findViewById(R.id.card5);

        chronometer = (Chronometer) findViewById(R.id.chronometer);

        //set question TextView
        setQuestion();

        //populate answers TextViews
        showCards();

        playGame();

        //create player and dealer
        player = new Player(
                Exec.getNewID(),
                null,
                false
        );
    }

    private void playGame(){
        player.setCzar(dealer.isCzar(player));
        player.setHand(dealer.getNewHand(player));
        dealer.setPlayers();

        do{
            //draw question card
            setQuestion();

            if(!player.isCzar()){
                Log.i("playGame", "player is not czar");

                //15 second timer for submission
                GameTimer submissionTimer = new GameTimer(15000, 1000);

                //default to submitting first card
                submissionTimer.setChosen(player.getHand().get(0));
                submissionTimer.start();

                while(!submissionTimer.isFinished()){
                    //waiting for player submission
                    Log.i("playGame", "inside submission timer");
                }

                //deal another card back to player
                Card freshCard = dealer.dealCard(player);
                player.addCard(freshCard);

                dummyTimer();
            }//end submission case

            if(player.isCzar()) {
                Log.i("playGame", "player is czar");
                dummyTimer();
                final ArrayList<Card> submitted = dealer.getSubmitted();

                GameTimer czarTimer = new GameTimer(15000, 1000);
                czarTimer.setChosen(submitted.get(0));
                czarTimer.start();

                while(!czarTimer.isFinished()){

                }

            }//end czar case

            Exec.addPoint(player);                          //give point to winner
            player.setCzar(dealer.isCzar(player));          //update whether player is czar
        }while(true);
    }//end playGame method

    //creates and runs 15 second waiting timer
    private void dummyTimer(){
        GameTimer dummy = new GameTimer(15000, 1000);
        dummy.setType(true);
        dummy.start();
        while(!dummy.isFinished()){
            Log.i("playGame", "Dummy timer onTick");
        }
    }

    private void setQuestion(){
        Log.i("setQuestion", "Entering setQuestion() method");

        //Card card = riffle.getQuestion();
        Card card = dealer.getQuestion();

        String questionText = card.getText();
        TextView textView = (TextView) findViewById(R.id.question);
        textView.setText(questionText);
    }//end setQuestion method

    private void showCards(){
        //ArrayList<Card> hand = riffle.getHand(player.getPlayerID());
        ArrayList<Card> hand = player.getHand();

        //change card texts to text of cards in hand
        for(int i=0; i<5; i++){
            String str = "card" + (i + 1);
            int resID = context.getResources().getIdentifier(str,
                    "id", context.getPackageName());
            TextView view = (TextView) findViewById(resID);
            view.setText(hand.get(i).getText());
        }
    }//end setAnswers method

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //riffle.leave(player);

        dealer.removePlayer(player);
    }

    public void submitCard1(View view){
        dealer.receiveCard(player, player.getHand().get(0));

        //set background colors
        setBackgrounds(1, view);
    }

    public void submitCard2(View view){
        dealer.receiveCard(player, player.getHand().get(1));
        setBackgrounds(2, view);
    }

    public void submitCard3(View view){
        dealer.receiveCard(player, player.getHand().get(2));
        setBackgrounds(3, view);
    }

    public void submitCard4(View view){
        dealer.receiveCard(player, player.getHand().get(3));
        setBackgrounds(4, view);
    }

    public void submitCard5(View view){
        dealer.receiveCard(player, player.getHand().get(4));
        setBackgrounds(5, view);
    }

    //whiten card backgrounds other than card c
    private void setBackgrounds(int c, View v){
        v.getBackground().setColorFilter(Color.parseColor("#e5ffff"), PorterDuff.Mode.DARKEN);

        String str;
        for(int i=1; i<=5; i++){
            str = "card" + i;
            if(i != c){//if i != c change background to white
                int resID = context.getResources().getIdentifier(str, "id",
                        context.getPackageName());
                TextView view = (TextView) findViewById(resID);
                view.getBackground().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.LIGHTEN);
            }
        }
    }//end setBackgrounds method

    public class GameTimer extends CountDownTimer{
        private String s;
        private boolean waiting;                        //allows us to create dummy timer
        private boolean finished;                       //whether the timer is finished
        private Card chosen;

        public GameTimer(long startTime, long interval){
            super(startTime, interval);
            finished = false;
            waiting = false;
        }

        @Override
        public void onFinish(){
            finished = true;

            if(!waiting){
                if(player.isCzar()){
                    //submit chosen card
                    Card newCard = dealer.receiveCard(player, chosen);
                    player.addCard(newCard);
                }else{
                    //submit chosen card
                    dealer.czarPick(chosen);
                }
            }
        }

        @Override
        public void onTick(long millisUntilFinished){
            long timeRemaining = (millisUntilFinished / 1000);

            if(!waiting){
                //my amazing interface
                s = timeRemaining + " seconds remain to choose!";
                chronometer.setText(s);

                if (timeRemaining > 5) {
                    chronometer.getBackground().setColorFilter(Color.parseColor("#009900"), PorterDuff.Mode.DARKEN);
                } else {
                    chronometer.getBackground().setColorFilter(Color.parseColor("#ff6600"), PorterDuff.Mode.DARKEN);
                }
            }
        }

        public void setType(boolean isWaiting){
            waiting = isWaiting;
        }

        public void setChosen(Card card){
            chosen = card;
        }

        public boolean isFinished(){
            return finished;
        }
    }//end GameTimer subclass
}//end GameActivity class
