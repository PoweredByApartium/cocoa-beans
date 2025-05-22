package net.apartium.cocoabeans.scoreboard.spigot;

import net.apartium.cocoabeans.spigot.ServerUtils;
import net.apartium.cocoabeans.structs.MinecraftVersion;

/**
 * @hidden
 */
/* package-private */ class VersionedImplInstantiator {

    private VersionedImplInstantiator() {}

    public static SpigotCocoaBoardFactory createCocoaBoardFactory() {
        MinecraftVersion minecraftVersion = ServerUtils.getVersion();
        return switch (minecraftVersion.update()) {
            case 8 -> constructCocoaBoardFactory("scoreboard.CocoaBoardFactory_1_8_R1");
            default -> constructCocoaBoardFactory("scoreboard.CocoaBoardFactory_1_20_R1");
        };
    }

    private static SpigotCocoaBoardFactory constructCocoaBoardFactory(String clazz) {
        return construct(clazz, SpigotCocoaBoardFactory.class);
    }

    /* package-private */ static <T> T construct(String name, Class<T> type) {
        try {
            Class<? extends T> cls = Class.forName(String.format("net.apartium.cocoabeans.spigot.%s", name), true, SpigotCocoaBoardFactory.class.getClassLoader())
                    .asSubclass(type);
            return cls.getConstructor().newInstance();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
