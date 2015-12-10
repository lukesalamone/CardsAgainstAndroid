package io.exis.cards.cards;

import android.util.Log;

import rx.Observable;
import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampClientBuilder;
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.net.URI;
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

public class WAMPWrapper {

    private static int playerID;
    private WampClient app;                       //application domain
    private WampClient user;                      //user domain
    Subscription addProcSubscription;
    Subscription eventPublication;
    Subscription eventSubscription;
    private ArrayList<Subscription> subscriptionList;

    private String URI;
    private String realm;
    static final int eventInterval = 2000;
    int lastEventValue = 0;

    public WAMPWrapper(String URI, String realm){
        IWampConnectorProvider connectorProvider = new NettyWampClientConnectorProvider();
        WampClientBuilder builder = new WampClientBuilder();
        //URI = "ws://ec2-52-26-83-61.us-west-2.compute.amazonaws.com:8000/ws";
        //realm = "xs.luke.Cards";

        //build application domain
        try {
            builder.withConnectorProvider(connectorProvider)
                    .withUri(URI)
                    .withRealm(realm)
                    .withInfiniteReconnects()
                    .withReconnectInterval(3, TimeUnit.SECONDS);
            app = builder.build();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        //build user domain
        try {
            builder.withConnectorProvider(connectorProvider)
                    .withUri(URI)
                    .withRealm(realm + "." + randUser())
                    .withInfiniteReconnects()
                    .withReconnectInterval(3, TimeUnit.SECONDS);
            user = builder.build();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void main(String[] args){
        String s = "a string";
        Log.i("info", "i is of type " + s.getClass());
    }

    /*
     * Return types cannot be guaranteed!
     */
    public void publish(String procedure, Object...args){
        app.publish(procedure, args)
                .subscribe(
                        publicationId -> { /* Event was published */ },
                        err -> { /* Error during publication */});
    }//end publish method

    /*
     * Client subscribes to server
     */
    public void subscribe(String procedure, Object...args){
        app.statusChanged()
                .subscribe((WampClient.State newState) -> {
                            if (newState instanceof WampClient.ConnectedState) {
                                // Client got connected to the remote router
                                // and the session was established
                            } else if (newState instanceof WampClient.DisconnectedState) {
                                // Client got disconnected from the remoute router
                                // or the last possible connect attempt failed
                            } else if (newState instanceof WampClient.ConnectingState) {
                                // Client starts connecting to the remote router
                            }
                        }
                );
    }//end subscribe method

    /*
     * As of now, we cannot guarantee return types.
     */
    public void register(String procedure){
        Subscription proc = app.registerProcedure(procedure).subscribe(
                request -> {
                    if (request.arguments() == null || request.arguments().size() != 1
                            || !request.arguments().get(0).canConvertToLong()){
                        try {
                            request.replyError(new ApplicationError(ApplicationError.INVALID_PARAMETER));
                        } catch (ApplicationError e) {
                            //error reporting the error
                        }
                    } else {
                        long a = request.arguments().get(0).asLong();
                        request.reply(a);
                    }
                },
                e -> System.err.println(e));
    }//end register method

    /*
     * Cannot guarantee return types!
     */
    public void call(String procedure, Object...args){
        Observable result = app.call(procedure, args);
        // Subscribe for the result
        // onNext will be called in case of success with a String value
        // onError will be called in case of errors with a Throwable
        result.subscribe((txt) -> System.out.println(txt),
                (err) -> System.err.println(err));
    }//end call method

    /*
     * Unsubscribe from a procedure subscription
     */
    public void unsub(String procedure){
        Subscription proc = findSubscription(procedure);
        proc.unsubscribe();
    }//end unsub method

    public void unreg(){

    }//end unreg method

    /*
     * Closes connection to server. Close() MUST be called on session end.
     */
    public void close(){
        user.close().toBlocking().last();
    }//end close method

    /*
     * Finds and returns a procedure by its name from procedureList
     */
    private Subscription findSubscription(String name){
        for(Subscription s : subscriptionList){
            //TODO
            return s;
        }

        Log.wtf("findSubscription", "subscription " + name + " not found!");
        return null;
    }//end findProcedure method

    private static String randUser(){
        return "user" + Math.random()*1000000;
    }//end randUser method

}//end WAMPWrapper class