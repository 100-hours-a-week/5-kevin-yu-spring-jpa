package com.ktb.community.entity.post;

import com.ktb.community.dto.post.PostRequestDto;
import com.ktb.community.dto.post.PostResponseDto;
import com.ktb.community.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    private String content;

    @Column(name = "post_image")
    private String postImage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column
    private int views;

    @Column(name = "comment_count")
    private int commentCount;

    @Column
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    public PostResponseDto toDto() {
        return PostResponseDto.builder()
                .id(id)
                .title(title)
                .content(content)
                .postImage(postImage)
                .createdAt(createdAt)
                .views(views)
                .commentCount(commentCount)
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .build();
    }

    public void updatePost(PostRequestDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.postImage = dto.getPostImage();
    }

    public void deletePost() {
        status = PostStatus.DELETED;
        deletedAt = LocalDateTime.now();
    }

    public void increaseViews() {
        views++;
    }

    public void increaseCommentCount() {
        commentCount++;
    }

    public void decreaseCommentCount() {
        commentCount--;
    }
}
