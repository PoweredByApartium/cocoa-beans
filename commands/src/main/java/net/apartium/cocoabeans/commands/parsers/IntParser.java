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

import net.apartium.cocoabeans.StringHelpers;
import net.apartium.cocoabeans.commands.CommandProcessingContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class IntParser extends ArgumentParser<Integer> {

    public static final String DEFAULT_KEYWORD = "int";

    /**
     * Creates a new int parser
     * @param priority parser priority of which should be higher than others or lower
     * @param keyword parser keyword
     */
    @ApiStatus.AvailableSince("0.0.36")
    public IntParser(int priority, String keyword) {
        super(keyword, int.class, priority);
    }

    /**
     * Creates a new int parser
     * @param priority parser priority of which should be higher than others or lower
     */
    public IntParser(int priority) {
        this(priority, DEFAULT_KEYWORD);
    }

    @Override
    public Optional<ParseResult<Integer>> parse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int startIndex = processingContext.index();

        OptionalInt optionalInt = StringHelpers.parseInteger(args.get(startIndex));
        if (optionalInt.isEmpty()) return Optional.empty();
        return Optional.of(new ParseResult<>(
                optionalInt.getAsInt(),
                startIndex + 1
        ));
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int startIndex = processingContext.index();

        OptionalInt optionalInt = StringHelpers.parseInteger(args.get(startIndex));
        if (optionalInt.isEmpty()) return OptionalInt.empty();
        return OptionalInt.of(startIndex + 1);
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int index = processingContext.index();

        if (args.get(index).isEmpty())
            return Optional.of(new TabCompletionResult(
                    Set.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "-"),
                    index + 1
            ));

        OptionalInt optionalInt = StringHelpers.parseInteger(args.get(index));
        if (optionalInt.isEmpty())
            return Optional.empty();

        int asInt = optionalInt.getAsInt();
        int asIntByTen = asInt * 10;

        boolean isPositive = asInt >= 0;

        if (isPositive ? (asInt > asIntByTen) : (asInt < asIntByTen))
            return Optional.empty();

        Set<String> result = new HashSet<>();
        for (int i = asInt == 0 ? 1 : 0; i < 10; i++) {
            int num = (asIntByTen + i);
            if (isPositive ? (asInt > num) : (asInt < num))
                break;
            result.add(args.get(index) + i);
        }

        return Optional.of(new TabCompletionResult(
                result,
                index + 1
        ));
    }
}
