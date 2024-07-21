package com.ktb.community.service.post;

import com.ktb.community.dto.post.PostRequestDto;
import com.ktb.community.dto.post.PostResponseDto;
import com.ktb.community.entity.post.Post;
import com.ktb.community.entity.user.User;
import com.ktb.community.exception.PostNotFoundException;
import com.ktb.community.exception.UnauthorizedUserException;
import com.ktb.community.exception.UserNotFoundException;
import com.ktb.community.repository.post.PostRepository;
import com.ktb.community.repository.user.UserRepository;
import com.ktb.community.utils.ClientServerHandler;
import com.ktb.community.utils.ClientServerHandlerMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final ClientServerHandler clientServerHandler;

    @Transactional
    public Long addPost(PostRequestDto requestDto, MultipartFile file, Long userId) {
        String imageName = file == null ? "" : clientServerHandler.sendFile(file, "", "posts", ClientServerHandlerMethod.POST);
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
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        post.increaseViews();

        return post.toDto();
    }

    @Transactional
    public String editPost(PostRequestDto requestDto, Long userId) {
        Post post = postRepository.findById(requestDto.getId()).orElseThrow(PostNotFoundException::new);

        if (!userId.equals(post.getUser().getId()))
            throw new UnauthorizedUserException("해당 게시글을 수정할 권한이 없는 사용자입니다.");

        String prevImage = post.getPostImage();

        if ("".equals(requestDto.getPostImage()))
            requestDto.setPostImage(prevImage);

        post.updatePost(requestDto);

        return prevImage.equals(requestDto.getPostImage()) ? "" : prevImage;
    }

    @Transactional
    public String deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

        if (!userId.equals(post.getUser().getId()))
            throw new UnauthorizedUserException("해당 게시글을 삭제할 권한이 없는 사용자입니다.");

        String prevImage = post.getPostImage();

        post.deletePost();

        return prevImage;
    }
}
