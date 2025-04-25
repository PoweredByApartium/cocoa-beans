package net.apartium.cocoabeans.spigot.scoreboard;

import net.apartium.cocoabeans.minecraft.*;
import net.apartium.cocoabeans.spigot.VersionedImplInstantiator;
import net.apartium.cocoabeans.spigot.utils.NMSUtils;
import net.apartium.cocoabeans.state.Observable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public abstract class SpigotCocoaBoard extends CocoaBoard {

    private static final SpigotCocoaBoardFactory factory = VersionedImplInstantiator.createCocoaBoardFactory();

    /**
     * Create a new SpigotCocoaBoard with {@link SpigotCocoaBoardFactory} that will return new instance base on {@link net.apartium.cocoabeans.structs.MinecraftVersion}
     * @param player player that scoreboard will target
     * @param objectiveId objective id is the id of the scoreboard objective
     * @param title title of the scoreboard
     * @return new instance of SpigotCocoaBoard for player
     */
    public static SpigotCocoaBoard create(Player player, String objectiveId, Observable<Component> title) {
        return factory.create(player, objectiveId, title);
    }

    /**
     * Create a new SpigotCocoaBoard with {@link SpigotCocoaBoardFactory} that will return new instance base on {@link net.apartium.cocoabeans.structs.MinecraftVersion}
     * @param player player that scoreboard will target
     * @param objectiveId objective id is the id of the scoreboard objective
     * @param title title of the scoreboard
     * @return new instance of SpigotCocoaBoard for player
     */
    public static SpigotCocoaBoard create(Player player, String objectiveId, Component title) {
        return factory.create(player, objectiveId, title);
    }

    /**
     * Create a new SpigotCocoaBoard with {@link SpigotCocoaBoardFactory} that will return new instance base on {@link net.apartium.cocoabeans.structs.MinecraftVersion}
     * @param player player that scoreboard will target
     * @param objectiveId objective id is the id of the scoreboard objective
     * @return new instance of SpigotCocoaBoard for player
     */
    public static SpigotCocoaBoard create(Player player, String objectiveId) {
        return factory.create(player, objectiveId);
    }

    /**
     * Create a new SpigotCocoaBoard with {@link SpigotCocoaBoardFactory} that will return new instance base on {@link net.apartium.cocoabeans.structs.MinecraftVersion}
     * @param player player that scoreboard will target
     * @param title title of the scoreboard
     * @return new instance of SpigotCocoaBoard for player
     */
    public static SpigotCocoaBoard create(Player player, Observable<Component> title) {
        return factory.create(player, title);
    }

    /**
     * Create a new SpigotCocoaBoard with {@link SpigotCocoaBoardFactory} that will return new instance base on {@link net.apartium.cocoabeans.structs.MinecraftVersion}
     * @param player player that scoreboard will target
     * @param title title of the scoreboard
     * @return new instance of SpigotCocoaBoard for player
     */
    public static SpigotCocoaBoard create(Player player, Component title) {
        return factory.create(player, title);
    }

    /**
     * Create a new SpigotCocoaBoard with {@link SpigotCocoaBoardFactory} that will return new instance base on {@link net.apartium.cocoabeans.structs.MinecraftVersion}
     * @param player player that scoreboard will target
     * @return new instance of SpigotCocoaBoard for player
     */
    public static SpigotCocoaBoard create(Player player) {
        return factory.create(player);
    }

    protected static final String[] COLOR_CODES = Arrays.stream(ChatColor.values())
            .map(Object::toString)
            .toArray(String[]::new);

    private WeakReference<Player> player;
    protected final UUID playerUUID;

    protected SpigotCocoaBoard(Player player, String objectiveId, Observable<Component> title) {
        super(objectiveId, title, NMSUtils.isCustomScoreSupported());

        this.playerUUID = player.getUniqueId();
        this.player = new WeakReference<>(player);
    }

    public Player getPlayer() {
        Player target = this.player.get();

        if (target == null) {
            target = Bukkit.getPlayer(playerUUID);
            if (target == null)
                return null;

            this.player = new WeakReference<>(target);
        }

        return target;
    }


    @Override
    protected void sendObjectivePacket(ObjectiveMode mode, Observable<Component> displayName) {
        try {
            NMSUtils.sendPacket(getPlayer(), NMSUtils.createObjectivePacket(objectiveId, mode, ObjectiveRenderType.INTEGER, displayName));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void sendDisplayPacket() {
        try {
            NMSUtils.sendPacket(getPlayer(), NMSUtils.createDisplayPacket(objectiveId, DisplaySlot.SIDEBAR));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void sendScorePacket(int score, Observable<Component> displayName, ScoreboardAction action, Style numberStyle) {
        try {
            NMSUtils.sendPacket(
                    getPlayer(),
                    NMSUtils.createScorePacket(
                            COLOR_CODES[score],
                            objectiveId,
                            displayName,
                            score,
                            action,
                            numberStyle
                    )
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void sendTeamPacket(int score, TeamMode mode, Observable<Component> prefix, Observable<Component> suffix) {
        try {
            NMSUtils.sendPacket(
                    getPlayer(),
                    NMSUtils.createTeamPacket(
                            intoTeamName(score),
                            mode,
                            prefix,
                            suffix,
                            mode == TeamMode.CREATE
                                    ? Collections.singleton(COLOR_CODES[score])
                                    : Collections.emptyList()
                    )
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
