package io.exis.cards.cards;

import android.content.res.Resources;
import android.os.Bundle;
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
    private Player player;
    public Chronometer chronometer;
    //public RiffleSession riffle;
    Dealer dealer = new Dealer(MainActivity.adult, Exec.getNewID());

    public GameActivity(Context context){
        /*riffle = new RiffleSession();
        player = riffle.addPlayer();

        //ask dealer if player is czar, set appropriately
        player.setCzar(riffle.isCzar(player.getPlayerID()));*/

        Exec.addPlayer(MainActivity.adult);
        player.setCzar(dealer.isCzar(player));
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
        setAnswers();

        playGame();
    }

    private void playGame(){
        while(true){

        }
    }//end playGame method

    private void setQuestion(){
        //Card card = riffle.getQuestion();
        Card card = dealer.getQuestion();

        String questionText = card.getText();
        TextView textView = (TextView)view.findViewById(R.id.question);
        textView.setText(questionText);
    }//end setQuestion method

    private void setAnswers(){
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
