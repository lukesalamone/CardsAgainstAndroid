package io.exis.cards.cards;

import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampClientBuilder;
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider;

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

        // Build two clients
        final WampClient client1;
        //final WampClient client2;

        try {
            builder.withConnectorProvider(connectorProvider)
                    .withUri("ws://ec2-52-26-83-61.us-west-2.compute.amazonaws.com:8000/ws")
                    .withRealm("xs.luke")
                    .withInfiniteReconnects()
                    .withReconnectInterval(3, TimeUnit.SECONDS);
            client1 = builder.build();
            client2 = builder.build();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        client1.statusChanged().subscribe(new Action1<WampClient.State>() {
            @Override
            public void call(WampClient.State t1) {
                System.out.println("Session1 status changed to " + t1);

                if (t1 instanceof WampClient.ConnectedState) {
                    // Register a procedure
                    addProcSubscription = client1.registerProcedure("com.example.add/k").subscribe(new Action1<Request>() {
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

        client2.statusChanged().subscribe(new Action1<WampClient.State>() {
            @Override
            public void call(WampClient.State t1) {
                System.out.println("Session2 status changed to " + t1);

                if (t1 instanceof WampClient.ConnectedState) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) { }
                    // Call the procedure
                    Observable<Long> result1 = client2.call("com.example.add/k", Long.class, 33, 66);
                    result1.subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long t1) {
                            System.out.println("Completed add with result " + t1);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable t1) {
                            System.out.println("Completed add with error " + t1);
                        }
                    });

                    // Call the procedure with invalid values
                    Observable<Long> result2 = client2.call("com.example.add/k", Long.class, 1, "dafs");
                    result2.subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long t1) {
                            System.out.println("Completed add with result " + t1);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable t1) {
                            System.out.println("Completed add with error " + t1);
                        }
                    });

                    eventSubscription = client2.makeSubscription("test.event/k", String.class)
                            .subscribe(new Action1<String>() {
                                @Override
                                public void call(String t1) {
                                    System.out.println("Received event test.event with value " + t1);
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable t1) {
                                    System.out.println("Completed event test.event with error " + t1);
                                }
                            }, new Action0() {
                                @Override
                                public void call() {
                                    System.out.println("Completed event test.event");
                                }
                            });

                    // Subscribe on the topic

                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable t) {
                System.out.println("Session2 ended with error " + t);
            }
        }, new Action0() {
            @Override
            public void call() {
                System.out.println("Session2 ended normally");
            }
        });

        client1.open();
        client2.open();

        // Publish an event regularly
        eventPublication = Schedulers.computation().createWorker().schedulePeriodically(new Action0() {
            @Override
            public void call() {
                client1.publish("test.event/k", "Hello " + lastEventValue);
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
        client1.close().toBlocking().last();

        waitUntilKeypressed();
        System.out.println("Closing the client 2");
        client2.close().toBlocking().last();
    }

    private void waitUntilKeypressed() {
        try {
            System.in.read();
            while (System.in.available() > 0) {
                System.in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}