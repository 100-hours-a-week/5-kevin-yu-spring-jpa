package com.ktb.community.entity.user;

import com.ktb.community.dto.user.UserRequestDto;
import com.ktb.community.dto.user.UserResponseDto;
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
    @Setter
    private Long id;

    private String email;

    private String password;

    private String nickname;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(insertable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private String role;

    public UserResponseDto toDto() {
        return UserResponseDto.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }

    public void updateUser(UserRequestDto userRequestDto) {
        this.nickname = userRequestDto.getNickname();
        this.password = userRequestDto.getPassword();
        this.profileImage = userRequestDto.getProfileImage();
    }

    public void changeStatus(UserStatus status) {
        this.status = status;

        if (status == UserStatus.DELETED)
            deletedAt = LocalDateTime.now();
    }
}
