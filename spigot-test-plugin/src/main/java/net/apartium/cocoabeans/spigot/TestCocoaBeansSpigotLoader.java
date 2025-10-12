/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.spigot;

import net.apartium.cocoabeans.commands.CommandManager;
import net.apartium.cocoabeans.commands.spigot.SpigotCommandManager;
import net.apartium.cocoabeans.spigot.board.BoardManager;
import net.apartium.cocoabeans.spigot.board.ScoreboardNumericManager;
import net.apartium.cocoabeans.spigot.commands.CocoaBoardCommand;
import net.apartium.cocoabeans.spigot.commands.SchematicCommand;
import net.apartium.cocoabeans.spigot.commands.TabListCommand;
import net.apartium.cocoabeans.spigot.lazies.ListenerAutoRegistration;
import net.apartium.cocoabeans.spigot.tab.TabManager;
import net.apartium.cocoabeans.spigot.team.TeamManager;
import net.apartium.cocoabeans.state.spigot.SpigotProvidedState;
import org.bukkit.plugin.java.JavaPlugin;

public class TestCocoaBeansSpigotLoader extends JavaPlugin {

    private SpigotProvidedState spigotProvidedState;
    private CommandManager commandManager;
    private BoardManager boardManager;
    private TeamManager teamManager;
    private TabManager tabManager;
    private ScoreboardNumericManager scoreboardNumericManager;

    @Override
    public void onEnable() {
        spigotProvidedState = new SpigotProvidedState(this);
        spigotProvidedState.startCprTask();

        boardManager = new BoardManager(spigotProvidedState);
        boardManager.initialize(this);

        teamManager = new TeamManager();
        teamManager.initialize(this);

        scoreboardNumericManager = new ScoreboardNumericManager(spigotProvidedState);
        scoreboardNumericManager.initialize(this);

        tabManager = new TabManager();
        tabManager.initialize(this);

        commandManager = new SpigotCommandManager(this);
        commandManager.registerArgumentTypeHandler(SpigotCommandManager.COMMON_PARSERS);
        commandManager.registerArgumentTypeHandler(SpigotCommandManager.SPIGOT_PARSERS);

        commandManager.addCommand(new CocoaBoardCommand(boardManager, teamManager, scoreboardNumericManager));
        commandManager.addCommand(new TabListCommand(tabManager));
        commandManager.addCommand(new SchematicCommand(this));

        ListenerAutoRegistration listenerAutoRegistration = new ListenerAutoRegistration(this, false);
        listenerAutoRegistration.addInjectableObject(boardManager);
        listenerAutoRegistration.addInjectableObject(teamManager);
        listenerAutoRegistration.addInjectableObject(scoreboardNumericManager);

        String packageName = getClass().getPackage().getName();
        listenerAutoRegistration.register(packageName + ".listeners", true);
    }

    @Override
    public void onDisable() {
        boardManager.disable();
        spigotProvidedState.remove();
        teamManager.disable();
        tabManager.disable();
    }

}
