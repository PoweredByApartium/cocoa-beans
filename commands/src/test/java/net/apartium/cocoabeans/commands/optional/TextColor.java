package net.apartium.cocoabeans.commands.optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public interface TextColor extends Comparable<TextColor> {

    static TextColor color(final int value) {
        final int truncatedValue = value & 0xffffff;
        final NamedTextColor named = NamedTextColor.namedColor(truncatedValue);

        return named != null ? named : new TextColor() {
            @Override
            public int value() {
                return truncatedValue;
            }

            @Override
            public int compareTo(@NotNull TextColor textColor) {
                return Integer.compare(value(), textColor.value());
            }
        };
    }

    int value();


    default @NotNull String asHexString() {
        return String.format("#%06x", this.value());
    }


    default @Range(from = 0x0, to = 0xff) int red() {
        return (this.value() >> 16) & 0xff;
    }


    default @Range(from = 0x0, to = 0xff) int green() {
        return (this.value() >> 8) & 0xff;
    }

    default @Range(from = 0x0, to = 0xff) int blue() {
        return this.value() & 0xff;
    }

}
