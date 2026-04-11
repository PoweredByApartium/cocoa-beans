/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.commands.spigot.parsers.exception;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.parsers.ContextualMapBasedParser;
import net.apartium.cocoabeans.commands.parsers.exception.NoSuchElementInMapResponse;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a response reported via a parser when a world type is not found
 */
@ApiStatus.AvailableSince("0.0.49")
public class NoSuchWorldTypeResponse extends NoSuchElementInMapResponse {

    /**
     * Construct a new instance of invalid parser response
     *
     * @param context context of the command processing
     * @param parser  parser that reported the error
     * @param message error message
     * @param attempted attempted key
     */
    public NoSuchWorldTypeResponse(CommandProcessingContext context, ContextualMapBasedParser<?> parser, String message, String attempted) {
        super(context, parser, message, attempted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NoSuchWorldTypeResponse.NoSuchWorldTypeException getError() {
        return new NoSuchWorldTypeResponse.NoSuchWorldTypeException(this);
    }

    /**
     * Thrown to indicate that a specific world type could not be found during the parsing process.
     * This exception is used internally to wrap a {@link NoSuchWorldTypeResponse} and propagate error information.
     * <p>
     * This exception extends {@link NoSuchElementInMapResponse.NoSuchElementInMapException}, which is raised when a key is not found
     * in a map-based parser during command processing.
     *
     * @see NoSuchWorldTypeResponse
     * @see NoSuchElementInMapResponse.NoSuchElementInMapException
     * @see ContextualMapBasedParser
     */
    public class NoSuchWorldTypeException extends NoSuchElementInMapResponse.NoSuchElementInMapException {

        @ApiStatus.Internal
        private NoSuchWorldTypeException(NoSuchWorldTypeResponse response) {
            super(response);
        }

    }
}
