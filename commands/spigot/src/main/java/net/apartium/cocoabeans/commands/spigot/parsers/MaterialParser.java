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

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import org.bukkit.Material;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class MaterialParser extends ArgumentParser<Material> {

    public static final String DEFAULT_KEYWORD = "material";

    /**
     * Creates a new MaterialParser
     *
     * @param priority parser priority of which should be higher than others or lower
     * @param keyword  parser keyword
     */
    @ApiStatus.AvailableSince("0.0.36")
    public MaterialParser(int priority, String keyword) {
        super(keyword, Material.class, priority);
    }

    /**
     * Creates a new MaterialParser
     *
     * @param priority parser priority of which should be higher than others or lower
     */
    public MaterialParser(int priority) {
        this(priority, DEFAULT_KEYWORD);
    }

    @Override
    public Optional<ParseResult<Material>> parse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int startIndex = processingContext.index();

        Optional<Material> materialOpt = findMaterial(args.get(startIndex));
        return materialOpt.map(material -> new ParseResult<>(
                material,
                startIndex + 1
        ));

    }

    private Optional<Material> findMaterial(String name) {
        if (!name.contains("_")) {
            return Arrays.stream(Material.values())
                    .filter(material -> material.name().replace("_", "").equalsIgnoreCase(name))
                    .findFirst();
        }

        return Optional.ofNullable(Material.getMaterial(name.toUpperCase()));
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        if (args.isEmpty())
            return OptionalInt.empty();

        int startIndex = processingContext.index();

        if (findMaterial(args.get(startIndex)).isEmpty())
            return OptionalInt.empty();

        return OptionalInt.of(startIndex + 1);
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int startIndex = processingContext.index();
        String arg = args.get(startIndex);


        Set<String> result = new HashSet<>();

        for (Material material : Material.values()) {
            String name = material.name().toLowerCase();
            if (!arg.contains("_"))
                name = name.replace("_", "");

            if (name.startsWith(arg.toLowerCase()))
                result.add(name);
        }

        return Optional.of(new TabCompletionResult(Collections.unmodifiableSet(result), startIndex + 1));
    }
}
