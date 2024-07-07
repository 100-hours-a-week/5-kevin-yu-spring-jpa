package com.ktb.community.exception;

import static com.ktb.community.utils.ExceptionMessageConst.USER_NOT_FOUND;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        this(USER_NOT_FOUND);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
