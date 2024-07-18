package com.ktb.community.repository.user;

import com.ktb.community.dto.user.UserRequestDto;
import com.ktb.community.entity.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    List<User> findAll();

    Optional<User> findById(Long userId);

    Optional<User> findByEmail(String email);

    Integer countByNickname(String nickname);

    void modifyUserInfo(UserRequestDto dto);

    void modifyPassword(Long userId, String newPassword);

    void modifyLoginTime(Long userId);

    void remove(Long userId);
}
