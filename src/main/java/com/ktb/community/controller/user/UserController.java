package com.ktb.community.controller.user;

import com.ktb.community.dto.user.CustomUserDetails;
import com.ktb.community.security.SecurityService;
import com.ktb.community.dto.user.UserRequestDto;
import com.ktb.community.dto.user.UserResponseDto;
import com.ktb.community.exception.EmailAlreadyExistException;
import com.ktb.community.exception.PasswordAlreadyExistException;
import com.ktb.community.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

import static com.ktb.community.utils.ExceptionMessageConst.ALREADY_EXIST_EMAIL;

@Slf4j
@RestController
@RequestMapping("/json/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SecurityService securityService;

    @GetMapping
    public ResponseEntity<?> getUserInfo() {
        Long userId = getUserId();

        if (userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        UserResponseDto user = userService.findUserById(userId);
        log.info(user.toString());
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<?> changeUserInfo(@RequestParam String nickname, @RequestParam(required = false) MultipartFile file) {
        log.info("nickname: {}, file: {}", nickname, file);

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUser().getId();

        if (userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setId(userId);
        userRequestDto.setNickname(nickname);

        userService.changeUserInfo(userRequestDto, file);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> withdraw() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUser().getId();

        if (userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "탈퇴에 실패했습니다. 잠시 후 다시 시도해주세요."));

        userService.deleteUserById(userId);

        return ResponseEntity.ok()
                .body(Collections.singletonMap("message", "회원 탈퇴를 완료했습니다. 이용해주셔서 감사합니다."));
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody UserRequestDto requestDto, BindingResult bindingResult) {
        log.info("requestDto: {}", requestDto);

        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.info(fieldError.getDefaultMessage());
            }
            return ResponseEntity.badRequest().build();
        }

        try {
            securityService.join(requestDto);
        } catch (EmailAlreadyExistException e) {
            Map<String, String> responseMessage = Collections.singletonMap("message", ALREADY_EXIST_EMAIL);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseMessage);
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody String password) {
        Long userId = getUserId();

        if (userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (!StringUtils.hasText(password))
            return ResponseEntity.badRequest().build();

        try {
            securityService.changePassword(userId, password);
        } catch (PasswordAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("duplication")
    public ResponseEntity<?> checkDuplication(@RequestParam String nickname) {
        if (!StringUtils.hasText(nickname))
            return ResponseEntity.badRequest().build();

        log.info("nickname: {}", nickname);

        boolean isDuplicateNickname = userService.isAlreadyExist(nickname);
        log.info("isDuplicateNickname: {}", isDuplicateNickname);

        if (isDuplicateNickname)
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        return ResponseEntity.ok().build();
    }

    private static Long getUserId() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return userDetails.getUser().getId();
    }
}
