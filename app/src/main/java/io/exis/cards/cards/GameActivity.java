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
    public Player player;
    public Dealer dealer;
    public Chronometer chronometer;

    TextView card1;
    TextView card2;
    TextView card3;
    TextView card4;
    TextView card5;

    //public RiffleSession riffle;

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

        //find game & join
        //riffle.join();

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

        while(true){
            //draw question card
            setQuestion();

            if(!player.isCzar()){
                //15 second timer for submission
                CountDownTimer timer = new CountDownTimer(15000, 1000) {
                    Card chosen = player.getHand().get(0);      //default to submitting first card
                    String s;

                    public void onTick(long millisUntilFinished) {
                        long timeRemaining = (millisUntilFinished / 1000);

                        //interface stuff
                        s = timeRemaining + " seconds remain to choose!";
                        chronometer.setText(s);

                        if(timeRemaining > 5){
                            chronometer.getBackground().setColorFilter(Color.parseColor("#009900"), PorterDuff.Mode.DARKEN);
                        } else {
                            chronometer.getBackground().setColorFilter(Color.parseColor("#ff6600"), PorterDuff.Mode.DARKEN);
                        }
                    }//end onTick method

                    public void onFinish() {
                        //submit chosen card
                        Card newCard = dealer.receiveCard(player, chosen);
                        player.getHand().add(newCard);
                    }
                };

                //start the submission timer
                timer.start();

                //deal another card back to player
                Card freshCard = dealer.dealCard(player);
                player.hand.add(freshCard);
            }//end submission timer



            final ArrayList<Card> submitted = dealer.getSubmitted();

            if(player.isCzar()) {
                //15 second timer for czar
                CountDownTimer czarTimer = new CountDownTimer(15000, 1000) {
                    Card chosen = submitted.get(0);             //default to submitting first card
                    String s;

                    public void onTick(long millisUntilFinished) {
                        long timeRemaining = (millisUntilFinished / 1000);

                        //my amazing interface
                        s = timeRemaining + " seconds remain to choose!";
                        chronometer.setText(s);

                        if (timeRemaining > 5) {
                            chronometer.getBackground().setColorFilter(Color.parseColor("#009900"), PorterDuff.Mode.DARKEN);
                        } else {
                            chronometer.getBackground().setColorFilter(Color.parseColor("#ff6600"), PorterDuff.Mode.DARKEN);
                        }

                        //OnClick listener for card submissions
                    }

                    public void onFinish() {
                        //submit chosen card
                        if (player.isCzar()) {
                            dealer.czarPick(chosen);
                        }

                        Card newCard = dealer.receiveCard(player, chosen);
                        player.getHand().add(newCard);
                    }
                };//end czarTimer

                //start the timer
                czarTimer.start();
            }//end

            if(!player.isCzar()) {
                //TODO set a dummy timer here
            }

            //give point to winner
            Exec.addPoint(player);

            player.setCzar(dealer.isCzar(player));          //update whether player is czar

        }//end game loop
    }//end playGame method

    private void setQuestion(){
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
}
