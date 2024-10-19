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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class BooleanParser extends ArgumentParser<Boolean> {

    public static final String DEFAULT_KEYWORD = "boolean";

    private final Set<String> trueSet;
    private final Set<String> falseSet;
    private final Set<String> joinedSet;

    public BooleanParser(int priority) {
        this(priority, DEFAULT_KEYWORD);
    }

    @ApiStatus.AvailableSince("0.0.36")
    public BooleanParser(int priority, String keyword) {
        this(priority, keyword, Set.of("true"), Set.of("false"));
    }

    public BooleanParser(int priority, String keyword, @NotNull Set<String> trueSet, @NotNull Set<String> falseSet) {
        super(keyword, boolean.class, priority);

        if (trueSet.isEmpty()) throw new RuntimeException("True set can't be empty");
        if (falseSet.isEmpty()) throw new RuntimeException("False set can't be empty");

        this.trueSet = trueSet;
        this.falseSet = falseSet;

        Set<String> joinedSet = new HashSet<>();
        joinedSet.addAll(trueSet);
        joinedSet.addAll(falseSet);
        this.joinedSet = Collections.unmodifiableSet(joinedSet);
    }

    @Override
    public Optional<ParseResult<Boolean>> parse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int startIndex = processingContext.index();

        if (trueSet.contains(args.get(startIndex)))
            return Optional.of(new ParseResult<>(
                    true,
                    startIndex + 1
            ));

        if (falseSet.contains(args.get(startIndex)))
            return Optional.of(new ParseResult<>(
                    false,
                    startIndex + 1
            ));

        return Optional.empty();
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int startIndex = processingContext.index();

        if (joinedSet.contains(args.get(startIndex)))
            return OptionalInt.of(startIndex + 1);

        return OptionalInt.empty();
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int startIndex = processingContext.index();

        return Optional.of(new TabCompletionResult(
                joinedSet.stream()
                        .filter(s -> s.toLowerCase().startsWith(args.get(startIndex).toLowerCase()))
                        .collect(Collectors.toSet()),
                startIndex + 1
        ));
    }
}
