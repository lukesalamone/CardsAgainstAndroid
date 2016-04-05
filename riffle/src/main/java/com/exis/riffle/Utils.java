package com.exis.riffle;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import go.mantle.Mantle;

public class Utils {
    private static Random generator = new Random();
    private static Gson gson = new GsonBuilder().create();

    static BigInteger newID() {
        return new BigInteger(53, generator);
    }

    /**
     * Marshall the given arguments, preparing them for transmission to the core.
     * @return
     */
    static String marshall(Object[] args) {
        return gson.toJson(args);
    }

    static Object[] unmarshall(String json) {
//        Object[] result = gson.fromJson(json, Object[].class);
//        Riffle.debug("Json from core: " + json + " after: " + result.toString());
        return gson.fromJson(json, Object[].class);
    }

    static BigInteger convertCoreInt64(Object o) {
        //Riffle.debug("Converting object: " + o.toString() + " Cast as double: " + t + " long value: " + t.longValue() + " BigInt: " + BigInteger.valueOf(t.longValue()).toString());
        BigInteger id = BigInteger.valueOf(((Double) o).longValue());
        return id;
    }

    /*
     * Serialize objects for transmission. Most objects implement serializable, but uses toString()
     * if not.
     *
     * Returns empty byte array on null param.
     */
    static byte[] serializeCoreBytes(Object o){
        if(o == null){
            Riffle.debug("Attempt to serialize null object");
            Log.e("SerializeCoreBytes", "Object is null");
            return new byte[]{};
        }

        if(o instanceof Serializable) {
            try{
                ByteArrayOutputStream a = new ByteArrayOutputStream();
                ObjectOutputStream b = new ObjectOutputStream(a);
                b.writeObject(o);
                return a.toByteArray();
            }catch(IOException e){
                Riffle.debug("IOException thrown in Utils::serializeCoreBytes");
                Log.e("SerializeCoreBytes", "IOException thrown");
            }
        }
        return o.toString().getBytes();
    }
}
