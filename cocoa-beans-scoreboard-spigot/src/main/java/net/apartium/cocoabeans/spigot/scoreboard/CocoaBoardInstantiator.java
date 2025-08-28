package net.apartium.cocoabeans.spigot.scoreboard;

import net.apartium.cocoabeans.spigot.ServerUtils;
import net.apartium.cocoabeans.spigot.VersionedImplInstantiator;
import net.apartium.cocoabeans.structs.MinecraftVersion;

/**
 * @hidden
 */
/* package-private */ class CocoaBoardInstantiator {

    private CocoaBoardInstantiator() {}

    public static SpigotCocoaBoardFactory createCocoaBoardFactory() {
        MinecraftVersion minecraftVersion = ServerUtils.getVersion();
        return switch (minecraftVersion.update()) {
            case 8 -> constructCocoaBoardFactory("scoreboard.CocoaBoardFactory_1_8_R1");
            default -> constructCocoaBoardFactory("scoreboard.CocoaBoardFactory_1_20_R1");
        };
    }

    private static SpigotCocoaBoardFactory constructCocoaBoardFactory(String clazz) {
        return VersionedImplInstantiator.construct(clazz, SpigotCocoaBoardFactory.class);
    }

}
