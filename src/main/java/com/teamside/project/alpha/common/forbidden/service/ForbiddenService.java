package com.teamside.project.alpha.common.forbidden.service;

import com.teamside.project.alpha.common.forbidden.model.enumutrate.ForbiddenWordType;

public interface ForbiddenService {
    void checkForbiddenWords(ForbiddenWordType type, String name);
}
