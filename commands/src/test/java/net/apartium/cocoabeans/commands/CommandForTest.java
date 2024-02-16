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

}
