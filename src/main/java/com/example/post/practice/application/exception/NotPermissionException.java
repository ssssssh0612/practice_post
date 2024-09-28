package com.example.post.practice.application.exception;

public class NotPermissionException extends RuntimeException{
    public NotPermissionException(String message){
        super(message);
    }
}
