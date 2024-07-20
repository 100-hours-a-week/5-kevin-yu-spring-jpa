package com.ktb.community.filter;

import com.ktb.community.dto.user.CustomUserDetails;
import com.ktb.community.entity.user.User;
import com.ktb.community.security.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.info(request.getRequestURI());
            if (request.getRequestURI().equals("/json/users/login") ||
                    request.getRequestURI().equals("/json/users/join")) {
                filterChain.doFilter(request, response);
                return;
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authorization.substring(7);

        if (jwtUtil.isExpired(token)) {
            log.info("token is expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Long userId = jwtUtil.getUserId(token);
        String email = jwtUtil.getEmail(token);
        String role = jwtUtil.getRole(token);

        log.info("user id:{} email:{} role:{}", userId, email, role);

        User user = User.builder()
                .id(userId)
                .email(email)
                .role(role)
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        log.info("authToken: {}", authToken);

        request.setAttribute("userId", userId);

        filterChain.doFilter(request, response);
    }
}
