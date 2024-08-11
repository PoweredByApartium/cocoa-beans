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

import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import net.apartium.cocoabeans.commands.requirements.Requirement;
import net.apartium.cocoabeans.commands.requirements.RequirementResult;
import net.apartium.cocoabeans.commands.requirements.UnmetRequirementResponse;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import java.util.List;

/**
 * Represents the context of processing the command's arguments
 */
public interface CommandProcessingContext {

    /**
     * Sender instance
     * @return sender instance
     */
    @NotNull Sender sender();

    /**
     * Returns the label of the command
     * @return label of the command
     */
    String label();

    /**
     * Returns an unmodifiable list representing all the args left to process
     * @return unmodifiable list representing all the args left to process
     */
    List<String> args();

    /**
     * Returns the current index of processing
     * @return current index of processing
     */
    int index();

    /**
     * Check if sender meets given requirement instance.
     * @param requirement requirement to meet
     * @return requirement result
     */
    RequirementResult senderMeetsRequirement(Requirement requirement);

    /**
     * Report a problem with command parsing, such as invalid arguments
     * @param source source reporter of the problem
     * @param response response error description object
     */
    @ApiStatus.AvailableSince("0.0.30")
    void report(Object source, @NotNull BadCommandResponse response);

}
