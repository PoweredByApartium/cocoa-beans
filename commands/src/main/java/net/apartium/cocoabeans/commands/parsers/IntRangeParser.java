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

import net.apartium.cocoabeans.commands.parsers.factory.IntRangeParserFactory;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A parser that parses an integer range
 * @see IntParser
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@CommandParserFactory(IntRangeParserFactory.class)
@ApiStatus.AvailableSince("0.0.30")
public @interface IntRangeParser {

    /**
     * Lower bound of the range (excl)
     * @return the lower bound of the range
     */
    int from() default 0;

    /**
     * Upper bound of the range (excl)
     * @return the upper bound of the range
     */
    int to();

    /**
     * Input int must be divisible by this number
     * @return step
     */
    int step() default 1;

    /**
     * Keyword for the parser, default is "range"
     * @return the keyword for the parser
     * @see ArgumentParser#getKeyword()
     */
    String keyword() default "range";

    /**
     * Parser priority
     * @return the priority of the parser
     * @see ArgumentParser#getPriority()
     */
    int priority() default 0;

}
