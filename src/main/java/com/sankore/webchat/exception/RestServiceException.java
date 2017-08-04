package com.sankore.webchat.exception;

/**
 * Created by olatunji on 7/27/17.
 */
public class RestServiceException extends RuntimeException {

    public RestServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
