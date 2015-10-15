package io.exis.cards.cards;

import java.util.ArrayList;
import java.util.List;

/**
 * Exec.java
 * Manages creation/deletion of rooms and authentication of users
 *
 * Created by luke on 10/15/15.
 */
public class Exec {

    ArrayList<Dealer> dealers = new ArrayList<>();

    int counter = 0;

    public Exec(){

    }//end Exec constructor

    //create new dealer and add to dealer list
    //return dealer ID
    public Dealer addDealer(boolean R){
        Dealer dealer = new Dealer(R, getNewID());
        dealers.add(dealer);
        return dealer;
    }//end addDealer method

    public void removeDealer(Dealer dealer){
        dealers.remove(dealer);
    }

    public void addPlayer(boolean R){
        Player newbie = new Player(getNewID(),
                new ArrayList<Card>(),
                false);
        findDealer(R).addPlayer(newbie);
    }

    //finds a dealer of appropriate game not at max capacity
    public Dealer findDealer(boolean R){
        //look for an open rating-appropriate dealer
        for(int i=0; i<dealers.size(); i++){
            if(dealers.get(i).getRating() == R && !dealers.get(i).full()){
                return dealers.get(i);
            }
        }

        return new Dealer(R, getNewID());
    }//end findDealer method

    public boolean auth(String user, String pass){

        /*
         * MAGIC GOES HERE
         */
        return true;
    }//end auth method

    public int getNewID(){
        return counter++;
    }//end getNewID method

    public double generateKey(){
        double key;
        double rand = Math.random()*Double.MAX_VALUE;

        //get SYS time
        double time = System.currentTimeMillis();;

        //multiply by random number
        key = time*rand;

        return (int)key;
    }

}
