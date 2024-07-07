package com.ktb.community.exception;

import static com.ktb.community.utils.ExceptionMessageConst.POST_NOT_FOUND;

public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException() {
        this(POST_NOT_FOUND);
    }

    public PostNotFoundException(String message) {
        super(message);
    }
}
