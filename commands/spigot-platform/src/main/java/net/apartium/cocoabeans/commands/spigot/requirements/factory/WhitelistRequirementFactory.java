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

import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.GenericNode;
import net.apartium.cocoabeans.commands.requirements.*;
import net.apartium.cocoabeans.commands.spigot.exception.WhitelistException;
import net.apartium.cocoabeans.commands.spigot.requirements.Whitelist;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Allows to specify a hard-coded uuid whitelist for sender allowed to run a command
 */
public class WhitelistRequirementFactory implements RequirementFactory {

    @Nullable
    @Override
    public Requirement getRequirement(GenericNode node, Object obj) {
        if (!(obj instanceof Whitelist whitelist))
            return null;

        return new WhitelistRequirementImpl(
                whitelist,
                Arrays.stream(whitelist.value())
                        .map(UUID::fromString)
                        .collect(Collectors.toSet()),
                whitelist.consoleBypass(),
                whitelist.invert()
        );
    }

    private static class WhitelistRequirementImpl implements Requirement {

        private final Whitelist whitelist;
        private final Set<UUID> uuids;
        private final boolean consoleBypass;
        private final boolean invert;

        public WhitelistRequirementImpl(Whitelist whitelist, Set<UUID> uuids, boolean consoleBypass, boolean invert) {
            this.whitelist = whitelist;
            this.uuids = uuids;
            this.consoleBypass = consoleBypass;
            this.invert = invert;
        }

        @Override
        public RequirementResult meetsRequirement(RequirementEvaluationContext context) {
            Object senderObj = context.sender().getSender();
            if (senderObj == null)
                return invert ? RequirementResult.meet() : RequirementResult.error(
                        new UnmetWhitelistResponse(
                                this,
                                context,
                                "Sender is null"
                        )
                );

            if (!(senderObj instanceof Player player))
                return (consoleBypass && senderObj instanceof ConsoleCommandSender) != invert
                        ? RequirementResult.meet()
                        : RequirementResult.error(
                        new UnmetWhitelistResponse(
                                this,
                                context,
                                invert ? "Invert console by pass lolllllllllll" : "Sender is not a player"
                        )
                );

            return uuids.contains(player.getUniqueId()) != invert
                    ? RequirementResult.meet()
                    : RequirementResult.error(
                    new UnmetWhitelistResponse(
                            this,
                            context,
                            "Sender is " + (invert ? "in the blacklist" : "not whitelisted")
                    ));
        }

        private class UnmetWhitelistResponse extends UnmetRequirementResponse<WhitelistException> {
            public UnmetWhitelistResponse(Requirement requirement, RequirementEvaluationContext context, String message) {
                super(requirement, context, message, whitelist);
            }

            @Override
            public WhitelistException getError() {
                return new WhitelistException(this, whitelist);
            }
        }
    }
}
