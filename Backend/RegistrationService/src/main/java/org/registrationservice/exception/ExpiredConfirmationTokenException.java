package org.registrationservice.exception;

public class ExpiredConfirmationTokenException extends RuntimeException{
    public ExpiredConfirmationTokenException(String message){
        super(message);
    }

}
