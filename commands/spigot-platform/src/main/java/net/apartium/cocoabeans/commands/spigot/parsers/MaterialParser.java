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
import java.util.stream.Collectors;

public class MaterialParser extends ArgumentParser<Material> {

    public static final String DEFAULT_KEYWORD = "material";

    /**
     * Creates a new MaterialParser
     * @param priority parser priority of which should be higher than others or lower
     * @param keyword parser keyword
     */
    @ApiStatus.AvailableSince("0.0.36")
    public MaterialParser(int priority, String keyword) {
        super(keyword, Material.class, priority);
    }

    /**
     * Creates a new MaterialParser
     * @param priority parser priority of which should be higher than others or lower
     */
    public MaterialParser(int priority) {
        this(priority, DEFAULT_KEYWORD);
    }

    @Override
    public Optional<ParseResult<Material>> parse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int startIndex = processingContext.index();

        Material material = Material.getMaterial(args.get(startIndex));
        if (material == null) return Optional.empty();
        return Optional.of(new ParseResult<>(
                material,
                startIndex + 1
        ));
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int startIndex = processingContext.index();

        if (Material.getMaterial(args.get(startIndex)) == null) return OptionalInt.empty();
        return OptionalInt.of(startIndex + 1);
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int startIndex = processingContext.index();

        Set<String> result = Arrays.stream(Material.values())
                .map((type) -> {
                    if (type.isLegacy())
                        return type.name();

                    return type.getKey().asString();
                })
                .collect(Collectors.toSet());

        return Optional.of(new TabCompletionResult(
                Arrays.stream(Material.values())
                        .map(Material::name)
                        .filter(s -> s.toLowerCase().startsWith(args.get(startIndex).toLowerCase()))
                        .collect(Collectors.toSet()),
                startIndex + 1
        ));
    }
}
