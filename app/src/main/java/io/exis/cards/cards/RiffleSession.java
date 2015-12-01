package io.exis.cards.cards;

import junit.framework.Assert;
import org.jdeferred.DoneCallback;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;

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

    protected AndroidDeferredManager manager = new AndroidDeferredManager();

    public void testDeferredAsyncTask() {
        final ValueHolder<String> backgroundThreadGroupName = new ValueHolder<String>();
        final ValueHolder<String> doneThreadGroupName = new ValueHolder<String>();

        try {
            manager.when(new DeferredAsyncTask<Void, Integer, String>() {
                @Override
                protected String doInBackgroundSafe(Void... nil) throws Exception {
                    backgroundThreadGroupName.set(Thread.currentThread()
                            .getThreadGroup().getName());
                    return "Done";
                }
            }).done(new DoneCallback<String>() {

                @Override
                public void onDone(String result) {
                    doneThreadGroupName.set(Thread.currentThread()
                            .getThreadGroup().getName());
                }

            }).waitSafely();
        } catch (InterruptedException e) {
            // Do nothing
        }

        doneThreadGroupName.assertEquals("main");
        Assert.assertFalse(
                String.format(
                        "Background Thread Group [%s] shouldn't be the same as Thread Group in Done [%s]",
                        backgroundThreadGroupName.get(),
                        doneThreadGroupName.get()), backgroundThreadGroupName
                        .equals(doneThreadGroupName));
    }

    public int getNewID(){
        final ValueHolder<Integer> ID = new ValueHolder<>();

        try {
            manager.when(new DeferredAsyncTask<Void, Integer, String>() {
                @Override
                protected String doInBackgroundSafe(Void... nil) throws Exception {
                    //insert RPC call here

                    //ID.set( ...something... );

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

        return ID.get().intValue();
    }//end getNewID function

}