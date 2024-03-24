package com.mike.redislua.exception;

public class SoldOutException extends Exception {

    public SoldOutException(String message) {
        super(message);
    }
}
