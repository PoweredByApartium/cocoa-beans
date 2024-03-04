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

import java.util.*;

public class LongParser extends ArgumentParser<Long> {

    public LongParser(int priority) {
        super("long", long.class, priority);
    }

    @Override
    public Optional<ParseResult<Long>> parse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int startIndex = processingContext.index();

        OptionalLong optionalLong = StringHelpers.parseLong(args.get(startIndex));
        if (optionalLong.isEmpty()) return Optional.empty();
        return Optional.of(new ParseResult<>(
                optionalLong.getAsLong(),
                startIndex + 1
        ));
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int startIndex = processingContext.index();

        OptionalLong optionalLong = StringHelpers.parseLong(args.get(startIndex));
        if (optionalLong.isEmpty()) return OptionalInt.empty();
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

        OptionalLong optionalLong = StringHelpers.parseLong(args.get(index));
        if (optionalLong.isEmpty())
            return Optional.empty();

        long asLong = optionalLong.getAsLong();
        long asLongByTen = asLong * 10;

        boolean isPositive = asLong >= 0;

        if (isPositive ? (asLong > asLongByTen) : (asLong < asLongByTen))
            return Optional.empty();

        Set<String> result = new HashSet<>();
        for (int i = asLong == 0 ? 1 : 0; i < 10; i++) {
            long num = (asLongByTen + i);
            if (isPositive ? (asLong > num) : (asLong < num))
                break;
            result.add(args.get(index) + i);
        }

        return Optional.of(new TabCompletionResult(
                result,
                index + 1
        ));
    }
}
