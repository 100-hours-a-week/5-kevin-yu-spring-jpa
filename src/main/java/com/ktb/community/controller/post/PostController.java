package com.ktb.community.controller.post;

import com.ktb.community.dto.post.PostRequestDto;
import com.ktb.community.dto.post.PostResponseDto;
import com.ktb.community.dto.user.CustomUserDetails;
import com.ktb.community.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/json/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> showBoard() {
        return ResponseEntity.ok(postService.showBoard());
    }

    @PostMapping
    public ResponseEntity<?> addPost(@Valid @ModelAttribute PostRequestDto postRequestDto,
                                        @RequestParam(required = false) MultipartFile file,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getDefaultMessage());
            }
            return ResponseEntity.badRequest().build();
        }

        Long userId = getUserId();
        if (userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Long postId;
        try {
            postId = postService.addPost(postRequestDto, file, userId);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
        if (postId == null)
            return ResponseEntity.internalServerError().build();

        URI uri = URI.create("/posts/" + postId);

        return ResponseEntity.created(uri).build();
    }


    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Long postId) {
        if (postId == null)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(postService.showPost(postId));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> editPost(@PathVariable Long postId, @Valid @RequestBody PostRequestDto postRequestDto,
                                      BindingResult bindingResult) {

        log.info(postRequestDto.toString());

        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.info(fieldError.getDefaultMessage());
            }
            return ResponseEntity.badRequest().build();
        }

        Long userId = getUserId();
        if (userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (postId == null)
            return ResponseEntity.notFound().build();

        postRequestDto.setId(postId);
        String prevImage = postService.editPost(postRequestDto, userId);

        return ResponseEntity.ok().body(Collections.singletonMap("prevImage", prevImage));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        if (postId == null)
            return ResponseEntity.badRequest().build();

        Long userId = getUserId();

        if (userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String prevImage = postService.deletePost(postId, userId);
        return ResponseEntity.ok().body(Collections.singletonMap("prevImage", prevImage));
    }

    private static Long getUserId() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return userDetails.getUser().getId();
    }
}
