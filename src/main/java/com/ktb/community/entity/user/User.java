package com.ktb.community.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email;

    private String password;

    private String nickname;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(insertable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "joined_at", insertable = false)
    private LocalDateTime joinedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
