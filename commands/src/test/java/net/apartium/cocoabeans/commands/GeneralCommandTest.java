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

import net.apartium.cocoabeans.CollectionHelpers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

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

        evaluate("test", "rm -rf /* false");
        assertEquals(List.of("rm_rf_slash_asterisk(Sender sender, boolean choice) You forgot sudo ),:"), sender.getMessages());
    }

    @Test
    void archJokeFail() {
        evaluate("test", "rm -rf /* trua");
        assertEquals(List.of("fallbackHandle(Sender sender, String label, String[] args) You can't access that method... args: [rm, -rf, /*, trua]"), sender.getMessages());
    }

    @Test
    void rangeTest() {
        evaluate("test", "testing 9");
        assertEquals(List.of("testRange(Sender sender, int num) You chosen 9"), sender.getMessages());
    }

    @Test
    void rangeTestFail() {
        evaluate("test", "testing -2");
        assertEquals(List.of("fallbackHandle(Sender sender, String label, String[] args) You can't access that method... args: [testing, -2]"), sender.getMessages());

        evaluate("test", "testing a1");
        assertEquals(List.of("fallbackHandle(Sender sender, String label, String[] args) You can't access that method... args: [testing, a1]"), sender.getMessages());

        evaluate("test", "testing 11");
        assertEquals(List.of("fallbackHandle(Sender sender, String label, String[] args) You can't access that method... args: [testing, 11]"), sender.getMessages());
    }

    @Test
    void range2Test() {
        evaluate("test", "testing2 6");
        assertEquals(List.of("test2Range(Sender sender, int num) You chosen 6"), sender.getMessages());
    }

    @Test
    void stringTest() {
        evaluate("test", "config get test");
        assertEquals(List.of("getConfigValue(Sender sender, String s) test = true"), sender.getMessages());

        evaluate("test", "config get key");
        assertEquals(List.of("getConfigValue(Sender sender, String s) key = true"), sender.getMessages());

        evaluate("test", "config get lol");
        assertEquals(List.of("getConfigValue(Sender sender, String s) lol = true"), sender.getMessages());
    }


    @Test
    void range2TestFail() {
        evaluate("test", "testing2 9");
        assertEquals(List.of("fallbackHandle(Sender sender, String label, String[] args) You can't access that method... args: [testing2, 9]"), sender.getMessages());
    }

    @Test
    void setSpeedTest() {
        float randomFloat = (float) Math.random();
        evaluate("test", "set speed " + randomFloat);
        assertEquals(List.of("setSpeed(Sender sender, float speed) speed has been set to " + randomFloat), sender.getMessages());
    }

    @Test
    void setSpeedFailed() {
        evaluate("test", "set speed 0.3a");
        assertEquals(List.of("fallbackHandle(Sender sender, String label, String[] args) You can't access that method... args: [set, speed, 0.3a]"), sender.getMessages());
    }

    @Test
    void tabCompletionTest() {
        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", ""),
                        List.of("arg", "diff-arg", "one", "1", "2", "3", "4", "5", "6", "7", "8", "9", "no", "rm", "testing", "testing2", "set", "config")
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                    evaluateTabCompletion("test", "te"),
                    List.of("testing", "testing2")
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "asd"),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "set speed"),
                        List.of("speed")
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "set speed 0"),
                        Stream.of(".", "1", "2", "3", "4", "5", "6", "7", "8", "9")
                                .map(s -> 0 + s)
                                .toList()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", new String[]{"testing2", ""}),
                        List.of("0", "2", "4", "6", "8")
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", new String[]{"testing", ""}),
                        List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "testing 10"),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "testing -4"),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "testing a"),
                        List.of()
                )
        );


    }


    void evaluate(String label, String args) {
        sender.getMessages().clear();
        testCommandManager.handle(sender, label, args.split("\\s+"));
    }

    List<String> evaluateTabCompletion(String label, String args) {
        return testCommandManager.handleTabComplete(sender, label, args.split("\\s+"));
    }

    List<String> evaluateTabCompletion(String label, String[] args) {
        return testCommandManager.handleTabComplete(sender, label, args);
    }

}
