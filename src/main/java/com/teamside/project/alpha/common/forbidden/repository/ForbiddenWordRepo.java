package com.teamside.project.alpha.common.forbidden.repository;

import com.teamside.project.alpha.common.forbidden.model.entity.ForbiddenWordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForbiddenWordRepo extends JpaRepository<ForbiddenWordEntity, Long>, ForbiddenWordRepoDSL {
}
