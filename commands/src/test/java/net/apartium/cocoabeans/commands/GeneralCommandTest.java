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

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeneralCommandTest {

    @Test
    void testNoParser() {
        CommandForTest commandForTest = new CommandForTest();
        TestCommandManager testCommandManager = new TestCommandManager();
        assertThrows(RuntimeException.class, () -> testCommandManager.addCommand(commandForTest));

    }

    @Test
    void sampleTest() {
        TestSender sender = run("test", new String[]{"1"});
        assertEquals(List.of("gotAnInt(Sender sender, int num) I got 1"), sender.getMessages());

    }

    @Test
    void skipTest() {
        TestSender sender = run("test", new String[]{"no"});
        assertEquals(List.of("skipMe(Sender sender) no", "ok(Sender sender) ok"), sender.getMessages());
    }

    TestSender run(String label, String[] args) {
        CommandForTest commandForTest = new CommandForTest();
        TestCommandManager testCommandManager = new TestCommandManager();

        testCommandManager.registerArgumentTypeHandler(CommandManager.COMMON_PARSERS);
        testCommandManager.addCommand(commandForTest);

        TestSender sender = new TestSender();
        testCommandManager.handle(sender, label, args);

        return sender;
    }
}
