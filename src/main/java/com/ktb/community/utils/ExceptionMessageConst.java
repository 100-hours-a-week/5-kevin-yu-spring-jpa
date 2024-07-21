package com.ktb.community.utils;

public interface ExceptionMessageConst {
    // 잘못된 파라미터가 들어왔을 때
    String ILLEGAL_FILE = "file이 유효하지 않은 값입니다.";
    String ILLEGAL_POST_ID = "postId가 유효하지 않은 값입니다.";
    String ILLEGAL_USER_REQUEST_DTO = "userRequestDto가 유효하지 않은 값입니다.";
    String ILLEGAL_POST_REQUEST_DTO = "postRequestDto가 유효하지 않은 값입니다.";

    // Repository에서 값을 찾지 못했을 때
    String POST_NOT_FOUND = "해당하는 게시글을 찾을 수 없습니다.";
    String USER_NOT_FOUND = "해당하는 사용자를 찾을 수 없습니다.";
    String COMMENT_NOT_FOUND = "해당하는 댓글을 찾을 수 없습니다.";

    // 이미 존재하는 값일 때
    String ALREADY_EXIST_EMAIL = "이미 존재하는 이메일입니다.";
    String ALREADY_EXIST_PASSWORD = "이미 존재하는 비밀번호입니다.";
}
