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
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public enum SenderType implements Requirement {

    /**
     * @see Player
     */
    PLAYER {
        @Override
        public boolean meetsRequirement(Sender sender) {
            return sender.getSender() != null && sender.getSender() instanceof Player;
        }

        @Override
        public RequirementError getError(String commandName, String[] args, int depth) {
            return new RequirementError(this, commandName, args, depth, "This command can only be used by players");
        }
    },

    /**
     * @see org.bukkit.block.CommandBlock
     */
    BLOCK {
        @Override
        public boolean meetsRequirement(Sender sender) {
            return sender.getSender() != null && sender.getSender() instanceof BlockCommandSender;
        }

        @Override
        public RequirementError getError(String commandName, String[] args, int depth) {
            return new RequirementError(this, commandName, args, depth, "This command can only be used by command block");
        }
    },

    /**
     * @see ConsoleCommandSender
     */
    CONSOLE {
        @Override
        public boolean meetsRequirement(Sender sender) {
            return sender.getSender() != null && sender.getSender() instanceof ConsoleCommandSender;
        }

        @Override
        public RequirementError getError(String commandName, String[] args, int depth) {
            return new RequirementError(this, commandName, args, depth, "This command can only be used by console");
        }
    },

    /**
     * @see Entity
     * @see Player
     */
    ENTITY {
        @Override
        public boolean meetsRequirement(Sender sender) {
            return sender.getSender() != null && sender.getSender() instanceof Entity;
        }

        @Override
        public RequirementError getError(String commandName, String[] args, int depth) {
            return new RequirementError(this, commandName, args, depth, "This command can only be used by entities");
        }
    }

    ;


}
