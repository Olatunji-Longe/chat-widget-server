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

        /*before(new Filter() {
            @Override
            public void handle(Request request, Response response) {
                updateHeaders(request, response);
            }
        });

        after(new Filter() {
            @Override
            public void handle(Request request, Response response) {
                updateHeaders(request, response);
            }
        });*/


        CorsFilter.enableCORS();
        CorsFilter.applyCORS();
        new MessagesRestController().initEndpoints();
        new WidgetController().initEndpoints();

        init();
    }

    /*public static final void updateHeaders(Request request, Response response){
        response.header("Access-Control-Allow-Origin", "*");
        response.header("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.header("Access-Control-Allow-Headers", "Authorization, Content-Type");
        response.header("Access-Control-Max-Age", "3600");
        if ("OPTIONS".equalsIgnoreCase(request.requestMethod())) {
            response.status(HttpServletResponse.SC_OK);
        }
    }*/

}
