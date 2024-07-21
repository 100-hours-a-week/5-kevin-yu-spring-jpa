package com.ktb.community.controller.comment;

import com.ktb.community.dto.comment.CommentRequestDto;
import com.ktb.community.dto.comment.CommentResponseDto;
import com.ktb.community.dto.user.CustomUserDetails;
import com.ktb.community.exception.CommentNotFoundException;
import com.ktb.community.exception.UnauthorizedUserException;
import com.ktb.community.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/json/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<?> getCommentsByPostId(@PathVariable Long postId) {
        if (postId == null)
            return ResponseEntity.badRequest().build();

        List<CommentResponseDto> comments = commentService.showAllComments(postId);

        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody String content) {
        if (postId == null || content == null) {
            log.info("postId: {}, content: {}", postId, content);
            return ResponseEntity.badRequest().build();
        }

        Long userId = getUserId();
        if (userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        CommentRequestDto requestDto = CommentRequestDto.builder()
                .postId(postId)
                .userId(userId)
                .content(content)
                .build();

        try {
            commentService.addComment(requestDto);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            Map<String, String> message = Collections.singletonMap("message", "댓글 수정에 실패했습니다. 잠시 후 다시 시도해주세요.");
            return ResponseEntity.internalServerError().body(message);
        }

        Map<String, String> message = Collections.singletonMap("message", "댓글이 성공적으로 등록되었습니다.");
        return ResponseEntity.ok(message);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<?> editComment(@PathVariable Long commentId, @RequestBody String content) {
        if (commentId == null || content == null) {
            log.info("commentId: {}, content: {}", commentId, content);
            return ResponseEntity.badRequest().build();
        }

        Long userId = getUserId();
        if (userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        CommentRequestDto requestDto = CommentRequestDto.builder()
                .id(commentId)
                .userId(userId)
                .content(content)
                .build();

        try {
            commentService.editComment(requestDto);
        } catch (CommentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        Long userId = getUserId();
        if (userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        CommentRequestDto requestDto = CommentRequestDto.builder()
                .id(commentId)
                .userId(userId)
                .build();

        try {
            commentService.deleteComment(requestDto);
        } catch (CommentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok().build();
    }

    private Long getUserId() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userDetails.getUser().getId();
    }
}
