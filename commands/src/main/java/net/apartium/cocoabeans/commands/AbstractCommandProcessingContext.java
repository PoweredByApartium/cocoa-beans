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

import net.apartium.cocoabeans.commands.requirements.Requirement;
import net.apartium.cocoabeans.commands.requirements.RequirementEvaluationContext;
import net.apartium.cocoabeans.commands.requirements.RequirementResult;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
public class AbstractCommandProcessingContext implements CommandProcessingContext {

    @NotNull
    private final Sender sender;

    private final List<String> args;

    private final int index;

    private final String label;

    public AbstractCommandProcessingContext(@NotNull Sender sender, String label, String[] args, int index) {
        this.sender = sender;
        this.args = List.of(args);
        this.index = index;
        this.label = label;
    }

    @Override
    public @NotNull Sender sender() {
        return this.sender;
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public List<String> args() {
        return this.args;
    }

    @Override
    public int index() {
        return this.index;
    }

    @Override
    public RequirementResult senderMeetsRequirement(Requirement requirement) {
        return requirement.meetsRequirement(new RequirementEvaluationContext(sender, label, args.toArray(new String[0]), index));
    }


}
