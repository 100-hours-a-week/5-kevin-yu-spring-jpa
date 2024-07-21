package com.ktb.community.exception;

import static com.ktb.community.utils.ExceptionMessageConst.COMMENT_NOT_FOUND;

public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException() {
        this(COMMENT_NOT_FOUND);
    }

    public CommentNotFoundException(String message) {
        super(message);
    }
}
