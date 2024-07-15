package com.ktb.community.exception;

import static com.ktb.community.utils.ExceptionMessageConst.ALREADY_EXIST_PASSWORD;

public class PasswordAlreadyExistException extends RuntimeException {

    public PasswordAlreadyExistException() {
        this(ALREADY_EXIST_PASSWORD);
    }

    public PasswordAlreadyExistException(String message) {
        super(message);
    }
}
