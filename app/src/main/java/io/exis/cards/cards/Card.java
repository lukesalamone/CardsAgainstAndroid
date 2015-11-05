package io.exis.cards.cards;

/**
 *
 * Card.java
 * Create a card from a card ID
 *
 * NOTE it will be difficult to easily extend the number of cards due to the
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
    static JSONObject cardsJSON = new JSONObject();
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
        ArrayList<Card> questions = new ArrayList<>();

        try {
            //R questions number 494 - 2889
            if (R) {
                for (int i = 494; i <= 2889; i++) {
                    questions.add(getCardByID(i, R, 'q'));
                }
            //pg13 questions number 28 - 35
            } else {
                for(int i = 28; i <= 35; i++){
                    questions.add(getCardByID(i, R, 'q'));
                }
            }
        } catch(JSONException e){
            throw new RuntimeException(e);
        }

        return questions;
    }//end getQuestions method

    public static ArrayList<Card> getAnswers(boolean R){
        ArrayList<Card> answers = new ArrayList<>();

        try {
            //R answers number from 36 to 2864
            if (R) {
                for (int i = 36; i <= 2864; i++) {
                    answers.add(getCardByID(i, R, 'a'));
                }
            //pg13 answers number from 0 - 27
            } else {
                for(int i = 0; i <= 27; i++){
                    answers.add(getCardByID(i, R, 'a'));
                }
            }
        } catch(JSONException e){
            throw new RuntimeException(e);
        }

        return answers;
    }//end getAnswers method

    //returns a Card from an ID. When R is true return normal card set
    //Card cannot set PID! Receiving party must set it.
    public static Card getCardByID(int ID, boolean R, char type) throws JSONException{

        if(R){
            //requesting a question card
            if(type == 'q'){
                cardsJSON = getCardsJSON("q21");
                //requesting an answer card
            } else {
                cardsJSON = getCardsJSON("a21");
            }
        } else { //pg-13 card set
            if(type == 'q'){
                cardsJSON = getCardsJSON("q13");
            } else {
                cardsJSON = getCardsJSON("a13");
            }
        }

        String cardText = cardsJSON.names().getString(ID);

        Card card = new Card(ID, cardText, type, -1);

        return card;
    }//end getCardByID method

    public static JSONObject getCardsJSON(String name){

        Log.i("getCardsJSON", "cardsJSON has length " + cardsJSON.length());

        if(cardsJSON.length() == 0){
            Log.i("getCardsJSON", "entering conditional");

            String cardString = MainActivity.getCardString(name);

            try {
                cardsJSON = new JSONObject(cardString);
                return cardsJSON;
            } catch (JSONException e){
                Log.wtf("Card::getCardsJson", "JSON exception thrown");
                throw new RuntimeException(e);
            }
        }

        return cardsJSON;

    }//end getCardJSON method

    /*
     *        MAY NOT NEED THESE...
     */

    //from http://stackoverflow.com/questions/12910503/read-file-as-string
    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = streamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public static String streamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
}