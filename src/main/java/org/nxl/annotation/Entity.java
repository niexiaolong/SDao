package org.nxl.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) // 此注解仅作用于类
@Retention(RetentionPolicy.RUNTIME) // 此注解一直保留到运行时
public @interface Entity {
}
