package net.apartium.cocoabeans.commands.parser;

import net.apartium.cocoabeans.commands.parsers.MapBasedParser;

import java.util.*;

public class GameModeParser extends MapBasedParser<GameMode> {

    private final Map<String, GameMode> gameModeMap = new HashMap<>();

    public GameModeParser(int priority) {
        super("gamemode", GameMode.class, priority, true, true);

        for (GameMode gameMode : GameMode.values()) {
            gameModeMap.put(gameMode.name().toLowerCase(), gameMode);
            gameModeMap.put(gameMode.getValue() + "", gameMode);
        }
    }

    @Override
    public Map<String, GameMode> getMap() {
        return gameModeMap;
    }

}
