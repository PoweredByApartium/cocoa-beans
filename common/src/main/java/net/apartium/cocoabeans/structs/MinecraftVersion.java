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

/**
 * Represents version in the game.
 * Format: major.update.minor, ex 1.8.3
 * @param major major version, currently only 1
 * @param update, update version
 * @param minor minor version, if not explicitly specified defaults to 0
 */
public record MinecraftVersion(
        int major,
        int update,
        int minor
) {

    public static final MinecraftVersion UNKNOWN = new MinecraftVersion(0, 0, 0);

    /**
     * Helper method to check if current instance equals to given major and update version
     * @param major major
     * @param update update
     * @return true if eq, else false
     */
    public boolean updateEq(int major, int update) {
        return this.major == major && this.update == update;
    }

    /**
     * Detects whether current instance represents an unknown version
     * @return true if unknown, else false
     */
    public boolean isUnknown() {
        return major == 0 && update == 0 && minor == 0;
    }

}
