package net.apartium.cocoabeans.commands.spigot.game.commands.utils;

import net.apartium.cocoabeans.commands.requirements.CommandRequirementType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@CommandRequirementType(InGameFactory.class)
public @interface InGame {

    boolean invert() default false;

}
