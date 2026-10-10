package net.apartium.cocoabeans.commands.option;

import net.apartium.cocoabeans.commands.parsers.MapBasedParser;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

enum Priority {

    LOWEST,
    LOW,
    MEDIUM,
    HIGH,
    HIGHEST;

    static class Parser extends MapBasedParser<Priority> {

        public static final String DEFAULT_KEYWORD = "priority";

        private final Map<String, Priority> map = Arrays.stream(Priority.values())
                .collect(Collectors.toMap(
                        p -> p.name().toLowerCase(),
                        Function.identity()
                ));

        public Parser(String keyword, int priority, boolean ignoreCase, boolean lax) {
            super(keyword, Priority.class, priority, ignoreCase, lax);
        }

        public Parser(String keyword, int priority) {
            this(keyword, priority, true, true);
        }

        public Parser(int priority) {
            this(DEFAULT_KEYWORD, priority);
        }

        public Parser() {
            this(DEFAULT_KEYWORD, 0);
        }

        @Override
        public Map<String, Priority> getMap() {
            return map;
        }
    }

}
