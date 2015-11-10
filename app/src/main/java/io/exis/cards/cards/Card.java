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

import android.app.Activity;
import android.content.res.Resources;
import android.provider.MediaStore;

import org.json.*;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Card {

    int ID;
    String text;
    char type;
    int PID;
    static JSONArray cardsArray;
    static JSONObject cardsJSON = new JSONObject();
    static String[] keys = {"text", "id"};                      //keys for JSON array
    static ArrayList<Card> questions;
    static ArrayList<Card> answers;

    static char arrType;

    static Context context;

    //Every card has an ID, associated text, and type
    //Type may be 'q' for question or 'a' for answer
    public Card(int cardID, String cardText, char cardType, int playerID){
        ID = cardID;
        text = cardText;
        type = cardType;
        PID = playerID;
        context = MainActivity.getAppContext();
    }//end Card constructor

    public int getID(){
        return this.ID;
    }//end getID method

    public String getText(){
        return this.text;
    }//end getID method

    public char getType(){
        return this.type;
    }//end getType method

    public int getPID(){
        return this.PID;
    }

    public boolean equals(Card card){
        if(this.getID() != card.getID()){
            return false;
        }
        if (!this.getText().equals(card.getText())){
            return false;
        }
        return this.getType() == card.getType();
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
                for(int i = 28; i < 35; i++){
                    questions.add(getCardByID(i, R, 'q'));
                }
            }
        } catch(JSONException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Log.i("getQuestions", "Nullifying cardsArray...");
        cardsArray = null;
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
                    Log.i("getAnswers", "Added answer card " + i);
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

        Log.i("getAnswers", "Nullifying cardsArray...");
        cardsArray = null;
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
                    Log.i("getCardByID", "Loading questions into cardsArray.");
                    cardsArray = getCardsJSON("q21");
                } else {    //requesting an answer card
                    Log.i("getCardByID", "Loading answers into cardsArray.");
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

        if(type == 'q' && cardsArray != null) {
            Log.i("getCardByID", "Questions card array has length " + cardsArray.length());
        } else if(cardsArray != null){
            Log.i("getCardByID", "Answers card array has length " + cardsArray.length());
        } else if(cardsArray == null){
            Log.wtf("getCardByID", "Array is null!");
        }

        try{
            //retrieve JSONObject from JSONArray
            cardsJSON = new JSONObject( cardsArray.getJSONObject(ID), keys );
        } catch(JSONException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Log.i("getCardByID", "cards JSONObject has length " + cardsJSON.length());
        Log.i("getCardByID", "retrieved cards JSONObject: " + cardsJSON.toString(4));
        String cardText = cardsJSON.getString("text");

        //return card created with cardText
        return new Card(ID, cardText, type, -1);
    }//end getCardByID method

    /*
     * Gets a JSON Array of JSON Objects which contain card texts and irrelevant IDs
     *
     * @param name The name of the file to be retrieved
     * @return The JSONArray
     */
    public static JSONArray getCardsJSON(String name){

        if(cardsArray == null || cardsJSON.length() == 0){
            Log.i("getCardsJSON", "entering conditional");

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
}