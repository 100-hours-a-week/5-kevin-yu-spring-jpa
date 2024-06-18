package com.ktb.community.repository.post;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class PostRepositoryCustom {

    @PersistenceContext
    private EntityManager em;
}
