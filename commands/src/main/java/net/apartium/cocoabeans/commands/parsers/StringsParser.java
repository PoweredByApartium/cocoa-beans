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

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

public class StringsParser extends ArgumentParser<String> {

    public static final String DEFAULT_KEYWORD = "strings";

    @ApiStatus.AvailableSince("0.0.36")
    public StringsParser(int priority, String keyword) {
        super(keyword, String.class, priority);
    }

    public StringsParser(int priority) {
        this(priority, DEFAULT_KEYWORD);
    }

    @Override
    public Optional<ParseResult<String>> parse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int startIndex = processingContext.index();

        String[] newArgs = new String[args.size() - startIndex];
        System.arraycopy(args.toArray(new String[0]), startIndex, newArgs, 0, newArgs.length);
        return Optional.of(new ParseResult<>(
                String.join(" ", newArgs),
                args.size()
        ));
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();

        return OptionalInt.of(args.size());
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();

        return Optional.of(new TabCompletionResult(
                Set.of(),
                args.size()
        ));
    }
}
