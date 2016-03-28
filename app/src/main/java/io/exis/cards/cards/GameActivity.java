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
    public RiffleSession session;
    private boolean answerSelected;                             //whether card has been selected
    private Card chosen;
    private ArrayList<Card> forCzar;
    private String phase;

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
        Log.i("GameActivity", "entered constructor");
        Riffle.setFabricDev();
        Riffle.setLogLevelDebug();
        Riffle.setCuminOff();

        ////////////////////////////////
        /////// FAT RED BUTTON ///////
        ////////////////////////////////
        this.online = true;
        ///////////////////////////////

        this.context = MainActivity.getAppContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("Game activity", "entering onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        card1 = (TextView) findViewById(R.id.card1);
        card2 = (TextView) findViewById(R.id.card2);
        card3 = (TextView) findViewById(R.id.card3);
        card4 = (TextView) findViewById(R.id.card4);
        card5 = (TextView) findViewById(R.id.card5);
        infoText = (TextView) findViewById(R.id.room_id);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        card1.setTypeface(MainActivity.getTypeface(""));
        card2.setTypeface(MainActivity.getTypeface(""));
        card3.setTypeface(MainActivity.getTypeface(""));
        card4.setTypeface(MainActivity.getTypeface(""));
        card5.setTypeface(MainActivity.getTypeface(""));
        infoText.setTypeface(MainActivity.getTypeface("LibSansItalic"));

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

        if(online){
            // int id = Exec.getNewID();
            Log.i(TAG, "creating Exec instance");
            exec = new Exec();

            Domain app = new Domain("xs.damouse.CardsAgainst");
            Player player = new Player(Exec.getNewID(), app);
            player.activity = this;
            session = new RiffleSession(player.playerDomain());
            session.setPlayer(player);
            exec.externalPlayer = player;
            exec.join();
        } else {
            //TODO consolidate calls into future Exec.join(player)

            dealer = Exec.findDealer();                       //gets a dealer for the player
            dealer.prepareGame();                                   //load questions and answers
            dealer.addPlayer(player);                               //adds player to dealer
            dealer.addDummies();

            setQuestion();                              //set question TextView
            refreshCards(player.hand());

            Log.i("onResume", "playing offline game");
            playOfflineGame();
        }
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
            session.leave();
        }else {
            dealer.leave(player);
        }
    }

    private void playOfflineGame(){
        int i = 0;
        answerSelected = false;
        player.setCzar(dealer.isCzar(player));
        player.setHand(dealer.getNewHand());
        dealer.setPlayers();
        setQuestion();                          //draw question card
        chosen = player.hand().get(0);

        GameTimer timer = new GameTimer(15000, 1000);
        timer.setType("answering");
        dealer.start();                         //start dealer's timer
        timer.start();
    }//end playOfflineGame method

    public void playOnlineGame(){
        String TAG = "playOnlineGame";
        int i = 0;
        answerSelected = false;
        setQuestion();

        GameTimer timer = new GameTimer(15000, 1000);
        timer.setType("answering");
        timer.start();
    }//end playGame method

    // called by Player after join
    public void onPlayerJoined(Object[] play){
        String TAG = "onPlayerJoined()";
        Log.i(TAG, "entered method");
        try {
            player.setHand(Card.buildHand((String[]) play[0]));
            this.players = (Player[]) play[1];
            this.state = (String) play[2];
            this.roomName = (String) play[3];
        }catch(NullPointerException e){
            Log.wtf(TAG, "null pointer in play object");
            e.printStackTrace();
        }

        this.runOnUiThread(() -> {
            Log.i(TAG, "runOnUithread block entered");
            setQuestion();                              //set question TextView
            refreshCards(player.hand());
            playOnlineGame();
        });
    }//end onPlayerJoin method

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
    public void refreshCards(ArrayList<Card> pile){
        //change card texts to text of cards in hand
        for(int i=0; i<5; i++){
            String str = "card" + (i + 1);
            int resID = context.getResources().getIdentifier(str,
                    "id", context.getPackageName());
            TextView view = (TextView) findViewById(resID);
            view.setText( pile.get(i).getText() );
        }
    }//end setAnswers method

    //TODO condense these 5 methods...
    public void submitCard1(View view){
        player.setPicked(0);

        //set background colors
        setBackgrounds(1, view);
        answerSelected = true;
    }

    public void submitCard2(View view){
        player.setPicked(1);

        setBackgrounds(2, view);
        answerSelected = true;
    }

    public void submitCard3(View view){
        player.setPicked(2);
        setBackgrounds(3, view);
        answerSelected = true;
    }

    public void submitCard4(View view){
        player.setPicked(3);

        setBackgrounds(4, view);
        answerSelected = true;
    }

    public void submitCard5(View view){
        player.setPicked(4);

        setBackgrounds(5, view);
        answerSelected = true;
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
                        forCzar = player.answers();
                        chosen = forCzar.get(0);            //default submit first card
                        refreshCards(forCzar);
                    }else{
                        infoText.setText(R.string.pickingInfo);
                        if(!answerSelected){
                            submitCard1(card1);
                        }
                    }

                    answerSelected = false;
                    phase = "picking";
                    setNextTimer("picking");
                    break;
                case "picking":                             //next phase will be scoring
                    infoText.setText(R.string.scoringInfo);

                    phase = "scoring";
                    setNextTimer("scoring");
                    break;
                case "scoring":                                 //next phase will be answering
                    setQuestion();                              //update question card
                    if(player.isCzar()){
                        infoText.setText(R.string.playersPickingInfo);
                    }else{
                        infoText.setText(R.string.answeringInfo);
                    }
                    Player winner = player.getWinner();             //give point if player is winner
                    if(winner != null && winner.ID() == player.ID()){
                        Toast.makeText(context, "You won this round!", Toast.LENGTH_SHORT).show();
                        player.addPoint();
                    }
                    Log.i("Player's cards", player.printHand());

                    refreshCards(player.hand());
                    phase = "answering";
                    setNextTimer("answering");
                    break;
            }//end switch
            Log.i("game timer", "beginning phase " + phase);
            next.start();
        }// end onFinish method

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
