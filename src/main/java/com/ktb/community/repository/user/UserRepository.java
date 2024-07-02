package com.ktb.community.repository.user;

import com.ktb.community.dto.user.UserRequestDto;
import com.ktb.community.dto.user.UserResponseDto;
import com.ktb.community.entity.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    List<User> findAll();

    Optional<User> findById(Long userId);

    void modify(UserRequestDto dto);

    void remove(Long userId);
}
