package com.ktb.community.exception;

import static com.ktb.community.utils.ExceptionMessageConst.ALREADY_EXIST_EMAIL;

public class EmailAlreadyExistException extends RuntimeException {

    public EmailAlreadyExistException() {
        this(ALREADY_EXIST_EMAIL);
    }

    public EmailAlreadyExistException(String message) {
        super(message);
    }
}
