package com.ktb.community.repository.comment;

import com.ktb.community.entity.comment.Comment;
import com.ktb.community.entity.comment.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndStatus(Long id, CommentStatus status);

    List<Comment> findCommentsByPostIdAndStatus(Long postId, CommentStatus status);
}
