package com.mimeng.request.annotations;

import com.mimeng.request.RequestMethods;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMethod {
    RequestMethods value() default RequestMethods.GET;
}
