package com.sankore.webchat.controllers.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sankore.webchat.controllers.rest.core.RestResource;
import com.sankore.webchat.http.GsonUtil;
import com.sankore.webchat.services.Messenger;
import com.sankore.webchat.services.MessengerService;
import org.eclipse.jetty.http.MimeTypes;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by olatunji on 8/4/17.
 */
public class MessagesRestController extends RestResource {

    private static final String JSON_MIME = MimeTypes.Type.APPLICATION_JSON.asString();

    private MessengerService messenger;

    public MessagesRestController() {
        super("/messages");
        messenger = new Messenger();
    }

    @Override
    public void initEndpoints() {

        get(route.base(), (request, response) -> {
            JsonObject json = new JsonObject();
            try{
                response.type(JSON_MIME);
                json = messenger.getServerWelcomeMessage();
            }catch(Exception ex){
                ex.printStackTrace();
            }
            return json;
        });

        get(route.append("/:id"), (request, response) -> {
            JsonObject json = new JsonObject();
            try{
                response.type(JSON_MIME);
                String id = request.params(":id");
                json.addProperty("id", id);
            }catch(Exception ex){
                ex.printStackTrace();
            }
            return json;
        });

        post(route.append("/text"), (request, response) -> {
            JsonObject json = new JsonObject();
            try{
                response.type(JSON_MIME);
                JsonElement body = GsonUtil.toJson(request.body());
                if(body != null){
                    String text = body.getAsJsonObject().get("text").getAsString();
                    json = messenger.getServerMessageWithText(text);
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
            return json;
        });
    }
}
