package net.apartium.cocoabeans.commands.exception;


import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ApiStatus.AvailableSince("0.0.22")
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionHandle {

    Class<? extends Throwable>[] value();
    int priority() default 0;

}
