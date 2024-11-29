/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.commands.spigot.requirements.factory;

import net.apartium.cocoabeans.CollectionHelpers;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.GenericNode;
import net.apartium.cocoabeans.commands.requirements.*;
import net.apartium.cocoabeans.commands.spigot.SenderType;
import net.apartium.cocoabeans.commands.spigot.exception.SenderLimitException;
import net.apartium.cocoabeans.commands.spigot.requirements.SenderLimit;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * Requires senders running a command / sub command to be of a certain type, for example players / console only
 */
public class SenderLimitFactory implements RequirementFactory {

    @Nullable
    @Override
    public Requirement getRequirement(GenericNode node, Object obj) {
        if (!(obj instanceof SenderLimit senderLimit))
            return null;

        return new SenderLimitImpl(senderLimit, senderLimit.value(), senderLimit.invert());
    }

    private static class SenderLimitImpl implements Requirement {

        private final SenderLimit senderLimit;
        private final EnumSet<SenderType> senderTypes;
        private final boolean invert;

        public SenderLimitImpl(SenderLimit senderLimit, SenderType[] senderTypes, boolean invert) {
            this.senderLimit = senderLimit;
            this.senderTypes = EnumSet.copyOf(Arrays.asList(senderTypes));
            this.invert = invert;
        }

        @Override
        public RequirementResult meetsRequirement(RequirementEvaluationContext context) {
            for (SenderType senderType : senderTypes) {
                RequirementResult requirementResult = senderType.meetsRequirement(context);
                if (invert) {
                    if (requirementResult.meetRequirement())
                        return RequirementResult.error(new UnmetSenderLimit(
                                this,
                                context,
                                "This command can not be used by " + String.join(", ", senderTypes.stream().map(Enum::name).toList().toArray(new String[0])) + "s"
                        ));

                    continue;
                }

                if (!requirementResult.meetRequirement())
                    continue;

                return requirementResult;
            }

            if (invert)
                return RequirementResult.meet();

            return RequirementResult.error(new UnmetSenderLimit(
                    this,
                    context,
                    "This command can only be used by " + String.join(", ", senderTypes.stream().map(Enum::name).toList().toArray(new String[0])) + "s"
            ));
        }

        @Override
        public List<Class<?>> getTypes() {
            return List.of(SenderType.class);
        }

        private class UnmetSenderLimit extends UnmetRequirementResponse {

            public UnmetSenderLimit(Requirement requirement, RequirementEvaluationContext context, String message) {
                super(requirement, context, message, senderLimit);
            }

            @Override
            public Exception getError() {
                return new SenderLimitException(this, senderLimit);
            }
        }

        @Override
        public String toString() {
            return "SenderLimit: [" + String.join(", ", senderTypes.stream().map(Enum::name).toList().toArray(new String[0])) + "]";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;

            if (obj == this)
                return true;

            if (!(obj instanceof SenderLimitImpl other))
                return false;

            if (this.senderTypes.size() != other.senderTypes.size())
                return false;

            if (this.invert != other.invert)
                return false;

            if (this.senderTypes.isEmpty())
                return true;

            return CollectionHelpers.equalsArray(this.senderTypes.toArray(new SenderType[0]), other.senderTypes.toArray(new SenderType[0]));
        }
    }
}
