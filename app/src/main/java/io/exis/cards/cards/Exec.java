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

    public Exec(){

    }//end Exec constructor

    //create new dealer and add to dealer list
    //return dealer ID
    public Dealer addDealer(boolean R){
        Dealer dealer = new Dealer(R);
        dealers.add(dealer);
        return dealer;
    }//end addDealer method

    public void removeDealer(Dealer dealer){
        dealers.remove(dealer);
    }

    //finds a dealer of appropriate game not at max capacity
    public Dealer findDealer(boolean R){

    }//end findDealer method

}
