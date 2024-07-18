package com.ktb.community.service.user;

import com.ktb.community.dto.user.CustomUserDetails;
import com.ktb.community.entity.user.User;
import com.ktb.community.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username이라는 필드가 없기 때문에 email을 principal로 사용
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));

        log.info("user: {}", user);

        return new CustomUserDetails(user);
    }
}
