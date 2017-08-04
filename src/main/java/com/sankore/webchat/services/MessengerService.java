package com.sankore.webchat.services;

import com.google.gson.JsonObject;
import com.sankore.webchat.exception.RestServiceException;
import com.sankore.webchat.http.HttpMethod;
import com.sankore.webchat.http.Payload;

/**
 * Created by olatunji on 8/4/17.
 */
public interface MessengerService {
    JsonObject sendRequest(String url, HttpMethod method, Payload payload) throws RestServiceException;
    JsonObject getServerWelcomeMessage() throws Exception;
    JsonObject getServerMessageWithText(String text) throws Exception;
}
