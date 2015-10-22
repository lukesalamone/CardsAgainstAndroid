package com.exis.riffletest;


import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampClientBuilder;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider;

import java.util.concurrent.TimeUnit;

/**
 * Created by damouse on 10/22/2015.
 */

public class CLITester {
    static final String URL = "ws://ec2-52-26-83-61.us-west-2.compute.amazonaws.com:8000/ws";

    public static void main(String[] args) {
        System.out.println("Starting wamp testing");

        ClientTester tester = new ClientTester();
//        tester.trySubscribe();
    }

}

class ClientTester {
    WampClient client;
    NettyWampClientConnectorProvider provider = new NettyWampClientConnectorProvider();

    public ClientTester() {
        try {
            // Create a builder and configure the client
            WampClientBuilder builder = new WampClientBuilder();
            builder.withConnectorProvider(provider)
                    .withUri("ws://ec2-52-26-83-61.us-west-2.compute.amazonaws.com:8000/ws")
                    .withRealm("xs.javatester")
                    .withInfiniteReconnects()
                    .withReconnectInterval(5, TimeUnit.SECONDS);

            // Create a client through the builder. This will not immediatly start
            // a connection attempt
            client = builder.build();
        } catch (Exception e) {
            // Catch exceptions that will be thrown in case of invalid configuration
            System.out.println(e);
            return;
        }
    }

//    void trySubscribe() {
//        client.statusChanged()
//                .observeOn(provider.createScheduler())
//                .subscribe((WampClient.State newState) -> {
//                    if (newState instanceof WampClient.ConnectedState) {
//                        // Client got connected to the remote router
//                        // and the session was established
//                    } else if (newState instanceof WampClient.DisconnectedState) {
//                        // Client got disconnected from the remoute router
//                        // or the last possible connect attempt failed
//                    } else if (newState instanceof WampClient.ConnectingState) {
//                        // Client starts connecting to the remote router
//                    }});
//    }
}