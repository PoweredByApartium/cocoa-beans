package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.lexer.CommandLexer;
import net.apartium.cocoabeans.commands.requirements.RequirementFactory;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Represents the context in which a command is evaluated and processed during registration.
 *
 * @param lexer                       The lexer responsible for tokenizing the command.
 * @param mapper                      The mapper used to translate arguments into their corresponding indexes.
 * @param externalRequirementFactories A collection of external requirement factories that override
 *                                     built-in behavior or provide custom requirements.
 */
@ApiStatus.AvailableSince("0.0.38")
public record EvaluationContext(
        CommandLexer lexer,
        ArgumentMapper mapper,
        Map<Class<? extends Annotation>, RequirementFactory> externalRequirementFactories
) {

    /**
     *
     * @param lexer The lexer used to tokenize the command
     * @param mapper The mapper used to map arguments into argument indexes
     */
    public EvaluationContext(CommandLexer lexer, ArgumentMapper mapper) {
        this(lexer, mapper, Map.of());
    }

}
