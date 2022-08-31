package com.teamside.project.alpha.common.configuration.converter;

import com.teamside.project.alpha.group.model.enumurate.MyGroupType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;

@Slf4j
public class StringToMyGroupTypeConverter implements Converter<String, MyGroupType> {
    @Override
    public MyGroupType convert(String source) {
        return Arrays.stream(MyGroupType.values()).filter(d -> d.name().equals(source)).findAny().orElse(MyGroupType.NULL);
    }
}
