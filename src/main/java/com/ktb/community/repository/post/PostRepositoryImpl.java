package com.ktb.community.repository.post;

import com.ktb.community.entity.post.Post;
import com.ktb.community.entity.post.PostStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.ktb.community.entity.post.QPost.post;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    @Autowired
    public PostRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Post save(Post post) {
        entityManager.persist(post);
        return post;
    }

    @Override
    public List<Post> findAll() {
        return queryFactory
                .selectFrom(post)
                .orderBy(post.createdAt.desc())
                .where(post.status.eq(PostStatus.ACTIVE))
                .fetch();
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(post)
                        .where(post.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public void remove(Long id) {
        Post post = findById(id).orElseThrow();
        post.deletePost();
    }
}
