package net.apartium.cocoabeans.scoreboard.team;

import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a chat format for player names in newer versions
 */
@ApiStatus.AvailableSince("0.0.41")
public enum ChatFormatting {
    BLACK('0'),
    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_AQUA('3'),
    DARK_RED('4'),
    DARK_PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    AQUA('b'),
    RED('c'),
    LIGHT_PURPLE('d'),
    YELLOW('e'),
    WHITE('f'),
    OBFUSCATED('k'),
    BOLD('l'),
    STRIKETHROUGH('m'),
    UNDERLINE('n'),
    ITALIC('o'),
    RESET('r');

    private final String toString;

    ChatFormatting(char code) {
        this.toString = "§" + code;
    }

    @Override
    public String toString() {
        return toString;
    }
}
