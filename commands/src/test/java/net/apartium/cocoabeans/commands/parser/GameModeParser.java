package net.apartium.cocoabeans.commands.parser;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.parsers.MapBasedParser;

import java.util.*;
import java.util.stream.Collectors;

public class GameModeParser extends MapBasedParser<GameMode> {

    private final Map<String, GameMode> gameModeMap = new HashMap<>();
    private final Set<String> tabCompletions = new HashSet<>();

    public GameModeParser(int priority) {
        super("gamemode", GameMode.class, priority, true);

        Set<String> dupes = new HashSet<>();
        for (GameMode gameMode : GameMode.values()) {
            tabCompletions.add(gameMode.name().toLowerCase());
            tabCompletions.add(gameMode.getValue() + "");


            for (int i = 1; i <= gameMode.name().length(); i++) {
                String gameModeName = gameMode.name().toLowerCase().substring(0, i);

                if (dupes.contains(gameModeName))
                    continue;

                if (gameModeMap.containsKey(gameModeName)) {
                    dupes.add(gameModeName);
                    gameModeMap.remove(gameModeName);
                    continue;
                }

                gameModeMap.put(gameModeName, gameMode);
            }

            gameModeMap.put(gameMode.getValue() + "", gameMode);
        }
    }

    @Override
    public Map<String, GameMode> getMap() {
        return gameModeMap;
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int index = processingContext.index();

        if (args.isEmpty() || args.get(index).isEmpty())
            return Optional.of(new TabCompletionResult(tabCompletions, index + 1));

        Set<String> gameModes = tabCompletions.stream().filter(s -> s.toLowerCase().startsWith(args.get(index).toLowerCase())).collect(Collectors.toSet());

        if (gameModes.isEmpty())
            return Optional.empty();

        return Optional.of(new TabCompletionResult(gameModes, index + 1));
    }

}
