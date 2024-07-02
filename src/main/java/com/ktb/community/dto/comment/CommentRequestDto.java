package com.ktb.community.dto.comment;

import com.ktb.community.entity.comment.Comment;
import com.ktb.community.entity.comment.CommentStatus;
import com.ktb.community.entity.post.Post;
import com.ktb.community.entity.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    // 수정할 때 사용
    private Long id;

    @NotNull
    private Long postId;

    @NotNull
    private Long userId;

    @NotBlank
    private String content;

    public Comment toEntity(Post post, User user) {
        return Comment.builder()
                .id(id)
                .post(post)
                .user(user)
                .content(content)
                .status(CommentStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
