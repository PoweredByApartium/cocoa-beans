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

public class IntParser extends ArgumentParser<Integer> {

    public IntParser(int priority) {
        super("int", int.class, priority);
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
        int startIndex = processingContext.index();

        boolean onlyZero = true;
        char[] charArray = args.get(startIndex).toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (i == 0 && (charArray[0] == '-' || charArray[0] == '+')) continue;
            if (charArray[i] >= '0' && charArray[i] <= '9') {
                if (charArray[i] != '0') onlyZero = false;
                continue;
            }
            return Optional.empty();
        }

        Set<String> result = new HashSet<>();
        for (int i = args.get(startIndex).isEmpty() || onlyZero ? 1 : 0;
             i < 10;
             i++
        ) result.add(args.get(startIndex) + i);

        return Optional.of(new TabCompletionResult(
                result,
                startIndex + 1
        ));
    }
}
