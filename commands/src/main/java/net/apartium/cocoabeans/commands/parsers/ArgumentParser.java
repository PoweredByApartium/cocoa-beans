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
 * <br/>
 * <p>It's recommended that each implementing of ArgumentParser class should define its own {@code DEFAULT_KEYWORD}
 * to represent the default keyword</p>
 * <br/>
 * <b>Note</b>: That all parsers should have at least those 2 constructors <br/>
 * <code>Parser(int priority)</code>/<code>Parser(int priority, String keyword)</code>so we can use @WithParser on them
 * @see net.apartium.cocoabeans.commands.parsers.IntParser
 * @see net.apartium.cocoabeans.commands.parsers.StringParser
 * @see WithParser
 * @param <T> output type
 */
public abstract class ArgumentParser<T> implements Comparable<ArgumentParser<?>> {

    /**
     * Keyword of the parser that will be used to know which parser to use
     */
    private final String keyword;
    /**
     * The output class
     */
    private final Class<T> clazz;

    /**
     * The priority of the parser,
     * if we have multiple parser to know which one to try first
     */
    private final int priority;

    /**
     * Constructs a new parser
     * @param keyword keyword of the parser
     * @param clazz output class
     * @param priority priority
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
