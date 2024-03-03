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
import net.apartium.cocoabeans.commands.requirements.Requirement;
import net.apartium.cocoabeans.commands.requirements.argument.Range;
import net.apartium.cocoabeans.commands.requirements.argument.RangeArgumentRequirementFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class

GeneralCommandTest {

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
    void noArgs() {
        evaluate("test", "");
        assertEquals(List.of("no args :)"), sender.getMessages());
    }

    @Test
    void errorHandlingWithNoParser() {
        CommandForTest commandForTest = new CommandForTest();
        TestCommandManager testCommandManager = new TestCommandManager();
        assertThrows(RuntimeException.class, () -> testCommandManager.addCommand(commandForTest));

    }

    @Test
    void testingExample() {
        evaluate("test", "testing example kfir 23");
        assertEquals(List.of("testingExample(Sender sender, ExampleParser.PersonInfo personInfo) name: kfir, age: 23"), sender.getMessages());
    }

    @Test
    void senderMeetsRequirementTest() {
        CommandProcessingContext processingContext = new AbstractCommandProcessingContext(sender, new String[0], 0);
        assertTrue(processingContext.senderMeetsRequirement(sender -> true));
        assertFalse(processingContext.senderMeetsRequirement(sender -> false));
    }

    @Test
    void sharedSecretsTest() {
        assertEquals(SharedSecrets.LOGGER.getName(), "cocoabeans-commands");
    }

    @Test
    void aliasTest() {
        testCommandManager = new TestCommandManager();
        testCommandManager.addCommand(new SecondCommandForTest());

        evaluate("test", "");
        assertEquals(List.of("You don't have access to use this command!"), sender.getMessages());
        evaluate("t", "");
        assertEquals(List.of("You don't have access to use this command!"), sender.getMessages());
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
    void longTest() {
        evaluate("test", "test 218");
        assertEquals(List.of("testLong(Sender sender, long num) got 218"), sender.getMessages());

        evaluate("test", "test 2185");
        assertEquals(List.of("testLong(Sender sender, long num) got 2185"), sender.getMessages());
    }

    @Test
    void longTestFail() {
        evaluate("test", "test 564a");
        assertEquals(List.of("fallbackHandle(Sender sender, String label, String[] args) You can't access that method... args: [test, 564a]"), sender.getMessages());
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
    void testing3() {
        evaluate("test", "testing3");
        assertEquals(List.of("testing3(TestSender sender) cool"), sender.getMessages());
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
    void setWalkSpeedAndFlightSpeed() {
        float randomWalkSpeed = (float) Math.random();
        float randomFlightSpeed = (float) Math.random();
        evaluate("test", "set speed " + randomWalkSpeed + " and flyspeed " + randomFlightSpeed);
        assertEquals(List.of("setSpeedAndFlySpeed(Sender sender, float walkingSpeed, float flyingSpeed) walk speed has been set to " + randomWalkSpeed + " while flying speed has been set to " + randomFlightSpeed), sender.getMessages());
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
    void testingArg() {
        evaluate("test", "testing-arg wowwww");
        assertEquals(List.of("fallbackHandle(Sender sender, String label, String[] args) You can't access that method... args: [testing-arg, wowwww]"), sender.getMessages());
    }

    @Test
    void throwAnError() {
        assertThrows(NullPointerException.class, () -> evaluate("test", "evil"));
    }

    @Test
    void rangeTestArg() {
        evaluate("test", "testing-arg 6");
        assertEquals(List.of("testRangeArg(Sender sender, @Range(to = 10) double num) 6.0"), sender.getMessages());

        evaluate("test", "testing-arg 3");
        assertEquals(List.of("testRangeArg(Sender sender, @Range(to = 10) double num) 3.0"), sender.getMessages());

        evaluate("test", "testing-arg 1");
        assertEquals(List.of("testRangeArg(Sender sender, @Range(to = 10) double num) 1.0"), sender.getMessages());

        evaluate("test", "testing-arg 9");
        assertEquals(List.of("testRangeArg(Sender sender, @Range(to = 10) double num) 9.0"), sender.getMessages());

        // Fails

        evaluate("test", "testing-arg 2.1");
        assertEquals(List.of("fallbackHandle(Sender sender, String label, String[] args) You can't access that method... args: [testing-arg, 2.1]"), sender.getMessages());

        evaluate("test", "testing-arg -1");
        assertEquals(List.of("fallbackHandle(Sender sender, String label, String[] args) You can't access that method... args: [testing-arg, -1]"), sender.getMessages());

        evaluate("test", "testing-arg 9.1");
        assertEquals(List.of("fallbackHandle(Sender sender, String label, String[] args) You can't access that method... args: [testing-arg, 9.1]"), sender.getMessages());

        evaluate("test", "testing-arg 10");
        assertEquals(List.of("fallbackHandle(Sender sender, String label, String[] args) You can't access that method... args: [testing-arg, 10]"), sender.getMessages());

        evaluate("test", "testing-arg 2a");
        assertEquals(List.of("fallbackHandle(Sender sender, String label, String[] args) You can't access that method... args: [testing-arg, 2a]"), sender.getMessages());
    }

    @Test
    void tryTest() {
        double randomDouble = Math.random();
        evaluate("test", "try random-string " + randomDouble);
        assertEquals(List.of("tryDouble(Sender sender, double num) I ignore the second argument it wasn't important also your number is " + randomDouble), sender.getMessages());
    }


    @Test
    void yesMeow() {
        evaluate("test", "yes meow");
        assertEquals(List.of("yesMeow(Sender sender) ok"), sender.getMessages());

        // Fail
        evaluate("test", "yes meOw");
        assertEquals(List.of("fallbackHandle(Sender sender, String label, String[] args) You can't access that method... args: [yes, meOw]"), sender.getMessages());
    }

    @Test
    void stringsTest() {
        evaluate("test", "send hey");
        assertEquals(List.of("sendMessageToSender(Sender sender, String message) message have been sent: hey"), sender.getMessages());

        evaluate("test", "send broadcast a test message");
        assertEquals(List.of("sendMessageToSender(Sender sender, String message) message have been sent: broadcast a test message"), sender.getMessages());
    }

    @Test
    void test() {
        assertEquals(
                evaluateTabCompletion("test", "testing example kfir 2").stream().sorted().toList(),
                List.of("20", "21", "22", "23", "24", "25", "26", "27", "28", "29")
        );
    }

    @Test
    void tabCompletionTest() {
        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", ""),
                        List.of("arg", "diff-arg", "one", "1", "2", "3", "4", "5", "6", "7", "8", "9", "no", "rm", "yes", "test", "testing", "testing-arg", "testing2", "testing3", "set", "send", "config", "try", "evil")
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "0002"),
                        List.of("00020", "00021", "00022", "00023", "00024", "00025", "00026", "00027", "00028", "00029")
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", new String[]{"send", ""}),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "send test"),
                        List.of()
                )
        );


        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", new String[]{"send", "wow", ""}),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                    evaluateTabCompletion("test", "te"),
                    List.of("test", "testing", "testing-arg", "testing2", "testing3")
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", new String[]{"testing3", ""}),
                        List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "true", "false")
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
                        evaluateTabCompletion("test", "config get testas"),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", new String[]{"config", "get", ""}),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", new String[]{"config", "get", "test", ""}),
                        List.of()
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
                        List.of("example", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
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

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", new String[]{"rm", "-rf", "/*", ""}),
                        List.of("true", "false")
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "set speed 0.253.1"),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "set speed ++0.52"),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "try speed 0.253.1"),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "try speed ++0.52"),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", new String[]{"test", ""}),
                        List.of("1", "2", "3", "4", "5", "6", "7", "8", "9")
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", new String[]{"test", "21", ""}),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", new String[]{"test", "712"}),
                        List.of("7120", "7121", "7122", "7123", "7124", "7125", "7126", "7127", "7128", "7129")
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "try speed 0.3"),
                        List.of("0.30", "0.31" ,"0.32", "0.33", "0.34", "0.35", "0.36", "0.37", "0.38", "0.39")
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "try speed 0.3 a"),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", new String[]{"testing2", "2", "a"}),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", new String[]{"testing", "7", "a"}),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "rm -rf /* true a"),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "483 a"),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "rm -rf /* bla bla"),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "try asd"),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "try test 0.3"),
                        List.of("0.30", "0.31", "0.32", "0.33", "0.34", "0.35", "0.36", "0.37", "0.38", "0.39")
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "try test 0.3 test test test"),
                        List.of()
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "yes me"),
                        List.of("meow")
                )
        );

        assertTrue(
                CollectionHelpers.equalsList(
                        evaluateTabCompletion("test", "yes mE"),
                        List.of()
                )
        );

    }

    @Test
    void evilCommand() {
        assertThrowsExactly(RuntimeException.class, () -> testCommandManager.addCommand(new EvilCommandTest()), "Couldn't resolve net.apartium.cocoabeans.commands.EvilCommandTest#evil parser: thebotgame");
        assertThrowsExactly(RuntimeException.class, () -> testCommandManager.addCommand(new NullCommandTest()), "Static method net.apartium.cocoabeans.commands.NullCommandTest#meow is not supported");
        testCommandManager.addCommand(new AnotherEvilCommandTest());
        evaluate("evil-brother", "private");
        assertEquals(List.of(), sender.getMessages());
    }


    @Test
    void rangeArgumentRequirementFactoryTest() {
        RangeArgumentRequirementFactory rangeArgumentRequirementFactory = new RangeArgumentRequirementFactory();

        assertNull(rangeArgumentRequirementFactory.getArgumentRequirement(null));
        assertNull(rangeArgumentRequirementFactory.getArgumentRequirement("test"));

        RangeArgumentRequirementFactory.RangeImpl range = new RangeArgumentRequirementFactory.RangeImpl(0, 10, 1);
        assertFalse(range.meetsRequirement(sender, null, null));
    }


    void evaluate(String label, String args) {
        sender.getMessages().clear();
        testCommandManager.handle(sender, label, args.isEmpty() ? new String[0] : args.split("\\s+"));
    }

    List<String> evaluateTabCompletion(String label, String args) {
        return testCommandManager.handleTabComplete(sender, label, args.split("\\s+"));
    }

    List<String> evaluateTabCompletion(String label, String[] args) {
        return testCommandManager.handleTabComplete(sender, label, args);
    }

}
