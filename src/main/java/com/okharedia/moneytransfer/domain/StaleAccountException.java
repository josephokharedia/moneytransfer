package com.okharedia.moneytransfer.domain;

public class StaleAccountException extends Exception {

    public StaleAccountException(String message) {
        super(message);
    }

    public StaleAccountException(String message, Exception cause) {
        super(message, cause);
    }
}
