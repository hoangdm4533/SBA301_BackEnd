package com.example.demologin.annotation;

import com.example.demologin.enums.UserActionType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UserAction {
    UserActionType actionType();
    String targetType() default "";
    String description() default "";
    boolean requiresReason() default false; // true = tự động inject reason nếu thiếu
}
