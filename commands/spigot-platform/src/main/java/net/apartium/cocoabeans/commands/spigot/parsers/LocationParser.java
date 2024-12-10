/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.commands.spigot.parsers;

import net.apartium.cocoabeans.commands.SimpleArgumentMapper;
import net.apartium.cocoabeans.commands.lexer.SimpleCommandLexer;
import net.apartium.cocoabeans.commands.parsers.*;
import net.apartium.cocoabeans.commands.spigot.SenderType;
import net.apartium.cocoabeans.commands.spigot.SpigotArgumentMapper;
import net.apartium.cocoabeans.commands.spigot.requirements.SenderLimit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

public class LocationParser extends WrappedArgumentParser<Location> {

    public static final String DEFAULT_KEYWORD = "location";

    private static LocationParserImpl getImpl() {
        if (impl == null)
            impl = new LocationParserImpl(DEFAULT_KEYWORD, 0);

        return impl;
    }

    private static LocationParserImpl impl = getImpl();

    /**
     * Creates a new LocationParser
     * @param priority priority
     * @param keyword keyword to be used
     */
    public LocationParser(int priority, String keyword) {
        super(impl, priority, keyword);
    }

    /**
     * Creates a new LocationParser
     * @param priority priority
     */
    public LocationParser(int priority) {
        this(priority, DEFAULT_KEYWORD);
    }

    @WithParser(DoubleParser.class)
    @WithParser(FloatParser.class)
    @WithParser(WorldParser.class)
    @ApiStatus.AvailableSince("0.0.38")
    private static class LocationParserImpl extends CompoundParser<Location> {

        public LocationParserImpl(String keyword, int priority) {
            super(keyword, Location.class, priority, new SpigotArgumentMapper(), new SimpleCommandLexer());
        }

        @ParserVariant("<world> <double> <double> <double>")
        public Location parseWorldWithXyz(World world, double x, double y, double z) {
            return new Location(world, x, y, z);
        }

        @ParserVariant("<world> <double> <double> <double> <float> <float>")
        public Location parseWorldWithXyzYawPitch(World world, double x, double y, double z, float yaw, float pitch) {
            return new Location(world, x, y, z, yaw, pitch);
        }

        @SenderLimit(SenderType.PLAYER)
        @ParserVariant("<double> <double> <double>")
        public Location parseWithXyz(Player sender, double x, double y, double z) {
            return new Location(
                    sender.getWorld(),
                    x,
                    y,
                    z
            );
        }

        @SenderLimit(SenderType.PLAYER)
        @ParserVariant("<double> <double> <double> <float> <float>")
        public Location parseWithXyzYawPitch(Player sender, double x, double y, double z, float yaw, float pitch) {
            return new Location(
                    sender.getWorld(),
                    x,
                    y,
                    z,
                    yaw,
                    pitch
            );
        }
    }


}
