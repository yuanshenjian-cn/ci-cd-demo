package com.oocl.springbootemployee.exception;

public class EmployeeSalaryInvalidException extends RuntimeException{
    public EmployeeSalaryInvalidException(String message) {
        super(message);
    }
}
