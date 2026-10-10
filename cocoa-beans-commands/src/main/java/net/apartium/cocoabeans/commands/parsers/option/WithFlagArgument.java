package net.apartium.cocoabeans.commands.parsers.option;

import net.apartium.cocoabeans.commands.parsers.CommandParserFactory;
import net.apartium.cocoabeans.commands.parsers.Scope;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(WithFlagArguments.class)
@CommandParserFactory(value = WithNamedParserFactory.class, scope = Scope.ALL)
@ApiStatus.AvailableSince("0.0.54")
public @interface WithFlagArgument {

    String keyword();
    String form();
    String[] aliases() default {};
    String usage();
    int priority() default 0;


}
