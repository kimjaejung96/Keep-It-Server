package com.teamside.project.alpha.common.forbidden.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamside.project.alpha.common.forbidden.model.entity.QForbiddenWordEntity;
import com.teamside.project.alpha.common.forbidden.model.enumutrate.ForbiddenWordType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class ForbiddenWordRepoDSLImpl implements ForbiddenWordRepoDSL{
    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    private EntityManager entityManager;
    private QForbiddenWordEntity forbiddenWord = QForbiddenWordEntity.forbiddenWordEntity;


    public ForbiddenWordRepoDSLImpl(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.entityManager = entityManager;
    }

    @Override
    public List<String> findForbiddenWords(ForbiddenWordType type) {
        return jpaQueryFactory.select(forbiddenWord.word)
                .from(forbiddenWord)
                .where(forbiddenWord.type.eq(type))
                .fetch();

    }
}
