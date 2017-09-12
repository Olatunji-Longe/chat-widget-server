package com.quadbaze.cws.controllers.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quadbaze.cws.controllers.rest.core.RestResource;
import com.quadbaze.cws.http.GsonUtil;
import com.quadbaze.cws.services.MessengerService;
import com.quadbaze.cws.services.MessengerServiceImpl;
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
        messenger = new MessengerServiceImpl();
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
