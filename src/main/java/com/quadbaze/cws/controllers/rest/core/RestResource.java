package com.quadbaze.cws.controllers.rest.core;

/**
 * Created by olatunji on 8/4/17.
 */
public abstract class RestResource {

    protected RequestRoute route;

    public RestResource(String basePath){
        route = new RequestRoute(basePath);
    }

    protected abstract void initEndpoints();
}
