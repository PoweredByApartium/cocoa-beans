package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.spigot.ServerUtils;
import net.apartium.cocoabeans.spigot.VersionedImplInstantiator;
import net.apartium.cocoabeans.structs.MinecraftVersion;

/**
 * @hidden
 */
/* package-private */ class CocoaSchematicInstantiator {

    private CocoaSchematicInstantiator() {}

    public static SpigotSchematicPlacer createPlacer() {
        MinecraftVersion minecraftVersion = ServerUtils.getVersion();
        return switch (minecraftVersion.update()) {
            case 8 -> constructPlacer("schematic.SpigotSchematicPlacer_1_8_R1");
            default -> constructPlacer("schematic.SpigotSchematicPlacer_1_20_R1");
        };
    }

    private static SpigotSchematicPlacer constructPlacer(String clazz) {
        return VersionedImplInstantiator.construct(clazz, SpigotSchematicPlacer.class);
    }

}
