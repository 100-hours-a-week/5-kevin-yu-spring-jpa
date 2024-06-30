package com.ktb.community.repository.post;

import com.ktb.community.dto.post.PostRequestDto;
import com.ktb.community.dto.post.PostResponseDto;
import com.ktb.community.entity.post.Post;
import com.ktb.community.entity.user.User;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.ktb.community.entity.post.QPost.post;

@Repository
public class PostRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    @Autowired
    public PostRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        queryFactory = new JPAQueryFactory(entityManager);
    }

    public PostResponseDto save(PostRequestDto dto, User user) {
        Post post = new Post();
        post.updatePost(dto);
        post.registWriter(user);

        entityManager.persist(post);

        return post.toResponseDto();
    }

    public List<PostResponseDto> findAll() {
        return queryFactory
                .select(getResponseDtoExpression())
                .from(post)
                .fetch();
    }

    public Optional<PostResponseDto> findById(Long id) {
        return Optional.ofNullable(
                queryFactory
                        .select(getResponseDtoExpression())
                        .from(post)
                        .where(post.id.eq(id))
                        .fetchOne()
        );
    }

    private QBean<PostResponseDto> getResponseDtoExpression() {
        return Projections.fields(PostResponseDto.class,
                post.id,
                post.title,
                post.content,
                post.postImage,
                post.createdAt,
                post.views,
                post.commentCount,
                post.user.nickname.as("nickname"),
                post.user.profileImage.as("profileImage"));
    }

    public void modify(Long id, PostRequestDto dto) {
        Post findPost = findPostEntity(id);

        if (findPost == null)
            throw new IllegalArgumentException("Not found post for update");

        findPost.updatePost(dto);
    }

    public void remove(Long id) {
        Post findPost = findPostEntity(id);

        if (findPost == null)
            throw new IllegalArgumentException("Not found post for delete");

        findPost.deletePost();
    }

    private Post findPostEntity(Long id) {
        return queryFactory
                .selectFrom(post)
                .where(post.id.eq(id))
                .fetchOne();
    }
}
