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

    public Exec(GameActivity activity){
        super("Exec", new Domain("xs.damouse.CardsAgainst"));
        Log.i("Exec", "entering constructor");
        this.activity = activity;
        Log.i("Exec constructor", "Exec is joining");
        join();
    }

    @Override
    public void onJoin(){
        String TAG = "Exec::onJoin()";
        register("play", Object[].class, Exec::play);

        activity.player = new Player(getNewID(), "", new Domain("xs.damouse.CardsAgainst"));
        Log.i(TAG, "creating new riffle session");
        activity.riffle = new RiffleSession(player.domain(), this);

        Log.i(TAG, "calling Exec::play");
        Object[] playObject = play();
        if(playObject == null){
            Log.wtf(TAG, "play object is null!");
        }

        try {
            activity.hand = (String[]) playObject[0];
        }catch(NullPointerException e){
            Log.wtf(TAG, "hand is null!");
        }

        activity.players = (Player[]) playObject[1];
        activity.state = (String) playObject[2];
        activity.roomName = (String) playObject[3];

        player.setDealer(activity.roomName);

        Log.i(TAG, "setting player");
        riffle.setPlayer(player);

        Log.i(TAG, "joining game");
        activity.player.join();

        activity.setQuestion();                              //set question TextView
        activity.showCards();
        Log.i(TAG, "playing online game");
        activity.playOnlineGame();


    }

    // TODO offline case in GameActivity
    public static void join(Player player){

    }

    public static Object[] play(){
        return findDealer(true).play();
    }

    public Domain getGame(){
        return this;
    }// end getGame method

    public static void removeDealer(Dealer dealer){
        dealers.remove(dealer);
    }

    //finds a dealer not at max capacity
    public static Dealer findDealer(boolean online){
        for(int i=0; i<dealers.size(); i++){
            if(!dealers.get(i).full()){
                return dealers.get(i);
            }
        }

        Dealer dealer = addDealer();
        Log.i("Exec::findDealer", "found dealer " + dealer.ID());

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

    public static void addPoint(Player player){

    }

}//end Exec class

