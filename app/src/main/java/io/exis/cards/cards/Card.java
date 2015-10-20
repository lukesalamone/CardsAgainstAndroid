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
import java.util.Scanner;

public class Card {

    int ID;
    String text;
    char type;
    int PID;
    JSONObject cardsJSON = new JSONObject();
    Context context;

    //it will be difficult to add or remove questions...
    private final int numQuestions21 = 36;
    private final int numQuestions13 = 27;

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
        if(this.getType() != card.getType()){
            return false;
        }
        return true;
    }

    //returns a Card from an ID. When R is true return normal card set
    public Card getCardByID(int ID, boolean R) throws JSONException{

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

        Card card = new Card(ID, cardText, type, getPID());

        return card;
    }//end getCardByID method

    //load file into string and return it
    private String getCardString(String name) {

        int resID = context.getResources().getIdentifier(name, "values", context.getPackageName());

        Scanner fileIn = new Scanner(context.getResources().openRawResource(resID));

        return fileIn.nextLine();

    }//end getCardString method

    private JSONObject getCardsJSON(String name){

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

}