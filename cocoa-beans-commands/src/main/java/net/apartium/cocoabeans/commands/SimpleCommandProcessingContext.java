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
import net.apartium.cocoabeans.commands.requirements.RequirementEvaluationContext;
import net.apartium.cocoabeans.commands.requirements.RequirementResult;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A simple implementation of {@link CommandProcessingContext}
 */
@ApiStatus.AvailableSince("0.0.37")
public class SimpleCommandProcessingContext implements CommandProcessingContext {

    @NotNull
    private final Sender sender;

    private final List<String> args;

    private final int index;

    private final String label;

    private BadCommandResponse error = null;

    /**
     * Create a simple command processing context
     * @param sender sender
     * @param label label (command name)
     * @param args args split by spaces
     * @param index current index
     */
    public SimpleCommandProcessingContext(@NotNull Sender sender, String label, String[] args, int index) {
        this.sender = sender;
        this.args = List.of(args);
        this.index = index;
        this.label = label;
    }

    /**
     * Returns the sender
     * @return sender
     */
    @Override
    public @NotNull Sender sender() {
        return this.sender;
    }

    /**
     * Returns the command name
     * @return command name
     */
    @Override
    public String label() {
        return label;
    }

    /**
     * Returns the arguments
     * @return arguments
     */
    @Override
    public List<String> args() {
        return this.args;
    }

    /**
     * Returns the current index
     * @return current index
     */
    @Override
    public int index() {
        return this.index;
    }

    /**
     * check if sender meets requirement
     * @param requirement requirement to meet
     * @return requirement result
     */
    @Override
    public RequirementResult senderMeetsRequirement(Requirement requirement) {
        return requirement.meetsRequirement(new RequirementEvaluationContext(sender, label, args.toArray(new String[0]), index));
    }

    /**
     * report a problem with command parsing
     * @param source source reporter of the problem
     * @param response response error description object
     */
    @Override
    public void report(Object source, @NotNull BadCommandResponse response) {
        error = response;
    }

    /**
     * Retrieving the current report
     * @return current report
     */
    public BadCommandResponse getReport() {
        return error;
    }

    /**
     * Clear the current report
     */
    public void clearReports() {
        error = null;
    }

}
