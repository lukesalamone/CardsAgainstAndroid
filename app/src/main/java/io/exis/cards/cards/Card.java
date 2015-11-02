package io.exis.cards.cards;

/**
 *
 * Card.java
 * Create a card from a card ID
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

import java.util.ArrayList;
import java.util.Scanner;

public class Card {

    int ID;
    String text;
    char type;
    int PID;
    static JSONObject cardsJSON = new JSONObject();
    static Context context;

    //it will be difficult to add or remove questions...
    private static final int numQuestions21 = 36;
    private static final int numQuestions13 = 27;

    //Every card has an ID, associated text, and type
    //Type may be 'q' for question or 'a' for answer
    public Card(int cardID, String cardText, char cardType, int playerID){
        ID = cardID;
        text = cardText;
        type = cardType;
        PID = playerID;
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

    //returns a Card from an ID. When R is true return normal card set
    //Card cannot set PID! Receiving party must set it.
    public static Card getCardByID(int ID, boolean R) throws JSONException{

        char type;

        if(R){
            //requesting a question card
            if(ID < numQuestions21){
                cardsJSON = getCardsJSON("q21");
                type = 'q';
            //requesting an answer card
            } else {
                cardsJSON = getCardsJSON("a21");
                type = 'a';
            }
        } else {
            if(ID < numQuestions13){
                cardsJSON = getCardsJSON("q13");
                type = 'q';
            } else {
                cardsJSON = getCardsJSON("a13");
                type = 'a';
            }
        }

        String cardText = cardsJSON.names().getString(ID);

        Card card = new Card(ID, cardText, type, -1);

        return card;
    }//end getCardByID method

    public static ArrayList<Card> getQuestions(boolean R){
        ArrayList<Card> questions = new ArrayList<>();

        try {
            //R questions number 494 - 2889
            if (R) {
                for (int i = 494; i <= 2889; i++) {
                    questions.add(getCardByID(i, R));
                }
            //pg13 questions number 28 - 35
            } else {
                for(int i = 28; i <= 35; i++){
                    questions.add(getCardByID(i, R));
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
                    answers.add(getCardByID(i, R));
                }
            //pg13 answers number from 0 - 27
            } else {
                for(int i = 0; i <= 27; i++){
                    answers.add(getCardByID(i, R));
                }
            }
        } catch(JSONException e){
            throw new RuntimeException(e);
        }

        return answers;
    }

    private static JSONObject getCardsJSON(String name){

        if(cardsJSON.length() == 0){
            String cardString = getCardString(name);
            try {
                cardsJSON = new JSONObject(cardString);
                return cardsJSON;
            } catch (JSONException e){
                throw new RuntimeException(e);
            }
        }

        return cardsJSON;

    }//end getCardJSON method

    //load file into string and return it
    private static String getCardString(String name) {

        int resID = context.getResources().getIdentifier(name, "values", context.getPackageName());

        Scanner fileIn = new Scanner(context.getResources().openRawResource(resID));

        return fileIn.nextLine();

    }//end getCardString method

}