package com.sankore.webchat.services;

import com.google.gson.JsonObject;
import com.sankore.webchat.controllers.socket.core.Chat;
import com.sankore.webchat.exception.RestServiceException;
import com.sankore.webchat.http.HttpManager;
import com.sankore.webchat.http.HttpMethod;
import com.sankore.webchat.http.Payload;
import com.sankore.webchat.utils.PropUtil;
import com.sankore.webchat.utils.ResourceLoader;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;

/**
 * Created by olatunji on 8/2/17.
 */
public class Messenger implements MessengerService {

    @Override
    public JsonObject sendRequest(String url, HttpMethod method, Payload payload) throws RestServiceException {
        try{
            payload = payload != null ? payload : new Payload();
            HttpRequestBase request = HttpManager.buildHttpRequest(method, url, ContentType.APPLICATION_JSON, payload);
            //HttpManager.addBasicAuthToken(request, osgiAccess.getAuthorizationKey(), osgiAccess.getUsername(), osgiAccess.getPassword());
            return HttpManager.processRequestAsync(request);
        }catch(Exception ex){
            throw new RestServiceException("Rest Request Exception", ex);
        }
    }

    @Override
    public JsonObject getServerWelcomeMessage() throws Exception {
        JsonObject json = new JsonObject();
        String message = PropUtil.getProperty(ResourceLoader.PROP_FILE,"message.welcome");
        json.addProperty("message", Chat.getSimpleMessageHtml("Server", message));
        return json;
    }

    @Override
    public JsonObject getServerMessageWithText(String text) throws Exception {
        JsonObject json = new JsonObject();
        json.addProperty("message", Chat.getSimpleMessageHtml("Server", text));
        return json;
    }

}
