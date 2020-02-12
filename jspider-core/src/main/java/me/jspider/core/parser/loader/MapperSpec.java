package me.jspider.core.parser.loader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A specification for {@link SpiderItemMapper}.
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MapperSpec {
    String json() default "";

    String xpath() default "";

    String css() default "";
}
