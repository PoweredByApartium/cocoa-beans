/*
 * Copyright (c) 2026, Lior Slakman (me@voigon.dev), ALL RIGHTS RESERVED
 * Do not use, copy, modify, and/or distribute this software without explicit permission from the
 * rights holder. Reselling this product is not allowed. Transfer of the source code to any person
 * or organization not explicitly approved by the rights holder via a license agreement is hereby forbidden.
 */

package net.apartium.cocoabeans.commands.spigot.parsers.exception;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.parsers.ContextualMapBasedParser;
import net.apartium.cocoabeans.commands.parsers.exception.NoSuchElementInMapResponse;
import org.jetbrains.annotations.ApiStatus;

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
