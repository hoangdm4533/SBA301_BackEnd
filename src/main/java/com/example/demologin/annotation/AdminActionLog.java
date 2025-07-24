package com.example.demologin.annotation;

import com.example.demologin.enums.AdminActionType;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminActionLog {
    String targetType();
    AdminActionType actionType();
} 