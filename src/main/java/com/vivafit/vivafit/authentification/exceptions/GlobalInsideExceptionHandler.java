package com.vivafit.vivafit.authentification.exceptions;

import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

@ControllerAdvice
public class GlobalInsideExceptionHandler {

    @ExceptionHandler(ClientAbortException.class)
    @ResponseStatus(HttpStatus.OK)
    public void handleClientAbortException(ClientAbortException ex) {
    }

    @ExceptionHandler(AsyncRequestNotUsableException.class)
    @ResponseStatus(HttpStatus.OK)
    public void handleAsyncRequestNotUsableException(AsyncRequestNotUsableException ex) {
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.OK)
    public void handleIllegalStateException(IllegalStateException ex) {
    }
}
