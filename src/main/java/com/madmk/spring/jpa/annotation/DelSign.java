package com.madmk.spring.jpa.annotation;

import java.lang.annotation.*;

/**
 * @author madmk
 * @date 2019/12/6 15:24
 * @description: 逻辑删除字段
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DelSign {

}