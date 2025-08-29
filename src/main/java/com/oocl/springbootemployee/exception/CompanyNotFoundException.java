package com.oocl.springbootemployee.exception;

public class CompanyNotFoundException extends RuntimeException{
    public CompanyNotFoundException() {
        super("CompanyNotFoundException");
    }
}
