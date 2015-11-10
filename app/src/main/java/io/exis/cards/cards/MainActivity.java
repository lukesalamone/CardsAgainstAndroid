package io.exis.cards.cards;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends Activity {

    static final String CONTENT_RATING = "contentRating";
    static final String QUESTION_CARDS = "questionCards";
    static final String ANSWER_CARDS = "answerCards";
    public static boolean adult = false;
    private static Context context;
    private static ArrayList<Card> answers;
    private static ArrayList<Card> questions;

    //public RiffleSession riffle = new RiffleSession();

    Button gameButton;
    TextView infoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.context = getApplicationContext();

        gameButton  = (Button) findViewById(R.id.button);
        infoText = (TextView) findViewById(R.id.info);

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

    /*
     * TODO
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        /*
        savedInstanceState.putInt(QUESTION_CARDS, questions);
        savedInstanceState.putInt(ANSWER_CARDS, answers);
        */

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }

    public void onRadioButtonClicked(View view){
        gameButton.setEnabled(true);
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radio_pg13:
                if (checked) {
                    adult = false;

                    //load PG-13 cards
                    infoText.setText("Loading Questions...");
                    questions = Card.getQuestions(adult);

                    infoText.setText("Loading Answers...");
                    answers = Card.getAnswers(adult);
                    Log.i("MainActivity", "Finished loading cards!");
                    break;
                }
            case R.id.radio_adult:
                if (checked) {
                    adult = true;
                    //load R-rated cards
                    infoText.setText("Loading Questions...");
                    questions = Card.getQuestions(adult);
                    infoText.setText("Loading Answers...");
                    answers = Card.getAnswers(adult);
                    Log.i("MainActivity", "Finished loading cards!");
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
