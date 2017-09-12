package com.quadbaze.cws.controllers.filter;

import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;

import javax.servlet.http.HttpServletResponse;

import static spark.Spark.before;
import static spark.Spark.options;

/**
 * Created by olatunji Longe on 9/11/17.
 */
public class CorsFilter {

    public static void applyCORS() {
        Filter filter = new Filter() {
            @Override
            public void handle(Request request, Response response) throws Exception {
                updateHeaders(request, response);
            }
        };
        Spark.after(filter);
    }

    // Enables CORS on requests. This method is an initialization method and should be called once.
    public static void enableCORS() {

        options("/*", (request, response) -> {

            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> updateHeaders(request, response));
    }

    public static final void updateHeaders(Request request, Response response){
        response.header("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
        response.header("Access-Control-Allow-Origin", "*");
        response.header("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
        response.header("Access-Control-Allow-Credentials", "true");
        response.header("Access-Control-Max-Age", "3600");
        if ("OPTIONS".equalsIgnoreCase(request.requestMethod())) {
            response.status(HttpServletResponse.SC_OK);
        }
    }
}
