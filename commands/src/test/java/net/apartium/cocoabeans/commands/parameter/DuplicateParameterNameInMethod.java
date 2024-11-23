package net.apartium.cocoabeans.commands.parameter;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.Param;
import net.apartium.cocoabeans.commands.SubCommand;

@Command("evil")
public class DuplicateParameterNameInMethod implements CommandNode {

    @SubCommand("meow")
    public void test(@Param("name") String name, @Param("name") String otherName) {
        // Method shouldn't be able to serialize because of the duplicate parameter name
        throw new UnsupportedOperationException("Not implemented");
    }


}
