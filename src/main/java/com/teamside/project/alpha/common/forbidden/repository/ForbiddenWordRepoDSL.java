package com.teamside.project.alpha.common.forbidden.repository;

import com.teamside.project.alpha.common.forbidden.model.enumutrate.ForbiddenWordType;

import java.util.List;

public interface ForbiddenWordRepoDSL {
    List<String> findForbiddenWords(ForbiddenWordType type);
}
