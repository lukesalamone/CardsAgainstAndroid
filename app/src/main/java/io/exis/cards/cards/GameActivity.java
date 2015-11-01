package io.exis.cards.cards;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Button;
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
public class GameActivity extends AppCompatActivity {

    private Context context;
    private boolean adult;
    public Player player;
    public Dealer dealer;
    public Chronometer chronometer;

    //public RiffleSession riffle;

    public GameActivity(Context context){
        /*riffle = new RiffleSession();
        player = riffle.addPlayer();

        //ask dealer if player is czar, set appropriately
        player.setCzar(riffle.isCzar(player.getPlayerID()));*/
        adult = MainActivity.adult;
    }

    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //find game & join
        //riffle.join();

        //set question TextView
        setQuestion();

        //populate answers TextViews
        showCards();

        dealer.beginGame();
        playGame();

        //create player and dealer
        player = new Player(
                Exec.getNewID(),
                null,
                false
        );
    }

    private void playGame(){
        dealer = Exec.addPlayer(player, adult);
        player.setCzar(dealer.isCzar(player));
        player.setHand(dealer.getNewHand(player));
        dealer.setPlayers();

        while(true){
            //draw question card
            setQuestion();

            //15 second timer for submission
            CountDownTimer timer = new CountDownTimer(15000, 1000) {
                Card chosen = player.getHand().get(0);      //default to submitting first card

                public void onTick(long millisUntilFinished) {
                    long timeRemaining = (millisUntilFinished / 1000);

                    //probably should do some interface things here

                    //OnClick listener for card submissions
                }

                public void onFinish() {
                    //submit chosen card
                    Card newCard = dealer.receiveCard(player, chosen);
                    player.getHand().add(newCard);
                }
            };

            timer.start();

            //deal another card back to player
            Card freshCard = dealer.dealCard(player);
            player.hand.add(freshCard);

            final ArrayList<Card> submitted = dealer.getSubmitted();

            //15 second timer for czar
            CountDownTimer czarTimer = new CountDownTimer(15000, 1000) {
                Card chosen = submitted.get(0);             //default to submitting first card

                public void onTick(long millisUntilFinished) {
                    //probably should do some interface things here

                    //OnClick listener for card submissions
                }

                public void onFinish() {
                    //submit chosen card
                    if(player.isCzar()){
                        dealer.czarPick(chosen);
                    }

                    Card newCard = dealer.receiveCard(player, chosen);
                    player.getHand().add(newCard);
                }
            };

            czarTimer.start();

            //give point to winner
            Exec.addPoint(player);

            player.setCzar(dealer.isCzar(player));          //update whether player is czar

        }//end game loop
    }//end playGame method

    private void setQuestion(){
        //Card card = riffle.getQuestion();
        Card card = dealer.getQuestion();

        String questionText = card.getText();
        TextView textView = (TextView)view.findViewById(R.id.question);
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
            TextView view = (TextView)(new TextView(this.context)).findViewById(resID);
            view.setText(hand.get(i).getText());
        }
    }//end setAnswers method

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //riffle.leave(player);

        dealer.removePlayer(player);
    }
}
