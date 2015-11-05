package io.exis.cards.cards;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends Activity {

    static final String CONTENT_RATING = "contentRating";
    public static boolean adult = false;
    private static Context context;
    private static ArrayList<Card> answers;
    private static ArrayList<Card> questions;

    //public RiffleSession riffle = new RiffleSession();

    Button gameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.context = getApplicationContext();

        //create cards as json object
        //JSONObject json = Card.getCardsJSON("../../../../../", this.context);

        gameButton  = (Button) findViewById(R.id.button);

        if (savedInstanceState != null && savedInstanceState.getBoolean(CONTENT_RATING)) {
            adult = savedInstanceState.getBoolean(CONTENT_RATING);
        } else {
            gameButton.setEnabled(false);
        }

        setPoints();
    }

    public void startGame(View view) {
        Log.v("MainActivity", "startGame");
        Intent intent = new Intent(view.getContext(), GameActivity.class);
        view.getContext().startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }

    /*
     * TODO add loading text when loading cards?
     */
    public void onRadioButtonClicked(View view){
        gameButton.setEnabled(true);
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radio_pg13:
                if (checked) {
                    adult = false;
                    //load R-rated cards
                    questions = Card.getQuestions(adult);
                    answers = Card.getAnswers(adult);
                    break;
                }
            case R.id.radio_adult:
                if (checked) {
                    adult = true;
                    //load PG-13 cards
                    questions = Card.getQuestions(adult);
                    answers = Card.getAnswers(adult);
                    break;
                }
        }
    }//end onRadioButtonClicked method

    public static Context getAppContext(){
        return MainActivity.context;
    }

    //load file into string and return it
    public static String getCardString(String name){
        //name = name + ".txt";
        Log.i("Card.getCardString", "name = " + name);
        int resID = context.getResources().getIdentifier(name, "raw", context.getPackageName());
        Log.i("Card.getCardString", "resID = " + resID);
        Scanner fileIn = new Scanner(context.getResources().openRawResource(resID));
        return fileIn.nextLine();
    }//end getCardString method

    public static ArrayList<Card> getQuestions(){
        return questions;
    }

    public static ArrayList<Card> getAnswers(){
        return answers;
    }

    /*
     * @TODO
     * Set points to previously saved value
     *
     */
    private void setPoints(){
        // 1. Restore from network

        // 2. Display correct number in activity
    }



}
