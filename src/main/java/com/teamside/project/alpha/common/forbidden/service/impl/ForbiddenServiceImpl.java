package com.teamside.project.alpha.common.forbidden.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.forbidden.model.enumutrate.ForbiddenWordType;
import com.teamside.project.alpha.common.forbidden.repository.ForbiddenWordRepo;
import com.teamside.project.alpha.common.forbidden.service.ForbiddenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ForbiddenServiceImpl implements ForbiddenService {
    private final ForbiddenWordRepo forbiddenWordRepo;
    public void checkForbiddenWords(ForbiddenWordType type, String name) {
        List<String> words = forbiddenWordRepo.findForbiddenWords(type);
        if (words.stream().map(String::toLowerCase)
                .anyMatch(w -> name.toLowerCase().contains(w.toLowerCase()))) {
            throw new CustomRuntimeException(ApiExceptionCode.FORBIDDEN_NAME);
        }
    }

}
