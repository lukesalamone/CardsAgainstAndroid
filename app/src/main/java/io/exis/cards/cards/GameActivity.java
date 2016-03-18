package io.exis.cards.cards;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import com.exis.riffle.Domain;
import com.exis.riffle.Riffle;

import java.util.ArrayList;

/**
 * GameActivity.java
 *
 * Manages game screen
 *
 * Created by luke on 10/22/15.
 */
public class GameActivity extends Activity {

    //public final String PREFS;
    public int points;
    private Context context;
    public static boolean online;
    public Player player;
    private Dealer dealer;
    private Exec exec;
    private ProgressBar progressBar;
    public RiffleSession riffle;
    private boolean selected;                                 //whether card has been selected
    private Card chosen;
    private ArrayList<Card> forCzar;
    private String phase;

    public String[] hand;
    public Player[] players;
    public String state;
    public String roomName;

    TextView card1;
    TextView card2;
    TextView card3;
    TextView card4;
    TextView card5;
    TextView infoText;

    public GameActivity(){
        Riffle.setFabricDev();
        Riffle.setLogLevelDebug();
        Riffle.setCuminOff();

        Log.i("GameActivity", "entered constructor");
        ////////////////////////////////
        /////// FAT RED BUTTON ///////
        ////////////////////////////////
        online = true;
        ///////////////////////////////

        context = MainActivity.getAppContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("Game activity", "entering onCreate()");
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
        infoText = (TextView) findViewById(R.id.room_id);
        infoText.setTypeface(MainActivity.getTypeface("LibSansItalic"));

        progressBar = (ProgressBar) findViewById(R.id.progress);
        Log.i("Game activity", "leaving onCreate()");
    }

    @Override
    public void onStart(){
        Log.i("Game activity", "entering onStart()");
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        String TAG = "GameActivity::onResume()";
        int i = 0;

        if(online){
            // int id = Exec.getNewID();
            Log.i(TAG, "creating Exec instance");
            exec = new Exec(this);

            Domain app = new Domain("xs.damouse.CardsAgainst");
            Player player = new Player(Exec.getNewID(), app);
            player.activity = this;
            exec.externalPlayerDomain = player;

            exec.join();

        } else {
            //TODO consolidate calls into future Exec.join(player)

            dealer = Exec.findDealer(online);                       //gets a dealer for the player
            dealer.prepareGame();                                   //load questions and answers
            dealer.addPlayer(player);                               //adds player to dealer
            dealer.addDummies();

            setQuestion();                              //set question TextView
            showCards();

            Log.i("onResume", "playing offline game");
            playOfflineGame();
        }

                               //populate answers TextViews
    }//end onResume method

    @Override
    public void onPause(){
        super.onPause();

        // TODO save points to disk

    }//end onPause method

