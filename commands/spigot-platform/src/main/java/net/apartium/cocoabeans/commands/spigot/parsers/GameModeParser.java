package net.apartium.cocoabeans.commands.spigot.parsers;

import net.apartium.cocoabeans.commands.parsers.MapBasedParser;
import org.bukkit.GameMode;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * GameMode parser with lazy mapping
 */
@ApiStatus.AvailableSince("0.0.36")
public class GameModeParser extends MapBasedParser<GameMode> {

    public static final String DEFAULT_KEYWORD = "gamemode";

    private final Map<String, GameMode> gameModeMap;

    /**
     * Create new GameModeParser
     * @param keyword parser keyword
     * @param priority parser priority
     */
    public GameModeParser(String keyword, int priority) {
        super(keyword, GameMode.class, priority, true, true);

        Map<String, GameMode> gameModeMap = new HashMap<>();

        for (GameMode gameMode : GameMode.values()) {
            gameModeMap.put(gameMode.name().toLowerCase(), gameMode);
            gameModeMap.put(gameMode.getValue() + "", gameMode);
        }

        this.gameModeMap = Map.copyOf(gameModeMap);
    }

    /**
     * Create new GameModeParser
     * @param priority parser priority
     */
    public GameModeParser(int priority) {
        this(DEFAULT_KEYWORD, priority);
    }

    /**
     * Get map of keyword to value
     * @return map
     */
    @Override
    public Map<String, GameMode> getMap() {
        return gameModeMap;
    }

}
