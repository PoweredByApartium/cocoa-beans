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

import net.apartium.cocoabeans.commands.exception.CommandException;
import net.apartium.cocoabeans.commands.exception.UnknownCommandResponse;
import net.apartium.cocoabeans.commands.lexer.SimpleCommandLexer;

import java.util.logging.Logger;

public class TestCommandManager extends CommandManager {

    public TestCommandManager() {
        super(Logger.getLogger(TestCommandManager.class.getName()), new SimpleArgumentMapper(), new SimpleCommandLexer());

    }

    @Override
    protected void addCommand(CommandNode commandNode, Command command) {

    }

    @Override
    public boolean handle(Sender sender, String commandName, String[] args) {
        try {
            return super.handle(sender, commandName, args);
        } catch (UnknownCommandResponse.UnknownCommandException e) {
            sender.sendMessage("Unknown command: " + e.getCommandName());
            return false;
        } catch (CommandException e) {
            sender.sendMessage(e.getMessage() == null ? "null" : e.getMessage());
            return false;
        } catch (Throwable e) {
            e.printStackTrace();
            sender.sendMessage("oh no");
            return false;
        }
    }
}
