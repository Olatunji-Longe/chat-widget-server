package com.sankore.webchat.http;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class GsonUtil {

	public static JsonElement parse(String jsonString) {
		try {
			JsonElement jsonElement = new JsonParser().parse(jsonString);
			return jsonElement.isJsonNull() ? null : jsonElement;
		} catch (JsonParseException ex) {
			return null;
		}
	}

	public static boolean isJsonValid(String jsonString) {
		return parse(jsonString) != null;
	}

	public static Gson gson() {
		return new GsonBuilder().registerTypeAdapter(Map.class, new GsonDeserializer()).create();
	}

	public static Map<String, Object> toMap(Object object) {
		Type type = new TypeToken<Map<String, Object>>() {}.getType();
		return gson().fromJson(toJson(object), type);
	}

	public static Map<String, Object> toMap(JsonElement json) {
		Type type = new TypeToken<Map<String, Object>>() {}.getType();
		return gson().fromJson(json, type);
	}

	public static JsonElement toJson(Object object) {
		return object.getClass().isAssignableFrom(String.class) ? parse(String.valueOf(object)) : gson().toJsonTree(object);
	}

	public static String toString(Object object){
		return gson().toJson(object);
	}

}