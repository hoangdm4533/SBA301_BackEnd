package com.example.demologin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation để đánh dấu các endpoint không cần authentication
 * Các method được đánh dấu với annotation này sẽ tự động được permit all
 * trong SecurityFilterChain mà không cần khai báo thủ công
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PublicEndpoint {
}
