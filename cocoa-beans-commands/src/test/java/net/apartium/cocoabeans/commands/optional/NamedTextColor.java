package net.apartium.cocoabeans.commands.optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class NamedTextColor implements TextColor {
    private static final int BLACK_VALUE = 0x000000;
    private static final int DARK_BLUE_VALUE = 0x0000aa;
    private static final int DARK_GREEN_VALUE = 0x00aa00;
    private static final int DARK_AQUA_VALUE = 0x00aaaa;
    private static final int DARK_RED_VALUE = 0xaa0000;
    private static final int DARK_PURPLE_VALUE = 0xaa00aa;
    private static final int GOLD_VALUE = 0xffaa00;
    private static final int GRAY_VALUE = 0xaaaaaa;
    private static final int DARK_GRAY_VALUE = 0x555555;
    private static final int BLUE_VALUE = 0x5555ff;
    private static final int GREEN_VALUE = 0x55ff55;
    private static final int AQUA_VALUE = 0x55ffff;
    private static final int RED_VALUE = 0xff5555;
    private static final int LIGHT_PURPLE_VALUE = 0xff55ff;
    private static final int YELLOW_VALUE = 0xffff55;
    private static final int WHITE_VALUE = 0xffffff;

    public static final NamedTextColor BLACK = new NamedTextColor("black", BLACK_VALUE);
    public static final NamedTextColor DARK_BLUE = new NamedTextColor("dark_blue", DARK_BLUE_VALUE);
    public static final NamedTextColor DARK_GREEN = new NamedTextColor("dark_green", DARK_GREEN_VALUE);
    public static final NamedTextColor DARK_AQUA = new NamedTextColor("dark_aqua", DARK_AQUA_VALUE);
    public static final NamedTextColor DARK_RED = new NamedTextColor("dark_red", DARK_RED_VALUE);
    public static final NamedTextColor DARK_PURPLE = new NamedTextColor("dark_purple", DARK_PURPLE_VALUE);
    public static final NamedTextColor GOLD = new NamedTextColor("gold", GOLD_VALUE);
    public static final NamedTextColor GRAY = new NamedTextColor("gray", GRAY_VALUE);
    public static final NamedTextColor DARK_GRAY = new NamedTextColor("dark_gray", DARK_GRAY_VALUE);
    public static final NamedTextColor BLUE = new NamedTextColor("blue", BLUE_VALUE);
    public static final NamedTextColor GREEN = new NamedTextColor("green", GREEN_VALUE);
    public static final NamedTextColor AQUA = new NamedTextColor("aqua", AQUA_VALUE);
    public static final NamedTextColor RED = new NamedTextColor("red", RED_VALUE);
    public static final NamedTextColor LIGHT_PURPLE = new NamedTextColor("light_purple", LIGHT_PURPLE_VALUE);
    public static final NamedTextColor YELLOW = new NamedTextColor("yellow", YELLOW_VALUE);
    public static final NamedTextColor WHITE = new NamedTextColor("white", WHITE_VALUE);

    private static final List<NamedTextColor> VALUES = Collections.unmodifiableList(Arrays.asList(BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE));



    public static @Nullable NamedTextColor namedColor(final int value) {
        return switch (value) {
            case BLACK_VALUE -> BLACK;
            case DARK_BLUE_VALUE -> DARK_BLUE;
            case DARK_GREEN_VALUE -> DARK_GREEN;
            case DARK_AQUA_VALUE -> DARK_AQUA;
            case DARK_RED_VALUE -> DARK_RED;
            case DARK_PURPLE_VALUE -> DARK_PURPLE;
            case GOLD_VALUE -> GOLD;
            case GRAY_VALUE -> GRAY;
            case DARK_GRAY_VALUE -> DARK_GRAY;
            case BLUE_VALUE -> BLUE;
            case GREEN_VALUE -> GREEN;
            case AQUA_VALUE -> AQUA;
            case RED_VALUE -> RED;
            case LIGHT_PURPLE_VALUE -> LIGHT_PURPLE;
            case YELLOW_VALUE -> YELLOW;
            case WHITE_VALUE -> WHITE;
            default -> null;
        };
    }

    private final String name;
    private final int value;

    public NamedTextColor(String name, int value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public int value() {
        return this.value;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(@NotNull TextColor textColor) {
        return Integer.compare(this.value, textColor.value());
    }

    @Override
    public String toString() {
        return name;
    }
}