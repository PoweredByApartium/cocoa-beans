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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeneralCommandTest {

    CommandForTest commandForTest;

    TestCommandManager testCommandManager;

    TestSender sender;

    @BeforeEach
    void before() {
        commandForTest = new CommandForTest();
        testCommandManager = new TestCommandManager();

        testCommandManager.registerArgumentTypeHandler(CommandManager.COMMON_PARSERS);
        testCommandManager.addCommand(commandForTest);

        sender = new TestSender();

    }

    @Test
    void errorHandlingWithNoParser() {
        CommandForTest commandForTest = new CommandForTest();
        TestCommandManager testCommandManager = new TestCommandManager();
        assertThrows(RuntimeException.class, () -> testCommandManager.addCommand(commandForTest));

    }

    @Test
    void sample() {
        evaluate("test", "1");
        assertEquals(List.of("gotAnInt(Sender sender, int num) I got 1"), sender.getMessages());

    }

    @Test
    void skip() {
        evaluate("test", "no");
        assertEquals(List.of("skipMe(Sender sender) no", "ok(Sender sender) ok"), sender.getMessages());
    }

    @Test
    void noOne() {
        evaluate("test", "no-one");
        assertEquals(List.of("fallbackHandle(Sender sender, String label, String[] args) You can't access that method... args: [no-one]"), sender.getMessages());
    }

    @Test
    void archJoke() {
        evaluate("test", "rm -rf /* true");
        assertEquals(List.of("rm_rf_slash_asterisk(Sender sender, boolean choice) Say GoodBye ah ah ah"), sender.getMessages());
    }

    @Test
    void archJokeReverse() {
        evaluate("test", "rm -rf /* false");
        assertEquals(List.of("rm_rf_slash_asterisk(Sender sender, boolean choice) You forgot sudo ),:"), sender.getMessages());
    }

    void evaluate(String label, String args) {
        testCommandManager.handle(sender, label, args.split("\\s+"));
    }
}
