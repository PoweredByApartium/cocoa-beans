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

import net.apartium.cocoabeans.commands.exception.ExceptionHandle;
import net.apartium.cocoabeans.commands.exception.RequirementException;
import net.apartium.cocoabeans.commands.parsers.*;
import net.apartium.cocoabeans.commands.requirements.UnmetRequirementResponse;
import net.apartium.cocoabeans.commands.requirements.argument.Range;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

@Command("test")
public class CommandForTest implements CommandNode {

    @SubCommand
    public void noArgs(Sender sender) {
        sender.sendMessage("no args :)");
    }

    @SubCommand("arg")
    public boolean testOneArg(Sender sender) {
        sender.sendMessage("arg has been accepted");
        return true;
    }

    @SubCommand("diff-arg")
    public void testDiffArg(Sender sender) {
        sender.sendMessage("testDiffArg(Sender sender) diff arg has been accepted");
    }


    @SubCommand("testing3 <int>")
    public void testingInteger(Sender sender, int num) {
        sender.sendMessage("testingInteger(Sender sender, int num) " + num);
    }

    @WithParser(value = DoubleParser.class, priority = -1)
    @SubCommand("testing3 <double>")
    public void testingDouble(Sender sender, Double num) {
        sender.sendMessage("testingDouble(Sender sender, Double num) " + num);
    }

    @SubCommand("testing3 <boolean>")
    public void testingBoolean(Sender sender, boolean b) {
        sender.sendMessage("testingBoolean(Sender sender, boolean b) " + b);
    }

    @SubCommand("one two")
    public void twoArg(Sender sender) {
        sender.sendMessage("twoArg(Sender sender) two args has been accepted");
    }

    @SubCommand("<int>")
    public boolean gotAnInt(Sender sender, int num) {
        sender.sendMessage("gotAnInt(Sender sender, int num) I got " + num);
        return true;
    }

    @SubCommand(value = "no", priority = 1)
    public boolean skipMe(Sender sender) {
        sender.sendMessage("skipMe(Sender sender) no");
        return false;
    }

    @WithParser(ExampleParser.class)
    @SubCommand("testing example <example>")
    public void testingExample(Sender sender, ExampleParser.PersonInfo personInfo) {
        sender.sendMessage("testingExample(Sender sender, ExampleParser.PersonInfo personInfo) name: " + personInfo.name() + ", age: " + personInfo.age());
    }

    @SubCommand("no")
    public void ok(Sender sender) {
        sender.sendMessage("ok(Sender sender) ok");
    }

    @SubCommand(value = "yes meow", ignoreCase = false)
    public void yesMeow(Sender sender) {
        sender.sendMessage("yesMeow(Sender sender) ok");
    }

    @WithParser(value = StringParser.class, priority = -1)
    @SubCommand("testing-arg <string>")
    public void testingFailedArg(Sender sender, @Range(to = 10) String s) {
        sender.sendMessage("testingFailedArg(Sender sender, @Range(to = 10) String s) how?");
    }

    @WallRequirement
    @SubCommand("no-one")
    public void noOne(Sender sender) {
        sender.sendMessage("noOne(Sender sender) witch!!!");
    }

    @SubCommand("rm -rf /* <boolean>")
    public void rmDashRfSpaceSlashSpaceAsterisk(Sender sender, boolean choice) {
        sender.sendMessage("rm_rf_slash_asterisk(Sender sender, boolean choice) " + (choice ? "Say GoodBye ah ah ah" : "You forgot sudo ),:"));
    }

    @IntRangeParser(to = 10)
    @SubCommand("testing <range>")
    public void testRange(Sender sender, int num) {
        sender.sendMessage("testRange(Sender sender, int num) You chosen " + num);
    }

    @SubCommand("testing-arg <double>")
    public void testRangeArg(Sender sender, @Range(to = 10) double num) {
        sender.sendMessage("testRangeArg(Sender sender, @Range(to = 10) double num) " + num);
    }

