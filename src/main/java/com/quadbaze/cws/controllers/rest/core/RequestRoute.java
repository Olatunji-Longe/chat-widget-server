package com.quadbaze.cws.controllers.rest.core;

/**
 * Created by olatunji on 8/4/17.
 */
public final class RequestRoute {

    private final String basePath;

    public RequestRoute(String basePath){
        this.basePath = basePath;
    }

    public String getBasePath() {
        return basePath;
    }

    public String base(){
        return getBasePath();
    }
    public String append(String requestPath){
        return getBasePath()+requestPath;
    }
}
