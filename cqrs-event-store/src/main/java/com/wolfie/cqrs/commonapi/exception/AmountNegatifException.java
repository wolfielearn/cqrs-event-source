package com.wolfie.cqrs.commonapi.exception;

public class AmountNegatifException extends RuntimeException {
    public AmountNegatifException(String message) {
        super(message);
    }
}
