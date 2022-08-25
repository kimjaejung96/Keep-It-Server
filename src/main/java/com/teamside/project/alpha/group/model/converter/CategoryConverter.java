package com.teamside.project.alpha.group.model.converter;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.group.model.enumurate.Category;

import javax.persistence.AttributeConverter;
import java.util.EnumSet;

public class CategoryConverter implements AttributeConverter<Category, String> {
    @Override
    public String convertToDatabaseColumn(Category attribute) {
        return attribute.getValue();
    }

    @Override
    public Category convertToEntityAttribute(String dbData) {
        return EnumSet.allOf(Category.class).stream()
                .filter(c -> c.getValue().equals(dbData))
                .findAny()
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.BAD_REQUEST));
    }
}
