package com.sankore.webchat.http;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tunji Longe on 7/5/2017.
 */
public class GsonDeserializer implements JsonDeserializer<Object> {

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return doDeserialization(json);
    }

    private static Object doDeserialization(JsonElement json) {
        if (json.isJsonNull()) {
            return null;
        } else if (json.isJsonObject()) {
            return handleJsonObject(json);
        } else if (json.isJsonArray()) {
            return handleJsonArray(json);
        } else {
            // must be primitive
            return handleJsonPrimitive(json);
        }
    }

    private static Object handleJsonPrimitive(JsonElement json) {
        JsonPrimitive primitive = json.getAsJsonPrimitive();
        if (primitive.isBoolean()) {
            return primitive.getAsBoolean();
        } else if (primitive.isString()) {
            return primitive.getAsString();
        } else {
            // else primitive is number, but the Type of number is yet unknown
            String str = primitive.getAsString();
            try {
                return new BigInteger(str);
            } catch (NumberFormatException ex) {
                // then it must be a decimal
                return new BigDecimal(str);
            }
        }
    }

    private static Map<String, Object> handleJsonObject(JsonElement json) {
        JsonObject jsonObject = json.getAsJsonObject();
        Map<String, Object> map = new HashMap<String, Object>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            map.put(key, doDeserialization(value));
        }
        return map;
    }

    private static List<Object> handleJsonArray(JsonElement json) {
        JsonArray array = json.getAsJsonArray();
        List<Object> list = new ArrayList<Object>(array.size());
        for (JsonElement element : array) {
            list.add(doDeserialization(element));
        }
        return list;
    }
}

