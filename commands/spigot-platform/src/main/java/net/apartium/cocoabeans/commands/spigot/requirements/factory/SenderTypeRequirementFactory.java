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
import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.commands.requirements.Requirement;
import net.apartium.cocoabeans.commands.requirements.RequirementFactory;
import net.apartium.cocoabeans.commands.spigot.SenderType;
import net.apartium.cocoabeans.commands.spigot.requirements.SenderTypeRequirement;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Requires senders running a command / sub command to be of a certain type, for example players / console only
 */
public class SenderTypeRequirementFactory implements RequirementFactory {

    @Nullable
    @Override
    public Requirement getRequirement(CommandNode node, Object obj) {
        if (!(obj instanceof SenderTypeRequirement senderTypePermission))
            return null;

        return new SenderTypeRequirementImpl(senderTypePermission.value(), senderTypePermission.invert());
    }

    private static class SenderTypeRequirementImpl implements Requirement {

        private final EnumSet<SenderType> senderTypes;
        private final boolean invert;

        public SenderTypeRequirementImpl(SenderType[] senderTypes, boolean invert) {
            this.senderTypes = EnumSet.copyOf(Arrays.asList(senderTypes));
            this.invert = invert;
        }

        @Override
        public boolean meetsRequirement(Sender sender) {
            return senderTypes.stream()
                    .anyMatch(senderType -> senderType.meetsRequirement(sender)) != invert;
        }

        @Override
        public String toString() {
            return "SenderTypeReq: [" + String.join(", ", senderTypes.stream().map(Enum::name).toList().toArray(new String[0])) + "]";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;

            if (obj == this)
                return true;

            if (!(obj instanceof SenderTypeRequirementImpl other))
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
