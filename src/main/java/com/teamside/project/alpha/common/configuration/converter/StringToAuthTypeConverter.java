package com.teamside.project.alpha.common.configuration.converter;

import com.teamside.project.alpha.member.model.enumurate.AuthType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;

@Slf4j
public class StringToAuthTypeConverter implements Converter<String, AuthType> {
    @Override
    public AuthType convert(String source) {
        if (!Arrays.asList(AuthType.values()).contains(source)) {
            return AuthType.NULL;
        }
        return AuthType.valueOf(source);
    }
}
