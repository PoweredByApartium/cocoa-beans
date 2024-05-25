/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.commands.parsers;


import net.apartium.cocoabeans.commands.CommandProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

/**
 * Base class for all parsers
 * @see net.apartium.cocoabeans.commands.parsers.IntParser
 * @see net.apartium.cocoabeans.commands.parsers.StringParser
 * @param <T>
 */
public abstract class ArgumentParser<T> implements Comparable<ArgumentParser<?>> {

    private final String keyword;
    private final Class<T> clazz;
    private final int priority;

    /**
     * Constructs a
     * @param keyword
     * @param clazz
     * @param priority
     */
    protected ArgumentParser(String keyword, Class<T> clazz, int priority) {
        this.keyword = keyword;
        this.clazz = clazz;
        this.priority = priority;
    }

    /**
     * Tries to parse next argument in the context
     * @param processingContext cmd processing context
     * @return empty if failed, otherwise result
     */
    public abstract Optional<ParseResult<T>> parse(CommandProcessingContext processingContext);

    /**
     * Tries to lazily parse next argument in the context
     * @param processingContext cmd processing context
     * @return new index int if success, empty if not
     */
    public abstract OptionalInt tryParse(CommandProcessingContext processingContext);

    /**
     * Retrieves available options for tab completion of this argument
     * @param processingContext cmd processing context
     * @return tab completetion result if success, otherwise empty optiona
     */
    public abstract Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext);

    /**
     * Get argument type
     * @return
     */
    public Class<T> getArgumentType() {
        return clazz;
    }

    /**
     * Get priority
     * @return
     */
    public int getPriority() {
        return priority;
    }

    public record ParseResult<T>(
            T result,
            int newIndex
    ) { }

    public record TabCompletionResult(
            Set<String> result,
            int newIndex
    ) { }

    @Override
    public int compareTo(@NotNull ArgumentParser<?> other) {
        return Integer.compare(priority, other.priority);
    }

    /**
     * Get type keyword, like int or string
     * @return type keyword
     */
    public String getKeyword() {
        return keyword;
    }
}
