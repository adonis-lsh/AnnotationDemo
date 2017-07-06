package com.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by adonis_lsh on 2017/7/3
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Factory {
    /**
     *工厂的名字
     */
    Class type();

    /**
     *用来表示生成那个对象的唯一id
     */
    String id();
}
