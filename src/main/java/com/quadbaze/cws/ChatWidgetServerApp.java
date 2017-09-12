package com.quadbaze.cws;

import com.quadbaze.cws.controllers.filter.CorsFilter;
import com.quadbaze.cws.controllers.rest.MessagesRestController;
import com.quadbaze.cws.controllers.rest.WidgetController;
import com.quadbaze.cws.controllers.socket.ChatWebSocketController;

import static spark.Spark.*;

/**
 * Created by olatunji on 8/1/17.
 */
public class ChatWidgetServerApp {

    //index.html is served at localhost:4567 (spark's default port)
    public static void main(String[] args) {
        staticFileLocation("/public");
        webSocket("/chat", ChatWebSocketController.class);

        CorsFilter.enableCORS();

        new MessagesRestController().initEndpoints();
        new WidgetController().initEndpoints();

        init();
    }
}
