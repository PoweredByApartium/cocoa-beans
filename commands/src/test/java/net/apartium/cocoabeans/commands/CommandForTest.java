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

import net.apartium.cocoabeans.commands.parsers.DummyParser;
import net.apartium.cocoabeans.commands.parsers.IntRangeParser;
import net.apartium.cocoabeans.commands.parsers.WithParser;

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

    @SubCommand("no")
    public void ok(Sender sender) {
        sender.sendMessage("ok(Sender sender) ok");
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

    @IntRangeParser(to = 10, step = 2)
    @SubCommand("testing2 <range>")
    public void test2Range(Sender sender, int num) {
        sender.sendMessage("test2Range(Sender sender, int num) You chosen " + num);
    }

    @SubCommand("set speed <float>")
    public void setSpeed(Sender sender, float speed) {
        sender.sendMessage("setSpeed(Sender sender, float speed) speed has been set to " + speed);
    }

    @SubCommand("config get <string>")
    public void getConfigValue(Sender sender, String s) {
        sender.sendMessage("getConfigValue(Sender sender, String s) " + s + " = true");
    }

    @WithParser(DummyParser.class)
    @SubCommand("try <ignore> <double>")
    public void tryDouble(Sender sender, double num) {
        sender.sendMessage("tryDouble(Sender sender, double num) I ignore the second argument it wasn't important also your number is " + num);
    }

    @Override
    public boolean fallbackHandle(Sender sender, String label, String[] args) {
        sender.sendMessage("fallbackHandle(Sender sender, String label, String[] args) You can't access that method... args: [" + String.join(", ", args) + "]");
        return true;
    }
}
