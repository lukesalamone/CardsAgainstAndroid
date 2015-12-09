package io.exis.cards.cards;

import android.util.Log;

import junit.framework.Assert;
import org.jdeferred.DoneCallback;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;
import com.thetransactioncompany.jsonrpc2.*;
import com.thetransactioncompany.jsonrpc2.client.*;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * RiffleSession.java
 *
 * Let's try this again with jdeferred.
 *
 * Brokers interactions between server-side exec & dealer
 * and client players.
 *
 * Adapted from https://github.com/jdeferred by Luke Salamone on 12/1/2015.
 *
 * Copyright © 2015 exis. All rights reserved.
 *
 */

public class RiffleSession {

    private URL serverURL;
    private JSONRPC2Session session;
    protected AndroidDeferredManager manager;

    //Constructor
    RiffleSession(){
        manager = new AndroidDeferredManager();
        try{
            serverURL = new URL("ws://ec2-52-26-83-61.us-west-2.compute.amazonaws.com:8000/ws");
            session = new JSONRPC2Session(serverURL);
        } catch (MalformedURLException e) {
            Log.wtf("RiffleSession constructor", "MalformedURLException thrown");

            //do some interface things...
        }
    }

    /*
     * Called in GameActivity. Must return result of call to Exec::getNewID()
     *
     * Needs to be reworked using wrapper for Jawampa. Not JSONRPC.
     */
    public int getNewID(){
        final ValueHolder<Integer> ID = new ValueHolder<>();
        final int methodID = 0;                             //TODO: Create index of method IDs
        final String method = "getNewID";

        try {
            manager.when(new DeferredAsyncTask<Void, Integer, String>() {
                @Override
                protected String doInBackgroundSafe(Void... nil) throws Exception {
                    //insert RPC call here
                    JSONRPC2Request request = new JSONRPC2Request(method, methodID);
                    JSONRPC2Response response;

                    //send to server...
                    try {
                        response = session.send(request);
                    } catch (JSONRPC2SessionException e) {
                        Log.wtf("getNewID", e.getMessage());
                        return "Error";
                        // handle exception...
                    }

                    //insert magic

                    //receive from server
                    String responseString = (String)response.getResult();
                    if(response.indicatesSuccess()) {
                        ID.set(Integer.parseInt( responseString ));
                    } else{
                        ID.set(0);
                    }

                    return "Done";
                }
            }).done(new DoneCallback<String>() {

                @Override
                public void onDone(String result) {
                    //do nothing
                }

            }).waitSafely();
        } catch (InterruptedException e) {
            // Do nothing
        }

        return ID.get();
    }//end getNewID function

}