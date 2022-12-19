package com.teamside.project.alpha.common.custom_annotaion;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GroupAuthCheck {
}
