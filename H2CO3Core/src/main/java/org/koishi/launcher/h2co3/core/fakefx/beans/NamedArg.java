package org.koishi.launcher.h2co3.core.fakefx.beans;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation that provides information about argument's name.
 *
 * @since JavaFX 8.0
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface NamedArg {
    /**
     * The name of the annotated argument.
     *
     * @return the name of the annotated argument
     */
    public String value();

    /**
     * The default value of the annotated argument.
     *
     * @return the default value of the annotated argument
     */
    public String defaultValue() default "";
}
