package net.apartium.cocoabeans.commands;

import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

/**
 * Represents the result of tab completion, including a set of suggestions and a priority.
 * @param suggestions the set of suggestions
 * @param priority the priority of the suggestions
 */
@ApiStatus.AvailableSince("0.0.51")
public record TabCompletionResult(
        Set<String> suggestions,
        int priority
) {

}
