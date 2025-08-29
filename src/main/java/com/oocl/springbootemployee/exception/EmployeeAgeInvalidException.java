package com.oocl.springbootemployee.exception;

public class EmployeeAgeInvalidException extends RuntimeException{
    public EmployeeAgeInvalidException(String message) {
        super(message);
    }
}
