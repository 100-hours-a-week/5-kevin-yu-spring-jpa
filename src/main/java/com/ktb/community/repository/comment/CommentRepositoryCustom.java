package com.ktb.community.repository.comment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class CommentRepositoryCustom {

    @PersistenceContext
    private EntityManager em;
}
