package com.sankore.webchat.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;

/**
 * Created by Olatunji on 9/11/2016.
 */
public class Attributes extends LinkedHashMap<String, String> {

    public static final Attributes INSTANCE = new Attributes();

    private Attributes(){
    }

    public Attributes assign(String key, String value){
        this.put(key, value);
        return this;
    }

    public String applyTo(String text) {
        for(String key: this.keySet()){
            text = StringUtils.replace(text, key, this.get(key));
        }
        return text;
    }
}
