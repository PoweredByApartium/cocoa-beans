package net.apartium.cocoabeans.commands;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to provide a short description for a class or method.
 * It can be applied to both classes and methods.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ApiStatus.AvailableSince("0.0.30")
public @interface Description {

    String value();

}
