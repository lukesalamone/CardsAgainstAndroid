package io.exis.cards.cards;

/**
 *
 * Card.java
 * Create a card from a card ID
 *
 * NOTE: Card IDs are completely fucked up in the text files and will probably
 * need to be redone at some point. There are large gaps in their numbering.
 *
 * NOTE: It will be difficult to easily extend the number of cards due to the
 * way they IDs are numbered.
 *
 * Created by luke on 10/8/15.
 * Copyright © 2015 paradrop. All rights reserved.
 *
 */

import org.json.*;
import android.content.Context;
import android.util.Log;
import java.util.ArrayList;

public class Card {

    private String text;
    private static JSONArray cardsArray;
    private static JSONObject cardsJSON = new JSONObject();
    private static String[] keys = {"text", "id"};                      //keys for JSON array
    private static ArrayList<Card> questions;
    private static ArrayList<Card> answers;

    static Context context;

    // Cards are fancy String wrappers
    public Card(String cardText){
        this.text = cardText;
        context = MainActivity.getAppContext();
    }//end Card constructor

    public static ArrayList<Card> questions(){
        if(questions == null || questions.size() == 0){
            loadQuestions();
        }

        return questions;
    }

    public static ArrayList<Card> answers(){
        if(answers == null || answers.size() == 0){
            loadAnswers();
        }

        return answers;
    }

    private static void loadQuestions(){
        questions = new ArrayList<>();
        JSONArray questionsJSON = getCardsJSON("q13");
        String cardText = "";
        JSONObject cardsJSON;
        for(int i=28; i<2890; i++){
            try{
                cardsJSON = new JSONObject( questionsJSON.getJSONObject(i), keys );
                cardText = cardsJSON.getString("text");
            } catch(JSONException e){
                break;
            }
            Log.i("adding question", cardText);
            questions.add(new Card(cardText));
        }
    }

    private static void loadAnswers(){
        answers = new ArrayList<>();
        JSONArray answersJSON = getCardsJSON("a13");
        String cardText = "";
        JSONObject cardsJSON;
        for(int i=0; i<2900; i++){
            try{
                cardsJSON = new JSONObject( answersJSON.getJSONObject(i), keys );
                cardText = cardsJSON.getString("text");
            } catch(JSONException e){
                break;
            }
            Log.i("adding answer", cardText);
            answers.add(new Card(cardText));
        }
    }

    /*
     * Gets a JSON Array of JSON Objects which contain card texts and irrelevant IDs
     *
     * @param name The name of the file to be retrieved
     * @return The JSONArray
     */
    public static JSONArray getCardsJSON(String name){
        if(cardsArray == null || cardsJSON.length() == 0){

            String cardString = MainActivity.getCardString(name);
            Log.i("getCardsJSON", "cardString has length " + cardString.length());

            try {
                cardsArray = new JSONArray(cardString);
                Log.i("getCardsJSON", "cardsArray has length " + cardsArray.length());
            } catch(JSONException e){
                Log.wtf("getCardsJSON", "JSON Exception thrown.");
                e.printStackTrace();
            }

            return cardsArray;
        }

        return cardsArray;
    }//end getCardJSON method

    public static String printHand(ArrayList<Card> hand){
        String s = "";
        for(Card c : hand){
            s += c.getText();
        }
        return s;
    }

    public static String printHand(String[] hand){
        String s = "";
        for(String c : hand){
            s += c + "\n";
        }
        return s;
    }

    public String getText(){
        return this.text;
    }//end getID method

    public static String[] handToStrings(ArrayList<Card> hand){
        String[] arr = new String[hand.size()];
        for(int i=0; i<arr.length; i++){
            arr[i] = hand.get(i).getText();
        }
        return arr;
    }

    public static ArrayList<Card> buildHand(String[] array){
        ArrayList<Card> hand = new ArrayList<>();
        for(String s : array){
            hand.add(new Card(s));
        }

        return hand;
    }// end buildHand method
}