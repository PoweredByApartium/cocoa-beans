/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.commands.parsers.factory;

import net.apartium.cocoabeans.StringHelpers;
import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.commands.parsers.IntRangeParser;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class IntRangeParserFactory implements ParserFactory {

    @Override
    public ArgumentParser<?> getArgumentParser(Object obj) {
        if (!(obj instanceof IntRangeParser intRangeParser))
            return null;

        if (intRangeParser.from() > intRangeParser.to())
            throw new RuntimeException("from must be smaller than to");
        if (intRangeParser.step() <= 0)
            throw new RuntimeException("step must be larger than 0");

        return new IntRangeParserImpl(intRangeParser.from(), intRangeParser.to(), intRangeParser.step(), intRangeParser.keyword(), intRangeParser.priority());
    }

    private class IntRangeParserImpl extends ArgumentParser<Integer> {

        private final int from;
        private final int to;
        private final int step;

        public IntRangeParserImpl(int from, int to, int step, String keyword, int priority) {
            super(keyword, int.class, priority);
            this.from = from;
            this.to = to;
            this.step = step;
        }

        @Override
        public Optional<ParseResult<Integer>> parse(CommandProcessingContext processingContext) {
            List<String> args = processingContext.args();
            int index = processingContext.index();

            OptionalInt optionalInt = StringHelpers.parseInteger(args.get(index));
            if (optionalInt.isEmpty())
                return Optional.empty();

            int targetNum = optionalInt.getAsInt();
            if ((targetNum - from) % step != 0)
                return Optional.empty();

            if (targetNum >= to)
                return Optional.empty();

            return Optional.of(new ParseResult<>(
                    targetNum,
                    index + 1
            ));
        }

        @Override
        public OptionalInt tryParse(CommandProcessingContext processingContext) {
            List<String> args = processingContext.args();
            int index = processingContext.index();

            OptionalInt optionalInt = StringHelpers.parseInteger(args.get(index));
            if (optionalInt.isEmpty())
                return OptionalInt.empty();

            int targetNum = optionalInt.getAsInt();

            if (targetNum < from)
                return OptionalInt.empty();

            if (targetNum >= to)
                return OptionalInt.empty();

            if ((targetNum - from) % step != 0)
                return OptionalInt.empty();


            return OptionalInt.of(index + 1);
        }

        @Override
        public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
            return Optional.empty();
        }

    }
}
