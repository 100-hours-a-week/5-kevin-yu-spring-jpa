package com.ktb.community.dto.post;

import com.ktb.community.entity.post.Post;
import com.ktb.community.entity.post.PostStatus;
import com.ktb.community.entity.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String postImage;

    public Post toEntity(User user) {
        return Post.builder()
                .title(title)
                .content(content)
                .postImage(postImage)
                .createdAt(LocalDateTime.now())
                .status(PostStatus.ACTIVE)
                .user(user)
                .build();
    }
}
