package com.ktb.community.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktb.community.dto.user.CustomUserDetails;
import com.ktb.community.dto.user.UserRequestDto;
import com.ktb.community.dto.user.UserResponseDto;
import com.ktb.community.security.JWTUtil;
import com.ktb.community.service.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JWTUtil jwtUtil;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UserRequestDto userRequestDto;
        try {
            BufferedReader reader = request.getReader();
             userRequestDto = new ObjectMapper().readValue(reader, UserRequestDto.class);

             log.info("{}", userRequestDto);
        } catch (IOException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            throw new RuntimeException(e);
        }

        String username = userRequestDto.getEmail();
        String password = userRequestDto.getPassword();

        log.info("username: {}, password: {}", username, password);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("successful authentication");

        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();

        String email = customUserDetails.getUsername();
        
        UserResponseDto userInfo = userService.findUserByEmail(email);
        Long userId = userInfo.getId();

        String role = getRole(authResult);

        userService.recordLoginAttempt(userId);

        String token = jwtUtil.createToken(email, userId, role);
        response.addHeader("Authorization", "Bearer " + token);
    }

    private String getRole(Authentication authResult) {
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        return auth.getAuthority();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("unsuccessful authentication");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
