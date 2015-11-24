package io.exis.cards.cards;

import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampClientBuilder;
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider;
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

public class RiffleSession {

    private static int playerID;
    private WampClient app;                       //application domain
    private WampClient user;                      //user domain
    Subscription addProcSubscription;
    Subscription eventPublication;
    Subscription eventSubscription;

    static final int eventInterval = 2000;
    int lastEventValue = 0;

    public RiffleSession(){
        URI serverUri = URI.create("ws://ec2-52-26-83-61.us-west-2.compute.amazonaws.com:8000/ws");
        IWampConnectorProvider connectorProvider = new NettyWampClientConnectorProvider();
        WampClientBuilder builder = new WampClientBuilder();

        //build application domain
        try {
            builder.withConnectorProvider(connectorProvider)
                    .withUri("ws://ec2-52-26-83-61.us-west-2.compute.amazonaws.com:8000/ws")
                    .withRealm("xs.luke.Cards")
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
                    .withUri("ws://ec2-52-26-83-61.us-west-2.compute.amazonaws.com:8000/ws")
                    .withRealm("xs.luke.Cards.u" + Math.random()*1000000)
                    .withInfiniteReconnects()
                    .withReconnectInterval(3, TimeUnit.SECONDS);
            user = builder.build();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public static void main(String[] args) {
        new RiffleSession().start();
    }

    public void start() {
        app.statusChanged().subscribe(new Action1<WampClient.State>() {
            @Override
            public void call(WampClient.State t1) {
                System.out.println("Session1 status changed to " + t1);

                if (t1 instanceof WampClient.ConnectedState) {
                    // Register a procedure
                    addProcSubscription = app.registerProcedure("com.example.add/k").subscribe(new Action1<Request>() {
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

        app.open();

        // Publish an event regularly
        eventPublication = Schedulers.computation().createWorker().schedulePeriodically(new Action0() {
            @Override
            public void call() {
                app.publish("test.event/k", "Hello " + lastEventValue);
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
        app.close().toBlocking().last();
    }//end start method

    /*
     * Implements WAMP call to Exec.getNewID()
     */
    public int getNewID(){
        app.statusChanged().subscribe(new Action1<WampClient.State>() {
            int ID;
            @Override
            public void call(WampClient.State t1) {
                System.out.println("Session1 status changed to " + t1);

                if (t1 instanceof WampClient.ConnectedState) {
                    // Register a procedure
                    addProcSubscription = app.registerProcedure("io.exis.cards/getNewID").subscribe(new Action1<Request>() {
                        @Override
                        public void call(Request request) {
                            //getNewID() takes no arguments
                            if (request.arguments() == null || request.arguments().size() != 0){
                                try {
                                    request.replyError(new ApplicationError(ApplicationError.INVALID_PARAMETER));
                                } catch (ApplicationError e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                ID = Exec.getNewID();
                                request.reply(ID);
                            }
                        }
                    });
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable t) {
                System.out.println("getNewID() ended with error " + t);
            }
        }, new Action0() {
            @Override
            public void call() {
                System.out.println("getNewID() ended normally");
            }
        });

        return 0;
    }//end getNewID method

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