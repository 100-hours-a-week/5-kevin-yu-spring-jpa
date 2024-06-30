package com.ktb.community.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String postImage;
    private LocalDateTime createdAt;
    private int views;
    private int commentCount;
    // User join 컬럼
    private String nickname;
    private String profileImage;
}
