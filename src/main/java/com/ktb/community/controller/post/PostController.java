package com.ktb.community.controller.post;

import com.ktb.community.dto.post.PostRequestDto;
import com.ktb.community.dto.post.PostResponseDto;
import com.ktb.community.entity.comment.Comment;
import com.ktb.community.entity.post.Post;
import com.ktb.community.service.post.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
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
    public ResponseEntity<Void> addPost(@ModelAttribute PostRequestDto postRequestDto,
                                        @RequestParam(required = false) MultipartFile file,
                                        BindingResult bindingResult, HttpSession session) {

        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().build();

        Long postId;
        try {
//            Long userId = (Long) session.getAttribute("userId");
            Long userId = 1L; // 임시
            postId = postService.addPost(postRequestDto, file, userId);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
        if (postId == null)
            return ResponseEntity.internalServerError().build();

        URI uri = URI.create("/posts/" + postId);
        log.info("uri: {}", uri);

        return ResponseEntity.created(uri).build();
    }


    @GetMapping("/{postNo}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Long postNo) {
        return ResponseEntity.ok(postService.showPost(postNo));
    }

    @PutMapping("/{postNo}")
    public ResponseEntity<Void> editPost(@PathVariable Long postNo) {
        return ResponseEntity.ok().build();
    }
}
