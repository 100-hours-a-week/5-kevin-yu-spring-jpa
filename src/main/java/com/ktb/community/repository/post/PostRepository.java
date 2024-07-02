package com.ktb.community.repository.post;

import com.ktb.community.entity.post.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    Post save(Post post);

    List<Post> findAll();

    Optional<Post> findById(Long postId);

    void remove(Long postId);
}
