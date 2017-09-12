package com.quadbaze.cws.services;

import com.google.gson.JsonObject;
import com.quadbaze.cws.utils.Attributes;
import com.quadbaze.cws.utils.HtmlUtil;

/**
 * Created by olatunji on 8/7/17.
 */
public class WidgetServiceImpl implements WidgetService {

    @Override
    public String getWidget(String jqueryCallbackMethodName){
        String widgetTemplatePath = "template/widget.html";
        String widgetHtml = HtmlUtil.toHtml(widgetTemplatePath, Attributes.INSTANCE);

        JsonObject json = new JsonObject();
        json.addProperty("content", widgetHtml);
        return jqueryCallbackMethodName + "(" + json + ")";
    }

}