    @IntRangeParser(to = 10, step = 2)
    @SubCommand("testing2 <range>")
    public void test2Range(Sender sender, int num) {
        sender.sendMessage("test2Range(Sender sender, int num) You chosen " + num);
    }

    @SubCommand("testing3")
    public void testing3(TestSender sender) {
        sender.sendMessage("testing3(TestSender sender) cool");
    }

    @SubCommand("set speed <float>")
    public void setSpeed(Sender sender, float speed) {
        sender.sendMessage("setSpeed(Sender sender, float speed) speed has been set to " + speed);
    }

    @SubCommand("set speed <float> and flyspeed <float>")
    public void setSpeedAndFlySpeed(Sender sender, float walkingSpeed, float flyingSpeed) {
        sender.sendMessage("setSpeedAndFlySpeed(Sender sender, float walkingSpeed, float flyingSpeed) walk speed has been set to " + walkingSpeed + " while flying speed has been set to " + flyingSpeed);
    }

    @SubCommand("test <long>")
    public void testLong(Sender sender, long num) {
        sender.sendMessage("testLong(Sender sender, long num) got " + num);
    }

    @SubCommand("optional <?long>")
    public void testOptionalLong(Sender sender, OptionalLong optLong) {
        sender.sendMessage("testOptionalLong(Sender sender, Optional<Long> num) got " + (optLong.isPresent() ? optLong.getAsLong() : "null"));
    }

    @SubCommand("optional meow <?!long>")
    public void testOptionalLongMeow(Sender sender, Optional<Long> optLong) {
        sender.sendMessage("testOptionalLongMeow(Sender sender, Optional<Long> num) got " + (optLong.isPresent() ? optLong.get() : "null"));
    }

    @SubCommand("optional woof <!int>")
    public void testOptionalWoof(Sender sender, OptionalInt optInt) {
        if (optInt.isEmpty()) {
            sender.sendMessage("testOptionalWoof(Sender sender, OptionalInt optInt) got null");
            return;
        }

        sender.sendMessage("testOptionalWoof(Sender sender, OptionalInt optInt) got " + optInt.getAsInt());
    }

    @SubCommand("optional meow <!?long> wow")
    public void testOptionalLongMeow1(Sender sender, Optional<Long> optLong) {
        sender.sendMessage("testOptionalLongMeow1(Sender sender, Optional<Long> num) got " + (optLong.isPresent() ? optLong.get() : "null"));
    }

    @SubCommand("optional yoo <?string>")
    public void testOptionalStringMeow(Sender sender, Optional<String> optString) {
        sender.sendMessage("testOptionalStringMeow(Sender sender, Optional<String> optString) got " + (optString.orElse("null")));
    }

    @WithParser(value = StringParser.class, keyword = "config-key")
    @SubCommand("config get <config-key>")
    public void getConfigValue(Sender sender, String s) {
        sender.sendMessage("getConfigValue(Sender sender, String s) " + s + " = true");
    }

    @WithParser(DummyParser.class)
    @SubCommand("try <ignore> <double>")
    public void tryDouble(Sender sender, double num) {
        sender.sendMessage("tryDouble(Sender sender, double num) I ignore the second argument it wasn't important also your number is " + num);
    }

    @SubCommand("send <strings>")
    public void sendMessageToSender(Sender sender, String message) {
        sender.sendMessage("sendMessageToSender(Sender sender, String message) message have been sent: " + message);
    }

    @SubCommand("config set <string> <string>")
    public void setConfigValue(Sender sender, String key, String value) {
        sender.sendMessage("setConfigValue(Sender sender, String key, String value) " + key + " = " + value);
    }

    @Override
    public boolean fallbackHandle(Sender sender, String label, String[] args) {
        sender.sendMessage("fallbackHandle(Sender sender, String label, String[] args) You can't access that method... args: [" + String.join(", ", args) + "]");
        return true;
    }

    @ExceptionHandle(RequirementException.class)
    public boolean meow(RequirementException exception, Sender sender) {
        sender.sendMessage("You don't have permission to execute this command!");
        return true;
    }

}
