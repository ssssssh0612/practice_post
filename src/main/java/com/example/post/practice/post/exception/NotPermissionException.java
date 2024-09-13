package com.example.post.practice.post.exception;

public class NotPermissionException extends RuntimeException{
    public NotPermissionException(String message){
        super(message);
    }
}
