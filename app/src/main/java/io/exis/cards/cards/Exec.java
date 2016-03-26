package io.exis.cards.cards;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.exis.riffle.Domain;

import java.util.ArrayList;

/**
 * Exec.java
 * Manages creation/deletion of rooms and authentication of users
 *
 * Created by luke on 10/15/15.
 */
public class Exec extends Domain{

    static ArrayList<Dealer> dealers = new ArrayList<>();
    private Player player;
    private RiffleSession riffle;
    private GameActivity activity;
    private Domain domain;

    public Player externalPlayer;

    public Exec() {
        super("Exec", new Domain("xs.damouse.CardsAgainst"));
    }

    @Override
    public void onJoin(){
        register("play", Object[].class, Exec::play);
        externalPlayer.join();
    }

    public static Object[] play(){
        return findDealer().play();
    }

    public Domain getGame(){
        return this;
    }// end getGame method

    public static void removeDealer(Dealer dealer){
        dealers.remove(dealer);
    }

    //finds a dealer not at max capacity
    public static Dealer findDealer(){
        for(int i=0; i<dealers.size(); i++){
            if(!dealers.get(i).full()){
                return dealers.get(i);
            }
        }

        Dealer dealer = addDealer();
        Log.i("Exec::findDealer", "found dealer " + dealer.ID());
        dealer.join();
        Log.i("Exec::findDealer", "dealer " + dealer.ID() + " joining");
        dealer.start();

        return dealer;
    }// end findDealer method

    public static int getNewID(){
        return (int) (Math.random() * Integer.MAX_VALUE);
    }// end getNewID method

    //create new dealer and add to dealer list
    //return dealer ID
    private static Dealer addDealer(){
        Dealer dealer = new Dealer(getNewID());
        dealers.add(dealer);
        return dealer;
    }//end addDealer method
}//end Exec class