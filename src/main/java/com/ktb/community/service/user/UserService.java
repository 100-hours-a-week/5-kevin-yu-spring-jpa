package com.ktb.community.service.user;

import com.ktb.community.dto.user.UserRequestDto;
import com.ktb.community.dto.user.UserResponseDto;
import com.ktb.community.entity.user.User;
import com.ktb.community.exception.ChangeImageFailedException;
import com.ktb.community.exception.UserNotFoundException;
import com.ktb.community.repository.user.UserRepository;
import com.ktb.community.utils.ClientServerHandler;
import com.ktb.community.utils.ClientServerHandlerMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.ktb.community.utils.ExceptionMessageConst.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ClientServerHandler clientServerHandler;

    @Transactional(readOnly = true)
    public UserResponseDto findUserById(Long userId) {
        if (userId == null)
            throw new IllegalArgumentException(ILLEGAL_USER_ID);

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        log.info(user.toString());
        return user.toDto();
    }

    @Transactional(readOnly = true)
    public UserResponseDto findUserByEmail(String email) {
        if (email == null)
            throw new IllegalArgumentException(ILLEGAL_EMAIL);

        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return user.toDto();
    }

    @Transactional(readOnly = true)
    public boolean isAlreadyExist(String nickname) {
        if (nickname == null)
            throw new IllegalArgumentException(ILLEGAL_NICKNAME);

        return userRepository.countByNickname(nickname) != 0;
    }

    @Transactional
    public void changeUserInfo(UserRequestDto userRequestDto, MultipartFile file) {
        if (userRequestDto == null || userRequestDto.getId() == null || userRequestDto.getNickname() == null)
            throw new IllegalArgumentException(ILLEGAL_USER_REQUEST_DTO);

        User user = userRepository.findById(userRequestDto.getId()).orElseThrow(UserNotFoundException::new);
        String prevImage = user.getProfileImage();

        if (file != null) {
            String imageName = clientServerHandler.sendFile(file, prevImage, "users", ClientServerHandlerMethod.PATCH);

            if (imageName == null)
                throw new ChangeImageFailedException("이미지 파일 변경에 실패하였습니다.");

            userRequestDto.setProfileImage(imageName);
        }


        userRepository.modifyUserInfo(userRequestDto);
    }

    @Transactional
    public void deleteUserById(Long userId) {
        if (userId == null)
            throw new IllegalArgumentException(ILLEGAL_USER_ID);

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        clientServerHandler.requestDeleteFile("users", user.getProfileImage());

        userRepository.remove(userId);
    }

    @Transactional
    public void recordLoginAttempt(Long userId) {
        if (userId == null)
            throw new IllegalArgumentException(ILLEGAL_USER_ID);

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        userRepository.modifyLoginTime(user.getId());
    }
}
