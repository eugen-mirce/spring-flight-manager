package com.lhind.flight.exception;

public class TripServiceException extends RuntimeException{
    public TripServiceException(String message) {
        super(message);
    }
}
