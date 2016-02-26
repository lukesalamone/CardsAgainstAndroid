package io.exis.cards.cards;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

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
    private Player player;
    private Dealer dealer;
    private ProgressBar progressBar;
    private RiffleSession riffle;
    private boolean selected;                                 //whether card has been selected
    private Card chosen;
    private ArrayList<Card> forCzar;
    private String phase;

    TextView card1;
    TextView card2;
    TextView card3;
    TextView card4;
    TextView card5;
    TextView infoText;

    public GameActivity(){
        online = MainActivity.online;
        context = MainActivity.getAppContext();

        // TODO

        riffle = new RiffleSession("ws://ec2-52-26-83-61.us-west-2.compute.amazonaws.com:8000/ws");
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

        riffle.setPlayer(player);
        riffle.play();

        dealer = Exec.findDealer();                        //gets a dealer for the player
        dealer.prepareGame();                                   //load questions and answers
        dealer.addPlayer(player);                               //adds player to dealer

        //damouse's scheme
        Log.i("Game Activity", "5");
        Object[] playObject = riffle.play();
        String[] hand = (String[]) playObject[0];
        Player[] players = (Player[]) playObject[1];
        String state = (String) playObject[2];
        String roomName = (String) playObject[3];
        Log.i("Game activity", "leaving constructor");
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
        setQuestion();                              //set question TextView
        showCards();                                //populate answers TextViews
    }

    @Override
    public void onResume() {
        super.onResume();

        if(online){
            playOnlineGame();
        } else {
            playOfflineGame();
        }
    }//end onResume method

    @Override
    public void onPause(){
        super.onPause();

/*        //save points to disk
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("points", points);
        editor.apply();*/
    }//end onPause method

    @Override
    protected void onStop(){
        super.onStop();

/*        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("points", points);
        editor.apply();*/
    }//end onStop method

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //riffle.leave(player);
        dealer.removePlayer(player);
    }

    private void playOfflineGame(){
        selected = false;
        player.setCzar(dealer.isCzar(player));
        player.setHand(dealer.getNewHand(player));
        dealer.setPlayers();
        setQuestion();                          //draw question card

        GameTimer timer = new GameTimer(15000, 1000);
        dealer.start();                         //start dealer's timer
        timer.start();
    }//end playOfflineGame method

    private void playOnlineGame(){
        selected = false;
        player.setCzar(dealer.isCzar(player));
        player.setHand(dealer.getNewHand(player));
        dealer.setPlayers();
        setQuestion();                          //draw question card

        GameTimer timer = new GameTimer(15000, 1000);
        dealer.start();                         //start dealer's timer
        timer.start();
    }//end playGame method

    private void setQuestion(){
        Card card = dealer.getQuestion();

        String questionText = card.getText();
        TextView textView = (TextView) findViewById(R.id.question);
        textView.setText(questionText);
        textView.setTypeface(MainActivity.getTypeface("LibSansBold"));
    }//end setQuestion method

    //Sets card faces to answers
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

    //TODO condense these 5 methods...
    public void submitCard1(View view){
        player.pick(dealer, player.getHand().get(0));

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
    }

    private class GameTimer extends CountDownTimer{
        private GameTimer next;

        public GameTimer(long startTime, long interval){
            super(startTime, interval);
        }

        @Override
        public void onFinish(){
            progressBar.setProgress(0);

            switch(phase){
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
                    setNextTimer();
                    break;
                case "picking":                             //next phase will be scoring
                    infoText.setText(R.string.scoringInfo);

                    phase = "scoring";
                    setNextTimer();
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
                    if(winner != null && winner.getPlayerID() == player.getPlayerID()){
                        Toast.makeText(context, "You won this round!", Toast.LENGTH_SHORT).show();
                        points++;
                    }
                    phase = "answering";
                    setNextTimer();
                    break;
            }//end switch
            setNextTimer();
            next.start();
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

        private void setNextTimer(){
            //lets keep this bad boy synchronized
            next = new GameTimer(dealer.getTimeRemainingInPhase(), 1000);
        }
    }//end GameTimer subclass
}//end GameActivity class
