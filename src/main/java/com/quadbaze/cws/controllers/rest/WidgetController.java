package com.quadbaze.cws.controllers.rest;

import com.quadbaze.cws.controllers.rest.core.RestResource;
import com.quadbaze.cws.services.WidgetService;
import com.quadbaze.cws.services.WidgetServiceImpl;

import static spark.Spark.get;

/**
 * Created by olatunji on 8/4/17.
 */
public class WidgetController extends RestResource {

    private static final String JAVASCRIPT_MIME = "text/javascript";

    private WidgetService widgetService;

    public WidgetController() {
        super("/widget");
        widgetService = new WidgetServiceImpl();
    }

    @Override
    public void initEndpoints() {

        get(route.base(), (request, response) -> {
            String widget = "";
            try{
                String jqueryCallbackMethodName = request.queryParams("callback");
                widget = widgetService.getWidget(jqueryCallbackMethodName);
                response.type(JAVASCRIPT_MIME);
            }catch(Exception ex){
                ex.printStackTrace();
            }
            return widget;
        });

    }
}
