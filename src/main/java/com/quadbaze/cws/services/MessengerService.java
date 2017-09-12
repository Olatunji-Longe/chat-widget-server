package com.quadbaze.cws.services;

import com.google.gson.JsonObject;
import com.quadbaze.cws.exception.RestServiceException;
import com.quadbaze.cws.http.Payload;
import com.quadbaze.cws.http.HttpMethod;

/**
 * Created by olatunji on 8/4/17.
 */
public interface MessengerService {
    JsonObject sendRequest(String url, HttpMethod method, Payload payload) throws RestServiceException;
    JsonObject getServerWelcomeMessage() throws Exception;
    JsonObject getServerMessageWithText(String text) throws Exception;
}
