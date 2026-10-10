package net.apartium.cocoabeans.commands.parsers.option;

import net.apartium.cocoabeans.commands.parsers.CommandParserFactory;
import net.apartium.cocoabeans.commands.parsers.Scope;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@CommandParserFactory(value = WithNamedParserFactory.class, scope = Scope.ALL)
@ApiStatus.AvailableSince("0.0.54")
public @interface WithOptionArguments {

    WithOptionArgument[] value();

}
