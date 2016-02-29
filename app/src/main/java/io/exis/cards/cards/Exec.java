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
public class Exec {

    static ArrayList<Dealer> dealers = new ArrayList<>();
    static ArrayList<Sender> senders = new ArrayList<>();                   // dealers are senders
    static Domain Game = new Domain("xs.damouse.CardsAgainst");

    public static Domain getGame(){
        return Game;
    }// end getGame method

    public void removeDealer(Dealer dealer){
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

        if(online){
            // connect to Exec
            Sender sender = new Sender("dealer" + dealer.ID(), Game);
            senders.add(sender);
        }

        return dealer;
    }// end findDealer method

    public static int getNewID(){
        return (int) (Math.random() * Integer.MAX_VALUE);
    }// end getNewID method

    // probably won't need this...
    public double generateKey(){
        double key;
        double rand = Math.random()*Double.MAX_VALUE;

        //get SYS time
        double time = System.currentTimeMillis();

        //multiply by random number
        key = time*rand;

        return (int)key;
    }

    //create new dealer and add to dealer list
    //return dealer ID
    private static Dealer addDealer(){
        Dealer dealer = new Dealer(getNewID());
        dealers.add(dealer);
        return dealer;

        // TODO register all endpoints
    }//end addDealer method

    public static void addPoint(Player player){

    }

}

// Exec is receiver in this case
class Receiver extends Domain {
    private static final String TAG = "Receiver";
    public MainActivity parent;

    public Receiver(String name) {
        super(name);
    }

    public Receiver(String name, Domain superdomain) {
        super(name, superdomain );
    }

    public void join(){
        onJoin();
    }

    @Override
    public void onJoin() {
        Log.d(TAG, "Receiver joined!");
        Dealer dealer = Exec.findDealer(false);

        // TODO add Exec registers
        //Exec.getGame().register("/join", Player.class, (player)->dealer.addPlayer(player));
    }
}// end Receiver class

// dealers are all senders
class Sender extends Domain {
    private static final String TAG = "Sender";
    public MainActivity parent;

    // I REALLY have to do this? Come on, java
    public Sender(String name) {
        super(name);
    }

    public Sender(String name, Domain superdomain) {
        super(name, superdomain);
    }

    @Override
    public void onJoin() {
        Log.d(TAG, "Sender joined!");

        // TODO pubsub

    }
}
