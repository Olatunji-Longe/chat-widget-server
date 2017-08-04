package com.sankore.webchat.controllers.rest;

import com.sankore.webchat.controllers.rest.core.RestResource;
import com.sankore.webchat.services.Messenger;
import com.sankore.webchat.services.MessengerService;
import org.eclipse.jetty.http.MimeTypes;

/**
 * Created by olatunji on 8/4/17.
 */
public class JavascriptController extends RestResource {

    private static final String JSON_MIME = MimeTypes.Type.APPLICATION_JSON.asString();

    private MessengerService messenger;

    public JavascriptController() {
        super("/scripts");
        messenger = new Messenger();
    }

    @Override
    public void initEndpoints() {

    }
}
