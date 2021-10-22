package com.health.exception;


/**
 * 自定义异常
 */
public class HealthException extends RuntimeException {
    public HealthException(String message){
        super(message);
    }

}
