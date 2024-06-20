package com.ktb.community.entity.post;

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    private String content;

    @Column(name = "post_image")
    private String postImage;

    @Column(name = "created_at", insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(insertable = false)
    private int views;

    @Column(name = "comment_count", insertable = false)
    private int commentCount;

    @Column(insertable = false)
    @Enumerated(EnumType.STRING)
    private PostStatus status;
}
