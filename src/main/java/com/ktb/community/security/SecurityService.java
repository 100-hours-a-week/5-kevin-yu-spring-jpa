package com.ktb.community.dto.user;

import com.ktb.community.entity.user.User;
import com.ktb.community.exception.EmailAlreadyExistException;
import com.ktb.community.exception.PasswordAlreadyExistException;
import com.ktb.community.exception.UserNotFoundException;
import com.ktb.community.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ktb.community.utils.ExceptionMessageConst.ILLEGAL_USER_REQUEST_DTO;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void join(UserRequestDto requestDto) {
        if (requestDto == null || requestDto.getEmail() == null || requestDto.getPassword() == null)
            throw new IllegalArgumentException(ILLEGAL_USER_REQUEST_DTO);

        User user = userRepository.findByEmail(requestDto.getEmail()).orElse(null);

        if (user != null)
            throw new EmailAlreadyExistException();

        String encodedPassword = bCryptPasswordEncoder.encode(requestDto.getPassword());
        requestDto.setPassword(encodedPassword);
        userRepository.save(requestDto.toEntity());
    }

    @Transactional
    public void changePassword(Long userId, String newPassword) {
        if (userId == null || newPassword == null)
            throw new IllegalArgumentException(ILLEGAL_USER_REQUEST_DTO);

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        boolean isSame = bCryptPasswordEncoder.matches(newPassword, user.getPassword());

        if (isSame) throw new PasswordAlreadyExistException();

        userRepository.modifyPassword(userId, bCryptPasswordEncoder.encode(newPassword));
    }
}
