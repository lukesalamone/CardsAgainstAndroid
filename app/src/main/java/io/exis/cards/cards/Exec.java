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
    static int counter = 0;
    static Domain Game = new Domain("xs.damouse.CardsAgainst");
    Receiver receiver = new Receiver("xs.damouse.CardsAgainst", Game);
    receiver.join();

    public static Domain getGame(){
        return Game;
    }// end getGame method

    public void removeDealer(Dealer dealer){
        dealers.remove(dealer);
    }

    //finds a dealer of appropriate game not at max capacity
    public static Dealer findDealer(){
        //look for an open rating-appropriate dealer
        for(int i=0; i<dealers.size(); i++){
            if(!dealers.get(i).full()){
                return dealers.get(i);
            }
        }

        Dealer dealer = addDealer(false);

        // connect to Exec
        Sender sender = new Sender("dealer" + dealer.getID(), Game);
        senders.add(sender);


        return dealer;
    }// end findDealer method

    public static int getNewID(){
        return counter++;
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
    private static Dealer addDealer(boolean R){
        Dealer dealer = new Dealer(R, getNewID());
        dealers.add(dealer);
        return dealer;

        //TODO register all calls
    }//end addDealer method

    public static void addPoint(Player player){

    }

}

// Exec is receiver in this case
class Receiver extends Domain {
    private static final String TAG = "Receiver";
    public MainActivity parent;

    // I REALLY have to do this? Come on, java
    // Create these without needing to override the default constructor...
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

        // TODO add Exec subscribes

        // TODO add Exec registers
    }
}// end Receiver class

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
