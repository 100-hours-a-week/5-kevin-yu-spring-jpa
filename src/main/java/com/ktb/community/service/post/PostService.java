package com.ktb.community.service.post;

import com.ktb.community.dto.post.PostRequestDto;
import com.ktb.community.dto.post.PostResponseDto;
import com.ktb.community.entity.post.Post;
import com.ktb.community.entity.user.User;
import com.ktb.community.exception.PostNotFoundException;
import com.ktb.community.repository.post.PostRepository;
import com.ktb.community.exception.UserNotFoundException;
import com.ktb.community.repository.user.UserRepository;
import com.ktb.community.utils.MultipartFileSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ktb.community.utils.ExceptionMessageConst.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final MultipartFileSender fileSender;

    @Transactional
    public Long addPost(PostRequestDto requestDto, Long userId) {
        if (validRequestDto(requestDto))
            throw new IllegalArgumentException(ILLEGAL_POST_REQUEST_DTO);

        String imageName = fileSender.sendFile(requestDto.getFile(), "posts");
        requestDto.setPostImage(imageName);

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Post post = postRepository.save(requestDto.toEntity(user));
        return post.getId();
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> showBoard() {
        return postRepository.findAll().stream().map(Post::toDto).toList();
    }

    @Transactional(readOnly = true)
    public PostResponseDto showPost(Long postId) {
        if (postId == null)
            throw new IllegalArgumentException(ILLEGAL_POST_ID);

        return postRepository.findById(postId).orElseThrow(PostNotFoundException::new).toDto();
    }

    @Transactional
    public void editPost(PostRequestDto requestDto) {
        if (validRequestDto(requestDto) || requestDto.getId() == null)
            throw new IllegalArgumentException(ILLEGAL_POST_REQUEST_DTO);

        Post post = postRepository.findById(requestDto.getId()).orElseThrow(PostNotFoundException::new);
        post.updatePost(requestDto);
    }

    @Transactional
    public void deletePost(Long postId) {
        if (postId == null)
            throw new IllegalArgumentException(ILLEGAL_POST_ID);

        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        post.deletePost();
    }

    private boolean validRequestDto(PostRequestDto requestDto) {
        if (requestDto == null) return true;
        return requestDto.getTitle() == null || requestDto.getContent() == null;
    }
}
