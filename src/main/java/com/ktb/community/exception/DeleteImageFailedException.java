package com.ktb.community.exception;

public class DeleteImageFailedException extends RuntimeException {

    public DeleteImageFailedException() {
        super();
    }

    public DeleteImageFailedException(String message) {
        super(message);
    }
}
