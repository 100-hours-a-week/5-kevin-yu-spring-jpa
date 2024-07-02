package com.ktb.community.entity.comment;

import com.ktb.community.dto.comment.CommentRequestDto;
import com.ktb.community.entity.post.Post;
import com.ktb.community.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@ToString()
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column
    @Enumerated(EnumType.STRING)
    private CommentStatus status;

    public void updateComment(CommentRequestDto commentDto) {
        this.content = commentDto.getContent();
    }

    public void deleteComment() {
        status = CommentStatus.DELETED;
        deletedAt = LocalDateTime.now();
    }
}
