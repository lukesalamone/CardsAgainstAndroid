package io.exis.cards.cards;

import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampClientBuilder;
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.net.URI;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.Request;
import ws.wamp.jawampa.connection.IWampConnectorProvider;

/*
 * RiffleSession.java
 *
 * Brokers interactions between server-side exec & dealer
 * and client players.
 *
 * Created by Luke Salamone on 10/20/2015.
 *
 * Copyright © 2015 exis. All rights reserved.
 *
 */

public class RiffleSession {

    public static void main(String[] args) {
        new RiffleSession().start();
    }

    Subscription addProcSubscription;
    Subscription eventPublication;
    Subscription eventSubscription;

    static final int eventInterval = 2000;
    int lastEventValue = 0;

    public void start() {

        URI serverUri = URI.create("ws://ec2-52-26-83-61.us-west-2.compute.amazonaws.com:8000/ws");

        IWampConnectorProvider connectorProvider = new NettyWampClientConnectorProvider();
        WampClientBuilder builder = new WampClientBuilder();

        final WampClient client;

        try {
            builder.withConnectorProvider(connectorProvider)
                    .withUri("ws://ec2-52-26-83-61.us-west-2.compute.amazonaws.com:8000/ws")
                    .withRealm("xs.luke")
                    .withInfiniteReconnects()
                    .withReconnectInterval(3, TimeUnit.SECONDS);
            client = builder.build();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        client.statusChanged().subscribe(new Action1<WampClient.State>() {
            @Override
            public void call(WampClient.State t1) {
                System.out.println("Session1 status changed to " + t1);

                if (t1 instanceof WampClient.ConnectedState) {
                    // Register a procedure
                    addProcSubscription = client.registerProcedure("com.example.add/k").subscribe(new Action1<Request>() {
                        @Override
                        public void call(Request request) {
                            if (request.arguments() == null || request.arguments().size() != 2
                                    || !request.arguments().get(0).canConvertToLong()
                                    || !request.arguments().get(1).canConvertToLong())
                            {
                                try {
                                    request.replyError(new ApplicationError(ApplicationError.INVALID_PARAMETER));
                                } catch (ApplicationError e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                long a = request.arguments().get(0).asLong();
                                long b = request.arguments().get(1).asLong();
                                request.reply(a + b);
                            }
                        }
                    });
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable t) {
                System.out.println("Session1 ended with error " + t);
            }
        }, new Action0() {
            @Override
            public void call() {
                System.out.println("Session1 ended normally");
            }
        });

        client.open();

        // Publish an event regularly
        eventPublication = Schedulers.computation().createWorker().schedulePeriodically(new Action0() {
            @Override
            public void call() {
                client.publish("test.event/k", "Hello " + lastEventValue);
                lastEventValue++;
            }
        }, eventInterval, eventInterval, TimeUnit.MILLISECONDS);

        waitUntilKeypressed();
        System.out.println("Stopping subscription");
        if (eventSubscription != null)
            eventSubscription.unsubscribe();

        waitUntilKeypressed();
        System.out.println("Stopping publication");
        eventPublication.unsubscribe();

        waitUntilKeypressed();

        waitUntilKeypressed();
        System.out.println("Closing the client 1");
        client.close().toBlocking().last();
    }//end start method
/*

    */
/*
     * Determine whether player is the czar right now
     *
     * @param PID - player id of player
     *//*

    public boolean isCzar(int PID){

    }//end isCzar method

    */
/*
     * Adds a player to a game. Should go to exec, who then
     * communicates with dealer.
     *
     * @return Player object added to dealer's game
     *//*

    public Player addPlayer(){

    }//end addPlayer method

    */
/*
     * Punt.
     *
     * @return Dealer for this player
     *//*

    public Dealer join(){

    }//end join method

    */
/*
     * Get the current question for this game
     *
     * @return Card with the current question
     *//*

    public Card getQuestion(){

    }//end getQuestion method

    */
/*
     * Get this player's hand
     *
     * @param   PID ID of player
     * @return  ArrayList of their cards
     *//*

    public ArrayList<Card> getHand(int PID){

    }//end getHand method

    */
/*
     * Player sending card to dealer
     *
     * @param   card    The card to be submitted
     * @return  true    Success
     * @return  false   Error
     *//*

    public boolean submit(Card card){

    }//end submit method

    */
/*
     * Removes player from room
     *
     * @param   player  The player
     *//*

    public void leave(Player player){

    }//end leave method

    */
/*
     * Dealer sends card to player
     *//*

    public void sendCard(int PID, Card card){

    }

    */
/*
     * Dealer receives card from player
     *//*

    public Card receiveCard(){

    }//end receiveCard method

    */
/*
     * Player draws card from dealer's deck
     *//*

    public Card drawCard(int PID){

    }

    */
/*
     * Report error to Exec
     *
     * @param   errID
     * @param   msg     Error message
     * @param   card    Card involved
     * @param   hand    The player's hand
     *//*

    public void reportError(int errID, int PID, String msg, Card card, ArrayList<Card> hand){

    }
*/

    private void waitUntilKeypressed() {
        try {
            System.in.read();
            while (System.in.available() > 0) {
                System.in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//end waitUntilKeypressed method
}