    @Override
    protected void onStop(){
        super.onStop();
    }//end onStop method

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(online) {
            riffle.leave();
        }else {
            dealer.removePlayer(player);
        }
    }

    private void playOfflineGame(){
        int i = 0;
        selected = false;
        player.setCzar(dealer.isCzar(player));
        player.setHand(dealer.getNewHand());
        dealer.setPlayers();
        setQuestion();                          //draw question card
        chosen = player.getHand().get(0);

        GameTimer timer = new GameTimer(15000, 1000);
        timer.setType("answering");
        dealer.start();                         //start dealer's timer
        timer.start();
    }//end playOfflineGame method

    public void playOnlineGame(){
        String TAG = "playOnlineGame";
        int i = 0;
        selected = false;

        Log.i(TAG, "" + i++);
        //player.setHand(dealer.getNewHand(player));                // TODO

        Log.i(TAG, "" + i++);
        //dealer.setPlayers();                                      // TODO

        Log.i(TAG, "" + i++);
        setQuestion();

        GameTimer timer = new GameTimer(15000, 1000);
        dealer.start();                         //start dealer's timer
        timer.start();
    }//end playGame method

    public void setQuestion(){
        String questionText;
        if(online) {
            questionText = player.question();
        }else{
            Card card = dealer.getQuestion();
            questionText = card.getText();
        }

        TextView textView = (TextView) findViewById(R.id.question);
        textView.setText(questionText);
        textView.setTypeface(MainActivity.getTypeface("LibSansBold"));
    }//end setQuestion method

    //Sets card faces to answers
    public void showCards(){
        if(online){
            //change card texts to text of cards in hand
            for(int i=0; i<5; i++){
                String str = "card" + (i + 1);
                int resID = context.getResources().getIdentifier(str,
                        "id", context.getPackageName());
                TextView view = (TextView) findViewById(resID);
                view.setText(hand[i]);
            }
        }else{
            ArrayList<Card> hand = player.getHand();

            //change card texts to text of cards in hand
            for(int i=0; i<5; i++){
                String str = "card" + (i + 1);
                int resID = context.getResources().getIdentifier(str,
                        "id", context.getPackageName());
                TextView view = (TextView) findViewById(resID);
                view.setText(hand.get(i).getText());
            }
        }
    }//end setAnswers method

    //TODO condense these 5 methods...
    public void submitCard1(View view){
        if(online){

        }else{
            player.pick(dealer, player.getHand().get(0));
        }


        //set background colors
        setBackgrounds(1, view);
        selected = true;
    }

    public void submitCard2(View view){
        player.pick(dealer, player.getHand().get(1));

        setBackgrounds(2, view);
        selected = true;
    }

    public void submitCard3(View view){
        player.pick(dealer, player.getHand().get(2));
        setBackgrounds(3, view);
        selected = true;
    }

    public void submitCard4(View view){
        player.pick(dealer, player.getHand().get(3));
        setBackgrounds(4, view);
        selected = true;
    }

    public void submitCard5(View view){
        player.pick(dealer, player.getHand().get(4));
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

    private void setCzarCards(ArrayList<Card> forCzar){
        String str;
        int resID;
        TextView view;
        for(int i=1; i<=5; i++){
            str = "card" + i;
            resID = context.getResources().getIdentifier(str, "id", context.getPackageName());
            view = (TextView) findViewById(resID);
            view.setText(forCzar.get(i-1).getText());
        }
        this.forCzar = forCzar;
    }

    private class GameTimer extends CountDownTimer{
        private GameTimer next;
        private String type;

        public GameTimer(long startTime, long interval){
            super(startTime, interval);
        }

        @Override
        public void onFinish(){
            progressBar.setProgress(progressBar.getMax());

            switch(type){
                case "answering":                           //next phase will be picking
                    if(player.isCzar()){
                        infoText.setText(R.string.answeringInfo);
                        forCzar = dealer.getCardsForCzar();
                        chosen = forCzar.get(0);            //default submit first card
                        setCzarCards(forCzar);
                    }else{
                        infoText.setText(R.string.pickingInfo);
                        if(!selected){
                            submitCard1(card1);
                            player.removeCard(player.getHand().get(0));
                        }else{
                            dealer.pick(player, chosen.getText());
                            player.removeCard(chosen);
                        }
                    }
                    selected = false;
                    phase = "picking";
                    setNextTimer("picking");
                    break;
                case "picking":                             //next phase will be scoring
                    infoText.setText(R.string.scoringInfo);

                    phase = "scoring";
                    setNextTimer("scoring");
                    break;
                case "scoring":                                 //next phase will be answering
                    player.setCzar(dealer.isCzar(player));      //update whether player is czar
                    setQuestion();                              //update question card
                    if(player.isCzar()){
                        infoText.setText(R.string.playersPickingInfo);
                    }else{
                        infoText.setText(R.string.answeringInfo);
                    }
                    Player winner = dealer.getWinner();             //give point if player is winner
                    if(winner != null && winner.ID() == player.ID()){
                        Toast.makeText(context, "You won this round!", Toast.LENGTH_SHORT).show();
                        points++;
                    }
                    Log.i("Player's cards", player.printHand());

                    showCards();
                    phase = "answering";
                    setNextTimer("answering");
                    break;
            }//end switch
            Log.i("game timer", "ending phase " + phase);
            next.start();
        }

        @Override
        public void onTick(long millisUntilFinished){
            //TODO add sexier progress animation

            //set chronometer width proportionally to time remaining
            long timeRemaining = (millisUntilFinished / 1000);
            int progress = (int) (progressBar.getMax() * timeRemaining / 15);

            progressBar.setProgress(progress);
        }//end onTick method

        public void setType(String timerType){
            type = timerType;
        }

        private void setNextTimer(String nextType){
            // TODO synchronization may be a problem
            next = new GameTimer(10000, 1000);
            next.setType(nextType);
        }
    }//end GameTimer subclass
}//end GameActivity class
