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
import android.provider.MediaStore;

import org.json.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;

public class Card {

    int ID;
    String text;
    char type;

    //Every card has an ID, associated text, and type
    //Type may be 'q' for question or 'a' for answer
    public Card(int cardID, String cardText, char cardType){

        ID = cardID;
        text = cardText;
        type = cardType;

    }

    public int getID(){
        return this.ID;
    }//end getID method

    public String getText(){
        return this.text;
    }//end getID method

    public char getType(){
        return this.type;
    }//end getType method

    //returns a Card from an ID.
    //When R is true return normal card set
    //When R is false return PG13 card set
    public Card getCardByID(int ID, boolean R){

        if(R){

        } else {

        }

    }//end getCardByID method

    //load file into string and return it
    private String getCardString(String path) throws IOException{
        /*
        FileInputStream stream;
        stream = Activity.openFileInput("test.txt");
        StringBuffer fileContent = new StringBuffer("");

        byte[] buffer = new byte[1024];

        int n = stream.read(buffer);
        while (n != -1)
        {
            fileContent.append(new String(buffer, 0, n));
        }
        */


        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, "UTF-8");

    }//end getCardString method

}