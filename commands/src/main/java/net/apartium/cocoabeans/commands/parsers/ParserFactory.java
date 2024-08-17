package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandNode;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericDeclaration;
import java.util.Collection;

@ApiStatus.AvailableSince("0.0.30")
public interface ParserFactory {

    @NotNull
    Collection<ParserResult> getArgumentParser(CommandNode commandNode, Annotation annotation, GenericDeclaration obj);

    record ParserResult(ArgumentParser<?> parser, Scope scope) {

    }


}
