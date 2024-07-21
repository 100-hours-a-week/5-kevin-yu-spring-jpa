package com.ktb.community.service.comment;

import com.ktb.community.dto.comment.CommentRequestDto;
import com.ktb.community.dto.comment.CommentResponseDto;
import com.ktb.community.entity.comment.Comment;
import com.ktb.community.entity.comment.CommentStatus;
import com.ktb.community.entity.post.Post;
import com.ktb.community.entity.user.User;
import com.ktb.community.exception.CommentNotFoundException;
import com.ktb.community.exception.PostNotFoundException;
import com.ktb.community.exception.UnauthorizedUserException;
import com.ktb.community.exception.UserNotFoundException;
import com.ktb.community.repository.comment.CommentRepository;
import com.ktb.community.repository.post.PostRepository;
import com.ktb.community.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public List<CommentResponseDto> showAllComments(Long postId) {
        return commentRepository.findCommentsByPostIdAndStatus(postId, CommentStatus.ACTIVE).stream()
                .map(Comment::toDto)
                .toList();
    }

    @Transactional
    public void addComment(CommentRequestDto commentRequestDto) {
        Post post = postRepository.findById(commentRequestDto.getPostId()).orElseThrow(PostNotFoundException::new);
        User user = userRepository.findById(commentRequestDto.getUserId()).orElseThrow(UserNotFoundException::new);

        Comment comment = commentRequestDto.toEntity(post, user);

        comment.getPost().increaseCommentCount();

        commentRepository.save(comment);
    }

    @Transactional
    public void editComment(CommentRequestDto commentRequestDto) {
        Comment comment = commentRepository.findByIdAndStatus(commentRequestDto.getId(), CommentStatus.ACTIVE)
                .orElseThrow(CommentNotFoundException::new);

        authorize(commentRequestDto, comment);

        comment.updateComment(commentRequestDto);
    }

    @Transactional
    public void deleteComment(CommentRequestDto commentRequestDto) {
        Comment comment = commentRepository.findByIdAndStatus(commentRequestDto.getId(), CommentStatus.ACTIVE)
                .orElseThrow(CommentNotFoundException::new);

        authorize(commentRequestDto, comment);

        comment.getPost().decreaseCommentCount();

        comment.deleteComment();
    }

    private void authorize(CommentRequestDto commentRequestDto, Comment comment) {
        Long userId = comment.getUser().getId();
        if (!userId.equals(commentRequestDto.getUserId()))
            throw new UnauthorizedUserException();
    }
}
