/*
 * Copyright (c) 2026, Lior Slakman (me@voigon.dev), ALL RIGHTS RESERVED
 * Do not use, copy, modify, and/or distribute this software without explicit permission from the
 * rights holder. Reselling this product is not allowed. Transfer of the source code to any person
 * or organization not explicitly approved by the rights holder via a license agreement is hereby forbidden.
 */

package net.apartium.cocoabeans.structs;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * @hidden
 */
@ApiStatus.Internal
/* package-private */ class MinecraftVersionHolder {

    // allows to defer initialization of this field in case it's not in use
    /* package-private */ static final List<MinecraftVersion>[] VERSIONS_BY_PROTOCOL = build();

    @SuppressWarnings("unchecked")
    private static List<MinecraftVersion>[] build() {
        List<MinecraftVersion> knownVersions = MinecraftVersion.KNOWN_VERSIONS;
        List<MinecraftVersion>[] array = new List[knownVersions.get(knownVersions.size() - 1).protocol() + 1];

        for (MinecraftVersion version : knownVersions) {
            if (array[version.protocol()] == null)
                array[version.protocol()] = new ArrayList<>();

            array[version.protocol()].add(version);
        }

        for (int i = 0; i < array.length; i++) {
            if (array[i] != null)
                array[i] = List.copyOf(array[i]);
        }

        return array;
    }

    private MinecraftVersionHolder() {}

}
