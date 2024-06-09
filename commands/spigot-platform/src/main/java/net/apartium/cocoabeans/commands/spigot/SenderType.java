/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.commands.spigot;

import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.commands.requirements.Requirement;
import net.apartium.cocoabeans.commands.requirements.RequirementError;
import net.apartium.cocoabeans.commands.requirements.RequirementEvaluationContext;
import net.apartium.cocoabeans.commands.requirements.RequirementResult;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public enum SenderType implements Requirement {

    /**
     * @see Player
     */
    PLAYER {

        private static final String ERROR_MESSAGE = "This command can only be used by players";

        @Override
        public RequirementResult meetsRequirement(RequirementEvaluationContext context) {
            Sender sender = context.sender();
            if (sender == null || !(sender.getSender() instanceof Player))
                return RequirementResult.error(new RequirementError(
                        this,
                        context,
                        ERROR_MESSAGE
                ));

            return RequirementResult.meet();
        }

    },

    /**
     * @see org.bukkit.block.CommandBlock
     */
    BLOCK {

        private static final String ERROR_MESSAGE = "This command can only be used by command block";

        @Override
        public RequirementResult meetsRequirement(RequirementEvaluationContext context) {
            Sender sender = context.sender();
            if (sender == null || !(sender.getSender() instanceof BlockCommandSender))
                return RequirementResult.error(new RequirementError(
                        this,
                        context,
                        ERROR_MESSAGE
                ));

            return RequirementResult.meet();
        }
    },

    /**
     * @see ConsoleCommandSender
     */
    CONSOLE {

        private static final String ERROR_MESSAGE = "This command can only be used by console";

        @Override
        public RequirementResult meetsRequirement(RequirementEvaluationContext context) {
            Sender sender = context.sender();
            if (sender == null || !(sender.getSender() instanceof ConsoleCommandSender))
                return RequirementResult.error(new RequirementError(
                        this,
                        context,
                        ERROR_MESSAGE
                ));

            return RequirementResult.meet();
        }
    },

    /**
     * @see Entity
     * @see Player
     */
    ENTITY {

        private static final String ERROR_MESSAGE = "This command can only be used by entities";

        @Override
        public RequirementResult meetsRequirement(RequirementEvaluationContext context) {
            Sender sender = context.sender();
            if (sender == null || !(sender.getSender() instanceof Entity))
                return RequirementResult.error(new RequirementError(
                        this,
                        context,
                        ERROR_MESSAGE
                ));

            return RequirementResult.meet();
        }
    }

    ;


}
