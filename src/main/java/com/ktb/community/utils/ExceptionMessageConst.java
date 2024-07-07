package com.ktb.community.utils;

public interface ExceptionMessageConst {
    // 잘못된 파라미터가 들어왔을 때
    String ILLEGAL_USER_ID = "userId가 유효하지 않은 값입니다.";
    String ILLEGAL_POST_ID = "postId가 유효하지 않은 값입니다.";
    String ILLEGAL_POST_REQUEST_DTO = "postRequestDto가 유효하지 않은 값입니다.";
    String ILLEGAL_FILE = "file이 유효하지 않은 값입니다.";

    // Repository에서 값을 찾지 못했을 때
    String POST_NOT_FOUND = "해당하는 게시글을 찾을 수 없습니다.";
    String USER_NOT_FOUND = "해당하는 사용자를 찾을 수 없습니다.";
}
