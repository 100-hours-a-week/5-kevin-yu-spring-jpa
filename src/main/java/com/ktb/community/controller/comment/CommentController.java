package com.ktb.community.controller.comment;

import com.ktb.community.entity.comment.Comment;
import com.ktb.community.repository.comment.CommentRepository;
import com.ktb.community.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/json/posts/{postNo}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<Long> getCommentsByPostNo(@PathVariable Long postNo) {
        return ResponseEntity.ok(postNo);
    }
}
