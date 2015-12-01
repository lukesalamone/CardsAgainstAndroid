package io.exis.cards.cards;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
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
    //private Chronometer chronometer;
    private ProgressBar progressBar;
    private RiffleSession riffle;
    private boolean selected;                                 //whether card has been selected

    TextView card1;
    TextView card2;
    TextView card3;
    TextView card4;
    TextView card5;

    public GameActivity(){
        adult = MainActivity.adult;
        context = MainActivity.getAppContext();
        //riffle = new RiffleSession();                         //create unique riffle session
        //int PID = riffle.getNewID();

        int PID = Exec.getNewID();

        if(PID == 0 || PID == -1){
            Log.i("GameActivity", "problem with riffle.getNewID");
            PID = Exec.getNewID();
        }

        player = new Player(
                PID,
                new ArrayList<Card>(),
                false
        );

        dealer = Exec.findDealer(adult);                        //gets a dealer for the player
        dealer.prepareGame();                                   //load questions and answers
        dealer.addPlayer(player);                               //adds player to dealer
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        card1 = (TextView) findViewById(R.id.card1);
        card1.setTypeface(MainActivity.getTypeface(""));
        card2 = (TextView) findViewById(R.id.card2);
        card2.setTypeface(MainActivity.getTypeface(""));
        card3 = (TextView) findViewById(R.id.card3);
        card3.setTypeface(MainActivity.getTypeface(""));
        card4 = (TextView) findViewById(R.id.card4);
        card4.setTypeface(MainActivity.getTypeface(""));
        card5 = (TextView) findViewById(R.id.card5);
        card5.setTypeface(MainActivity.getTypeface(""));

        progressBar = (ProgressBar) findViewById(R.id.progress);
    }

    @Override
    public void onStart(){
        super.onStart();
        setQuestion();                              //set question TextView
        showCards();                                //populate answers TextViews
    }

    @Override
    public void onResume() {
        super.onResume();

        //play while the window is still in focus
        playAGame();
    }

    private void playAGame(){
        selected = false;
        player.setCzar(dealer.isCzar(player));
        player.setHand(dealer.getNewHand(player));
        dealer.setPlayers();
        setQuestion();                          //draw question card

        if(!player.isCzar()){
            Log.i("playGame", "player is not czar");

            //15 second timer for submission
            GameTimer submissionTimer = new GameTimer(15000, 1000);

            //default to submitting first card
            submissionTimer.setChosen(player.getHand().get(0));
            submissionTimer.start();

            //sexy smooth progress bar
            //stackoverflow.com/questions/6097795/android-make-a-progress-bar-update-smoothly
            if(android.os.Build.VERSION.SDK_INT >= 11){
                // will update the "progress" propriety of seekbar until it reaches progress
                ObjectAnimator animation = ObjectAnimator.ofInt(progressBar,
                        "progress",
                        progressBar.getMax(), 0);
                animation.setDuration(15000);
                animation.setInterpolator(new DecelerateInterpolator());
                animation.start();
            }
        }//end submission case

        if(player.isCzar()) {
            dummyTimer();
            final ArrayList<Card> submitted = dealer.getSubmitted();

            GameTimer czarTimer = new GameTimer(15000, 1000);
            czarTimer.setChosen(submitted.get(0));
            czarTimer.start();
        }//end czar case

        Exec.addPoint(player);                          //give point to winner
        player.setCzar(dealer.isCzar(player));          //update whether player is czar
    }//end playGame method

    //creates and runs 15 second waiting timer
    private void dummyTimer(){
        GameTimer dummy = new GameTimer(15000, 1000);
        dummy.setType(true);
        dummy.start();
    }

    private void setQuestion(){
        Card card = dealer.getQuestion();

        String questionText = card.getText();
        TextView textView = (TextView) findViewById(R.id.question);
        textView.setText(questionText);
        textView.setTypeface(MainActivity.getTypeface("LibSansBold"));
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
        dealer.receiveCard(player.getHand().get(0));
        //set background colors
        setBackgrounds(1, view);
        selected = true;
    }

    public void submitCard2(View view){
        dealer.receiveCard(player.getHand().get(1));
        setBackgrounds(2, view);
        selected = true;
    }

    public void submitCard3(View view){
        dealer.receiveCard(player.getHand().get(2));
        setBackgrounds(3, view);
        selected = true;
    }

    public void submitCard4(View view){
        dealer.receiveCard(player.getHand().get(3));
        setBackgrounds(4, view);
        selected = true;
    }

    public void submitCard5(View view){
        dealer.receiveCard(player.getHand().get(4));
        setBackgrounds(5, view);
        selected = true;
    }

    //whiten card backgrounds other than card c
    private void setBackgrounds(int c, View v){
        //v.setBackgroundColor(Color.parseColor("#ff30b2c1"));
        ((TextView) v).setTextColor(Color.parseColor("#ff000000"));

        String str;
        //set only selected card to blue
        for(int i=1; i<=5; i++){
            str = "card" + i;
            if(i != c){//if i != c change background to white
                int resID = context.getResources().getIdentifier(str, "id",
                        context.getPackageName());
                TextView view = (TextView) findViewById(resID);
                //view.setBackgroundColor(Color.parseColor("#ffffffff"));
                view.setTextColor(Color.parseColor("#55000000"));
            }
        }
    }//end setBackgrounds method

    public class GameTimer extends CountDownTimer{
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
            progressBar.setProgress(0);

            if(!waiting){
                //if player is idle
                if(!selected){
                    submitCard1(card1);
                }

                if(player.isCzar()){
                    //submit chosen card
                    dealer.czarPick(chosen);
                }else{
                    dealer.receiveCard(chosen);
                    Card newCard = dealer.dealCard(player);
                    player.addCard(newCard);
                }
            }
            Exec.addPoint(player);                              //give point to winner
            player.setCzar(dealer.isCzar(player));              //update whether player is czar
        }

        @Override
        public void onTick(long millisUntilFinished){
            //set chronometer width proportionally to time remaining
            if(android.os.Build.VERSION.SDK_INT < 11) {
                long timeRemaining = (millisUntilFinished / 1000);
                int progress = (int) (progressBar.getMax() * timeRemaining / 15);
                progressBar.setProgress(progress);
            }
        }//end onTick method

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
