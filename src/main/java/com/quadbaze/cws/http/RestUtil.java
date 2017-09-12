package com.quadbaze.cws.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Created by Tunji Longe on 7/7/2017.
 */
public class RestUtil {

    public static JsonObject init() {
        return response(org.apache.http.HttpStatus.SC_ACCEPTED, "");
    }

    public static JsonObject response(HttpStatus status, Object object) {
        return buildJsonResponse(status.value(), convertToJson(object));
    }

    public static JsonObject response(int statusCode, Object object) {
        return buildJsonResponse(statusCode, convertToJson(object));
    }

    public static JsonObject success(Object object) {
        return buildJsonResponse(HttpStatus.OK.value(), convertToJson(object));
    }

    public static JsonObject exception(Exception ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : (ex.getCause() != null ? ex.getCause().getMessage() : HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        return buildJsonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), convertToJson(message));
    }

    private static JsonElement convertToJson(Object object){
        JsonElement jsonElement;
        if(object != null){
            if(object.getClass().isAssignableFrom(JsonElement.class)){
                jsonElement = (JsonElement)object;
            }else if(object.getClass().isAssignableFrom(String.class)) {
                jsonElement = new JsonPrimitive((String.valueOf(object)));
            }else{
                jsonElement = GsonUtil.toJson(object);
            }
        }else{
            jsonElement = JsonNull.INSTANCE;
        }
        return jsonElement;
    }

    private static JsonObject buildJsonResponse(int statusCode, JsonElement jsonElement){
        JsonObject json = new JsonObject();
        json.addProperty(ResponseParam.code, statusCode);
        if(statusCode == HttpStatus.OK.value()){
            json.add(ResponseParam.result, jsonElement);
        }else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()){
            json.add(ResponseParam.error, jsonElement);
        }else{
            json.add(ResponseParam.message, jsonElement);
        }
        return json;
    }

}
