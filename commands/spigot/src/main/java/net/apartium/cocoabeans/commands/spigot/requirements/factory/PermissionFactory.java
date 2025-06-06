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

import net.apartium.cocoabeans.commands.GenericNode;
import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.commands.requirements.*;
import net.apartium.cocoabeans.commands.spigot.exception.PermissionException;
import net.apartium.cocoabeans.commands.spigot.requirements.Permission;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PermissionFactory implements RequirementFactory {

    @Nullable
    @Override
    public Requirement getRequirement(GenericNode node, Object obj) {
        if (obj instanceof Map<?, ?> map) {
            Object permission = map.get("value");
            Object invert = map.get("invert");

            if (permission == null || invert == null)
                return null;

            if (permission instanceof String && invert instanceof Boolean)
                return new PermissionImpl(null, (String) permission, (Boolean) invert);

            return null;
        }

        if (!(obj instanceof Permission permission))
            return null;

        return new PermissionImpl(permission, permission.value(), permission.invert());
    }

    private record PermissionImpl(Permission permission, String permissionAsString, boolean invert) implements Requirement {

        @Override
        public RequirementResult meetsRequirement(RequirementEvaluationContext context) {
            if (invert)
                return meetsInvertedRequirement(context);

            Sender sender = context.sender();

            if ((sender.getSender() == null || !(sender.getSender() instanceof CommandSender commandSender))) {
                return RequirementResult.error(new UnmetPermissionResponse(
                        this,
                        context,
                        "You must be a command sender"
                ));
            }

            if (!commandSender.hasPermission(permissionAsString))
                return RequirementResult.error(new UnmetPermissionResponse(
                        this,
                        context,
                        "You don't have permission to execute this command"
                ));

            return RequirementResult.meet();
        }

        private RequirementResult meetsInvertedRequirement(RequirementEvaluationContext context) {
            Sender sender = context.sender();

            if (sender.getSender() == null || !(sender.getSender() instanceof CommandSender commandSender))
                return RequirementResult.error(new UnmetPermissionResponse(
                        this,
                        context,
                        "You must be a command sender"
                ));

            if (commandSender.hasPermission(permissionAsString))
                return RequirementResult.error(new UnmetPermissionResponse(
                        this,
                        context,
                        "You should not have permission to execute this command"
                ));

            return RequirementResult.meet();
        }

        private class UnmetPermissionResponse extends UnmetRequirementResponse {

            public UnmetPermissionResponse(Requirement requirement, RequirementEvaluationContext context, String message) {
                super(requirement, context, message, permission);
            }

            @Override
            public PermissionException getError() {
                return new PermissionException(this, permission);
            }
        }

    }
}
