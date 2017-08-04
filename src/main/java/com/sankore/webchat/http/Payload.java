package com.sankore.webchat.http;

import java.util.HashMap;

/**
 * Created by Tunji Longe on 7/4/2017.
 */
public class Payload extends HashMap<String, Object>{

    public Payload set(String key, Object value){
        this.put(key, value);
        return this;
    }

}
