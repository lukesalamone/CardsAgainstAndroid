package io.exis.cards.cards;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends Activity {

    static final String CONTENT_RATING = "contentRating";
    static final String QUESTION_CARDS = "questionCards";
    static final String ANSWER_CARDS = "answerCards";
    public static final String PREFS = "prefsFile";
    public static int points;
    public static boolean online = false;
    private static Context context;
    private static ArrayList<Card> answers;
    private static ArrayList<Card> questions;
    private static Typeface LibSans;
    private static Typeface LibSansBold;
    private static Typeface LibSansItalic;

    private boolean finishedLoading;

    Button gameButton;
    TextView infoText;
    TextView pointsText;
    Switch onlineSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.context = getApplicationContext();

        finishedLoading = false;
        online = false;

        //set typefaces
        LibSans = Typeface.createFromAsset(getAssets(),"LiberationSans-Regular.ttf");
        LibSansBold = Typeface.createFromAsset(getAssets(),"LiberationSans-Bold.ttf");
        LibSansItalic = Typeface.createFromAsset(getAssets(),"LiberationSans-Italic.ttf");

        gameButton  = (Button) findViewById(R.id.button);
        gameButton.setTypeface(LibSansBold);
        infoText = (TextView) findViewById(R.id.info);
        infoText.setTypeface(LibSansItalic);
        pointsText = (TextView) findViewById(R.id.your_points);
        onlineSwitch = (Switch) findViewById(R.id.onlineSwitch);
        onlineSwitch.setTypeface(LibSans);

        onlineSwitch.setOnClickListener(v ->
            {online = !online;
            onlineSwitch.toggle();});

        //setPoints();
    }

    public void startGame(View view) {
        Log.v("MainActivity", "startGame");
        Intent intent = new Intent(view.getContext(), GameActivity.class);
        view.getContext().startActivity(intent);
    }

    public static Typeface getTypeface(String tf){
        if(tf.equals("LibSans")){
            return LibSans;
        }
        if(tf.equals("LibSansBold")){
            return LibSansBold;
        }
        if(tf.equals("LibSansItalic")){
            return LibSansItalic;
        }
        //default to returning regular
        return LibSans;
    }

    /*
     * TODO
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        //savedInstanceState.putInt("points", points);
    }

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
    }

    public void onSwitchClicked(View view){
        //online = ((Switch) view).isChecked();

        if(!finishedLoading) {
            infoText.setText(R.string.loading_questions);
            questions = Card.getQuestions(false);
            infoText.setText(R.string.loading_answers);
            answers = Card.getAnswers(false);
            infoText.setText(R.string.finished);
            Log.i("MainActivity", "Finished loading cards!");

            finishedLoading = true;
            gameButton.setTextColor(Color.parseColor("#ffffff"));
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
     * Set points to previously saved value
     */
    private void setPoints(){
        points = getSharedPreferences(PREFS, 0).getInt("points", 0);

        String text = String.valueOf(points);
        Log.i("points", text);

        pointsText.setText(text);
    }
}
