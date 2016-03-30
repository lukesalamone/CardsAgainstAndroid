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

import com.exis.riffle.Riffle;

import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends Activity {
    public static boolean online = false;
    private static Context context;
    private static ArrayList<Card> answers;
    private static ArrayList<Card> questions;
    private static Typeface LibSans;
    private static Typeface LibSansBold;
    private static Typeface LibSansItalic;

    Button gameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.context = getApplicationContext();
        online = false;

        //set typefaces
        LibSans = Typeface.createFromAsset(getAssets(),"LiberationSans-Regular.ttf");
        LibSansBold = Typeface.createFromAsset(getAssets(),"LiberationSans-Bold.ttf");
        LibSansItalic = Typeface.createFromAsset(getAssets(),"LiberationSans-Italic.ttf");

        gameButton  = (Button) findViewById(R.id.button);
        gameButton.setTypeface(LibSansBold);

        questions = Card.questions();
        answers = Card.answers();
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
    }//end onStop method

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static Context getAppContext(){
        return MainActivity.context;
    }

    //load file into string and return it
    public static String getCardString(String name){
        String cardString = "";
        if(name.equals("q13")){
            for(int i=1; i<=6; i++){
                int resID = context.getResources().getIdentifier("q" + i, "raw", context.getPackageName());
                Scanner fileIn = new Scanner(context.getResources().openRawResource(resID));
                cardString += fileIn.useDelimiter("\\Z").next();
            }
        }else{
            for(int i=1; i<=15; i++){
                int resID = context.getResources().getIdentifier("a" + i, "raw", context.getPackageName());
                Scanner fileIn = new Scanner(context.getResources().openRawResource(resID));
                cardString += fileIn.useDelimiter("\\Z").next();
            }
        }

        return cardString;
    }//end getCardString method

    public static ArrayList<Card> getQuestions(){
        return questions;
    }

    public static ArrayList<Card> getAnswers(){
        return answers;
    }
}
