/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.StringHelpers;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;

import java.util.*;

public class ExampleParser extends ArgumentParser<ExampleParser.PersonInfo> {

    public ExampleParser(String keyword, int priority) {
        super(keyword, PersonInfo.class, priority);
    }

    public ExampleParser(int priority) {
        this("example", priority);
    }

    public ExampleParser() {
        this(0);
    }

    @Override
    public Optional<ParseResult<PersonInfo>> parse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int index = processingContext.index();

        if (args.size() - index < 2)
            return Optional.empty();

        String name = args.get(index);
        OptionalInt optionalAge = StringHelpers.parseInteger(args.get(index + 1));

        if (optionalAge.isEmpty())
            return Optional.empty();

        return Optional.of(new ParseResult<>(
                new PersonInfo(name, optionalAge.getAsInt()),
                index + 2
        ));
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int index = processingContext.index();

        if (args.size() - index < 2)
            return OptionalInt.empty();

        if (StringHelpers.parseInteger(args.get(index + 1)).isEmpty())
            return OptionalInt.empty();

        return OptionalInt.of(index + 2);
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int index = processingContext.index();

        if (args.isEmpty())
            return Optional.of(new TabCompletionResult(
                    Set.of(),
                    index + 1
            ));

        if (args.size() - index == 1)
            return Optional.of(new TabCompletionResult(
                    Set.of(),
                    index + 1
            ));

        if (args.size() - index != 2)
            return Optional.empty();

        OptionalInt optionalInt = StringHelpers.parseInteger(args.get(index + 1));
        if (optionalInt.isEmpty())
            return Optional.empty();


        Set<String> result = new HashSet<>();
        for (int i = optionalInt.getAsInt() == 0 ? 1 : 0; i < 10; i++)
            result.add(args.get(index + 1) + i);


        return Optional.of(new TabCompletionResult(
                result,
                index + 2
        ));
    }

    public record PersonInfo(String name, int age) {

    }

}

