package com.teamside.project.alpha.common.configuration.converter;

import com.teamside.project.alpha.member.model.enumurate.AuthType;
import org.springframework.core.convert.converter.Converter;

public class StringToAuthTypeConverter implements Converter<String, AuthType> {
    @Override
    public AuthType convert(String source) {
        return AuthType.valueOf(source);
    }
}
