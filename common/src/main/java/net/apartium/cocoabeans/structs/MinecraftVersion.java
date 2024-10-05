/*
 * Copyright 2023 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.structs;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Objects;

/**
 * Represents version in the game.
 * Format: major.update.minor, ex 1.8.3
 * @param major major version, currently only 1
 * @param update, update version
 * @param minor minor version, if not explicitly specified defaults to 0
 * @param protocol protocol version
 * @see <a href="https://minecraft.wiki/w/Protocol_version">Protocol version</a>
 */
public record MinecraftVersion(
        int major,
        int update,
        int minor,
        @ApiStatus.AvailableSince("0.0.36")
        int protocol
) {

    public static final MinecraftVersion UNKNOWN = new MinecraftVersion(0, 0, 0, 0);

    public static final MinecraftVersion V1_8 = new MinecraftVersion(1, 8, 0, 47);
    public static final MinecraftVersion V1_8_1 = new MinecraftVersion(1, 8, 1, 47);
    public static final MinecraftVersion V1_8_2 = new MinecraftVersion(1, 8, 2, 47);
    public static final MinecraftVersion V1_8_3 = new MinecraftVersion(1, 8, 3, 47);
    public static final MinecraftVersion V1_8_4 = new MinecraftVersion(1, 8, 4, 47);
    public static final MinecraftVersion V1_8_5 = new MinecraftVersion(1, 8, 5, 47);
    public static final MinecraftVersion V1_8_6 = new MinecraftVersion(1, 8, 6, 47);
    public static final MinecraftVersion V1_8_7 = new MinecraftVersion(1, 8, 7, 47);
    public static final MinecraftVersion V1_8_8 = new MinecraftVersion(1, 8, 8, 47);
    public static final MinecraftVersion V1_8_9 = new MinecraftVersion(1, 8, 9, 47);

    public static final MinecraftVersion V1_9 = new MinecraftVersion(1, 9, 0, 107);
    public static final MinecraftVersion V1_9_1 = new MinecraftVersion(1, 9, 1, 108);
    public static final MinecraftVersion V1_9_2 = new MinecraftVersion(1, 9, 2, 109);
    public static final MinecraftVersion V1_9_3 = new MinecraftVersion(1, 9, 3, 110);
    public static final MinecraftVersion V1_9_4 = new MinecraftVersion(1, 9, 4, 110);

    public static final MinecraftVersion V1_10 = new MinecraftVersion(1, 10, 0, 210);
    public static final MinecraftVersion V1_10_1 = new MinecraftVersion(1, 10, 1, 210);
    public static final MinecraftVersion V1_10_2 = new MinecraftVersion(1, 10, 2, 210);

    public static final MinecraftVersion V1_11 = new MinecraftVersion(1, 11, 0, 315);
    public static final MinecraftVersion V1_11_1 = new MinecraftVersion(1, 11, 1, 316);
    public static final MinecraftVersion V1_11_2 = new MinecraftVersion(1, 11, 2, 316);

    public static final MinecraftVersion V1_12 = new MinecraftVersion(1, 12, 0, 335);
    public static final MinecraftVersion V1_12_1 = new MinecraftVersion(1, 12, 1, 338);
    public static final MinecraftVersion V1_12_2 = new MinecraftVersion(1, 12, 2, 340);

    public static final MinecraftVersion V1_13 = new MinecraftVersion(1, 13, 0, 393);
    public static final MinecraftVersion V1_13_1 = new MinecraftVersion(1, 13, 1, 401);
    public static final MinecraftVersion V1_13_2 = new MinecraftVersion(1, 13, 2, 404);

    public static final MinecraftVersion V1_14 = new MinecraftVersion(1, 14, 0, 477);
    public static final MinecraftVersion V1_14_1 = new MinecraftVersion(1, 14, 1, 480);
    public static final MinecraftVersion V1_14_2 = new MinecraftVersion(1, 14, 2, 485);
    public static final MinecraftVersion V1_14_3 = new MinecraftVersion(1, 14, 3, 490);
    public static final MinecraftVersion V1_14_4 = new MinecraftVersion(1, 14, 4, 498);

    public static final MinecraftVersion V1_15 = new MinecraftVersion(1, 15, 0, 573);
    public static final MinecraftVersion V1_15_1 = new MinecraftVersion(1, 15, 1, 575);
    public static final MinecraftVersion V1_15_2 = new MinecraftVersion(1, 15, 2, 578);

    public static final MinecraftVersion V1_16 = new MinecraftVersion(1, 16, 0, 735);
    public static final MinecraftVersion V1_16_1 = new MinecraftVersion(1, 16, 1, 736);
    public static final MinecraftVersion V1_16_2 = new MinecraftVersion(1, 16, 2, 751);
    public static final MinecraftVersion V1_16_3 = new MinecraftVersion(1, 16, 3, 753);
    public static final MinecraftVersion V1_16_4 = new MinecraftVersion(1, 16, 4, 754);
    public static final MinecraftVersion V1_16_5 = new MinecraftVersion(1, 16, 5, 754);

    public static final MinecraftVersion V1_17 = new MinecraftVersion(1, 17, 0, 755);
    public static final MinecraftVersion V1_17_1 = new MinecraftVersion(1, 17, 1, 756);

    public static final MinecraftVersion V1_18 = new MinecraftVersion(1, 18, 0, 757);
    public static final MinecraftVersion V1_18_1 = new MinecraftVersion(1, 18, 1, 757);
    public static final MinecraftVersion V1_18_2 = new MinecraftVersion(1, 18, 2, 758);

    public static final MinecraftVersion V1_19 = new MinecraftVersion(1, 19, 0, 759);
    public static final MinecraftVersion V1_19_1 = new MinecraftVersion(1, 19, 1, 760);
    public static final MinecraftVersion V1_19_2 = new MinecraftVersion(1, 19, 2, 760);
    public static final MinecraftVersion V1_19_3 = new MinecraftVersion(1, 19, 3, 761);
    public static final MinecraftVersion V1_19_4 = new MinecraftVersion(1, 19, 4, 762);

    public static final MinecraftVersion V1_20 = new MinecraftVersion(1, 20, 0, 763);
    public static final MinecraftVersion V1_20_1 = new MinecraftVersion(1, 20, 1, 763);
    public static final MinecraftVersion V1_20_2 = new MinecraftVersion(1, 20, 2, 764);
    public static final MinecraftVersion V1_20_3 = new MinecraftVersion(1, 20, 3, 765);
    public static final MinecraftVersion V1_20_4 = new MinecraftVersion(1, 20, 4, 765);
    public static final MinecraftVersion V1_20_5 = new MinecraftVersion(1, 20, 5, 766);
    public static final MinecraftVersion V1_20_6 = new MinecraftVersion(1, 20, 6, 766);

    public static final MinecraftVersion V1_21 = new MinecraftVersion(1, 21, 0, 767);
    public static final MinecraftVersion V1_21_1 = new MinecraftVersion(1, 21, 1, 767);


    public static final List<MinecraftVersion> KNOWN_VERSIONS = List.of(
            // 1.8 - 1.8.9
            V1_8,
            V1_8_1,
            V1_8_2,
            V1_8_3,
            V1_8_4,
            V1_8_5,
            V1_8_6,
            V1_8_7,
            V1_8_8,
            V1_8_9,

            // 1.9 - 1.9.4
            V1_9,
            V1_9_1,
            V1_9_2,
            V1_9_3,
            V1_9_4,

            // 1.10 - 1.10.2
            V1_10,
            V1_10_1,
            V1_10_2,

            // 1.11 - 1.11.2
            V1_11,
            V1_11_1,
            V1_11_2,

            // 1.12 - 1.12.2
            V1_12,
            V1_12_1,
            V1_12_2,

            // 1.13 - 1.13.2
            V1_13,
            V1_13_1,
            V1_13_2,

            // 1.14 - 1.14.4
            V1_14,
            V1_14_1,
            V1_14_2,
            V1_14_3,
            V1_14_4,

            // 1.15 - 1.15.2
            V1_15,
            V1_15_1,
            V1_15_2,

            // 1.16 - 1.16.5
            V1_16,
            V1_16_1,
            V1_16_2,
            V1_16_3,
            V1_16_4,
            V1_16_5,

            // 1.17 - 1.17.1
            V1_17,
            V1_17_1,

            // 1.18 - 1.18.2
            V1_18,
            V1_18_1,
            V1_18_2,

            // 1.19 - 1.19.4
            V1_19,
            V1_19_1,
            V1_19_2,
            V1_19_3,
            V1_19_4,

            // 1.20 - 1.20.6
            V1_20,
            V1_20_1,
            V1_20_2,
            V1_20_3,
            V1_20_4,
            V1_20_5,
            V1_20_6,

            // 1.21 - 1.21.1
            V1_21,
            V1_21_1
    );

    /**
     *
     * @param major major version
     * @param update update version
     * @param minor minor version
     * @return cached minecraft version or create a new one without any protocol version
     */
    @ApiStatus.AvailableSince("0.0.36")
    public static MinecraftVersion getVersion(int major, int update, int minor) {
        return getVersion(major, update, minor, -1);
    }

    /**
     * Get Minecraft Version by lookup table or create new one
     * @param major major version
     * @param update update version
     * @param minor minor version
     * @return cached minecraft version or create a new one with desired protocol version
     */
    @ApiStatus.AvailableSince("0.0.36")
    public static MinecraftVersion getVersion(int major, int update, int minor, int protocol) {
        for (MinecraftVersion version : KNOWN_VERSIONS) {
            if (version.major == major && version.update == update && version.minor == minor) {
                return version;
            }
        }
        return new MinecraftVersion(major, update, minor, protocol);
    }

    /**
     * Helper method to check if current instance is higher than or equal to given version
     * @param other other minecraft version
     * @return true if higher than or equal, otherwise false
     */
    @ApiStatus.AvailableSince("0.0.36")
    public boolean isHigherThanOrEqual(MinecraftVersion other) {
        if (major != other.major)
            return major > other.major;

        if (update != other.update)
            return update > other.update;

        return minor >= other.minor;
    }

    /**
     * Helper method to check if current instance is higher than given version
     * @param other other minecraft version
     * @return true if higher than or equal, otherwise false
     */
    @ApiStatus.AvailableSince("0.0.36")
    public boolean isHigherThan(MinecraftVersion other) {
        if (major != other.major)
            return major > other.major;

        if (update != other.update)
            return update > other.update;

        return minor > other.minor;
    }

    /**
     * Helper method to check if current instance is lower than or equal to given version
     * @param other other minecraft version
     * @return true if lower than or equal, otherwise false
     */
    @ApiStatus.AvailableSince("0.0.36")
    public boolean isLowerThanOrEqual(MinecraftVersion other) {
        return other.isHigherThanOrEqual(this);
    }

    /**
     * Helper method to check if current instance is lower than given version
     * @param other other minecraft version
     * @return true if lower than, otherwise false
     */
    @ApiStatus.AvailableSince("0.0.36")
    public boolean isLowerThan(MinecraftVersion other) {
        return other.isHigherThan(this);
    }

    /**
     * Check if two versions are equal
     * @param other other minecraft version
     * @return true if equal, otherwise false
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        MinecraftVersion that = (MinecraftVersion) other;
        return major == that.major
                && update == that.update
                && minor == that.minor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, update, minor);
    }

    /**
     * Detects whether current instance represents an unknown version
     * @return true if unknown, else false
     */
    public boolean isUnknown() {
        return major == 0 && update == 0 && minor == 0;
    }

    /**
     * Detects whether current instance represents a known version
     * @return true if known, else false
     */
    @ApiStatus.AvailableSince("0.0.36")
    public boolean isProtocolKnown() {
        return protocol > 0;
    }

}
