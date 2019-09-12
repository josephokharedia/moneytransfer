package com.okharedia.moneytransfer.domain;

public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(String message, Exception cause) {
        super(message, cause);
    }
}
