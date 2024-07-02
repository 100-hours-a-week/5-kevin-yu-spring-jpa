package com.ktb.community.dto.user;

import com.ktb.community.entity.user.User;
import com.ktb.community.entity.user.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    // 수정할 때 사용 (@NotNull이 없는 이유)
    private Long id;

    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    private String nickname;

    private String profileImage;

    public User toEntity() {
        return User.builder()
                .id(id) // 등록할 때는 null로 초기화 돼서 상관없음
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImage(profileImage)
                .status(UserStatus.ACTIVE)
                .joinedAt(LocalDateTime.now())
                .build();
    }
}
