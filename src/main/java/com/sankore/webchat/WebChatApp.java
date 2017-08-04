package com.sankore.webchat;

import com.sankore.webchat.controllers.rest.MessagesRestController;
import com.sankore.webchat.controllers.socket.ChatWebSocketController;

import static spark.Spark.init;
import static spark.Spark.staticFileLocation;
import static spark.Spark.webSocket;

/**
 * Created by olatunji on 8/1/17.
 */
public class WebChatApp {

    public static void main(String[] args) {
        staticFileLocation("/public"); //index.html is served at localhost:4567 (default port)
        webSocket("/chat", ChatWebSocketController.class);

        new MessagesRestController().initEndpoints();

        init();

    }

}
