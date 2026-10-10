package net.apartium.cocoabeans.commands.parsers.option;

import net.apartium.cocoabeans.commands.parsers.*;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(WithOptionArguments.class)
@CommandParserFactory(value = WithNamedParserFactory.class, scope = Scope.ALL)
@ApiStatus.AvailableSince("0.0.54")
public @interface WithOptionArgument {

    String keyword();
    String form();
    String[] aliases() default {};
    String usage();
    Class<? extends ArgumentParser<?>> parser();
    int priority() default 0;


}
