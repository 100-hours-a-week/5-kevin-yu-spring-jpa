package com.ktb.community.repository.user;

import com.ktb.community.dto.post.PostRequestDto;
import com.ktb.community.dto.post.PostResponseDto;
import com.ktb.community.dto.user.UserRequestDto;
import com.ktb.community.dto.user.UserResponseDto;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    UserResponseDto save(UserRequestDto dto);

    List<UserResponseDto> findAll();

    Optional<UserResponseDto> findById(Long userId);

    void modify(UserRequestDto dto);

    void remove(Long userId);
}
