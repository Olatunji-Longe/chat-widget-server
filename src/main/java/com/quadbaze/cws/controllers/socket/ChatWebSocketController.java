package com.quadbaze.cws.controllers.socket;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quadbaze.cws.http.GsonUtil;
import com.quadbaze.cws.http.HttpMethod;
import com.quadbaze.cws.http.Payload;
import com.quadbaze.cws.http.ResponseParam;
import com.quadbaze.cws.services.MessengerService;
import com.quadbaze.cws.services.MessengerServiceImpl;
import com.quadbaze.cws.utils.PropUtil;
import com.quadbaze.cws.utils.ResourceLoader;
import com.quadbaze.cws.controllers.socket.core.Chat;
import org.apache.http.HttpStatus;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

/**
 * Created by olatunji on 8/1/17.
 */
@WebSocket
public class ChatWebSocketController {

    private Chat chat;
    private MessengerService messenger;

    public ChatWebSocketController(){
        chat = new Chat();
        messenger = new MessengerServiceImpl();
    }

    @OnWebSocketConnect
    public void onConnect(Session userSession) throws Exception {
        userSession.setIdleTimeout(60 * (60 * 1000)); //60-minutes
        String username = chat.getUsername(userSession);
        userSession.getUpgradeRequest().setHeader("username", username);
        userSession.getUpgradeRequest().setHeader("is_welcome", Boolean.TRUE.toString());
        chat.getSessionUsernamesMap().put(username, userSession);
        chat.pushMessage(userSession, "Server", (username + " joined the chat"));
    }

    @OnWebSocketMessage
    public void onMessage(Session userSession, String message) throws Exception {
        JsonElement json = GsonUtil.parse(message);
        String username = chat.getUsername(userSession);
        String msg = json.getAsJsonObject().get("msg").getAsString();
        chat.pushMessage(userSession, chat.getUsername(userSession), msg);

        String url = PropUtil.getProperty(ResourceLoader.PROP_FILE,"endpoint.chat.response-engine");
        Payload payload = new Payload().set("message", msg).set("username", username);
        System.out.println(payload);
        JsonObject response = messenger.sendRequest(url, HttpMethod.POST, payload);

        if(response != null && response.get(ResponseParam.code).getAsInt() == HttpStatus.SC_OK){
            //send email and sms here
            System.out.println(response.toString());
            chat.pushMessage(userSession, "Server", response.getAsJsonObject().get("result").getAsJsonObject().get("status").getAsString());
        }else{
            System.out.println(response.toString());
            chat.pushMessage(userSession, "Server", PropUtil.getProperty(ResourceLoader.PROP_FILE,"message.remote.server.is.down"));
        }
    }

    @OnWebSocketClose
    public void onClose(Session userSession, int statusCode, String reason) throws Exception {
        String username = chat.getUsername(userSession);
        chat.getSessionUsernamesMap().remove(username);
        chat.pushMessage(userSession, "Server", (username + " left the chat"));
    }

    @OnWebSocketError
    public void onError(Throwable cause) {
        cause.printStackTrace();
        System.out.println("WebSocket Error: " + cause.getLocalizedMessage());
    }

}
