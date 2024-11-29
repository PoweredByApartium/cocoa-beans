/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.commands.requirements.argument;

import net.apartium.cocoabeans.commands.CommandContext;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.GenericNode;
import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.commands.requirements.ArgumentRequirement;
import net.apartium.cocoabeans.commands.requirements.ArgumentRequirementFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RangeArgumentRequirementFactory implements ArgumentRequirementFactory {

    @Override
    public @Nullable ArgumentRequirement getArgumentRequirement(GenericNode node, Object obj) {
        if (obj == null)
            return null;

        if (!(obj instanceof Range range))
            return null;

        return new RangeImpl(range.from(), range.to(), range.step());
    }

    public record RangeImpl(double from, double to, double step) implements ArgumentRequirement {


        @Override
        public boolean meetsRequirement(@NotNull Sender sender, @Nullable CommandContext context, @Nullable Object argument) {
            if (argument == null)
                return false;

            if (!(argument instanceof Double))
                return false;

            double num = (double) argument;

            if (num < from)
                return false;

            if (num >= to)
                return false;

            return (num - from) % step == 0;
        }
    }

}
