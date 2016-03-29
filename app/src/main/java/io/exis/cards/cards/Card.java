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

    public static Card getErrorCard(String dummyString){
        return new Card(dummyString);
    }

    public String getText(){
        return this.text;
    }//end getID method

    public boolean equals(Card card){
        return this.text.equals(card.getText());
    }

    public static ArrayList<Card> getQuestions(boolean R){
        questions = new ArrayList<>();

        try {
            //There are 595 R-rated questions
            if (R) {
                for (int i = 0; i < 595; i++) {
                    questions.add(getCardByID(i, R, 'q'));
                    Log.i("getQuestions", "Added question card " + i);
                }
            //pg13 questions number 28 - 35
            } else {
                for(int i=0; i < 8; i++){
                    questions.add(getCardByID(i, R, 'q'));
                }
            }
        } catch(JSONException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        cardsArray = null;
        Log.i("get questions", "questions has size " + questions.size());
        return questions;
    }//end getQuestions method

    public static ArrayList<Card> getAnswers(boolean R){
        Log.i("getAnswers", "Loading answers...");
        answers = new ArrayList<>();
        try {
            //R answers number from 36 to 2864
            if (R) {
                for (int i = 0; i < 2259; i++) {
                    answers.add(getCardByID(i, R, 'a'));
                }
            //pg13 answers number from 0 - 27
            } else {
                for(int i = 0; i < 27; i++){
                    answers.add(getCardByID(i, R, 'a'));
                }
            }
        } catch(JSONException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        cardsArray = null;
        Log.i("get answers", "answers has size " + answers.size());
        return answers;
    }//end getAnswers method

    //returns a Card from an ID. When R is true return normal card set
    //Card cannot set PID! Receiving party must set it.
    public static Card getCardByID(int ID, boolean R, char type) throws JSONException{
        //load card text files into JSON array
        if(cardsArray == null) {
            if (R) {
                //requesting a question card
                if (type == 'q') {
                    cardsArray = getCardsJSON("q21");
                } else {    //requesting an answer card
                    cardsArray = getCardsJSON("a21");
                }
            } else { //pg-13 card set
                if (type == 'q') {
                    cardsArray = getCardsJSON("q13");
                } else {
                    cardsArray = getCardsJSON("a13");
                }
            }
        }

        try{
            //retrieve JSONObject from JSONArray
            cardsJSON = new JSONObject( cardsArray.getJSONObject(ID), keys );
        } catch(JSONException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        String cardText = cardsJSON.getString("text");

        //return card created with cardText
        return new Card(cardText);
    }//end getCardByID method

    /*
     * Gets a JSON Array of JSON Objects which contain card texts and irrelevant IDs
     *
     * @param name The name of the file to be retrieved
     * @return The JSONArray
     */
    public static JSONArray getCardsJSON(String name){

        if(cardsArray == null || cardsJSON.length() == 0){

            String cardString = MainActivity.getCardString(name);

            try {
                cardsArray = new JSONArray(cardString);
                Log.i("getCardsJSON", "cardsArray:\n\n" + cardsArray.toString(4));
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

    public static String[] handToStrings(ArrayList<Card> hand){
        String[] arr = new String[hand.size()];
        for(int i=0; i<arr.length; i++){
            arr[i] = hand.get(i).getText();
        }
        return arr;
    }

    // return card whose text is passed as parameter
    public static Card searchCards(String text){
        for(Card card : answers){
            if(card.getText().equals(text)){
                return card;
            }
        }

        for(Card card : questions){
            if(card.getText().equals(text)){
                return card;
            }
        }

        // this should never happen
        return new Card(text);
    }// end searchCards method

    public static ArrayList<Card> buildHand(String[] array){
        ArrayList<Card> hand = new ArrayList<>();
        for(String s : array){
            hand.add(new Card(s));
        }

        return hand;
    }// end buildHand method
}