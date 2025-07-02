package net.apartium.cocoabeans.spigot.scoreboard;

import net.apartium.cocoabeans.state.ViewerGroup;
import net.apartium.cocoabeans.scoreboard.TeamMode;
import net.apartium.cocoabeans.scoreboard.team.ChatFormatting;
import net.apartium.cocoabeans.scoreboard.team.CollisionRule;
import net.apartium.cocoabeans.scoreboard.team.DisplayTeam;
import net.apartium.cocoabeans.scoreboard.team.NameTagVisibilityRule;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

/**
 * @see DisplayTeam
 */
@ApiStatus.AvailableSince("0.0.41")
public class SpigotDisplayTeam extends DisplayTeam<Player> {

    public SpigotDisplayTeam(String name, ViewerGroup<Player> group) {
        super(name, group);
    }

    @Override
    public void sendUpdateTeamPacket(Set<Player> audience, Component displayName, byte friendlyFire, NameTagVisibilityRule nameTagVisibilityRule, CollisionRule collisionRule, ChatFormatting formatting, Component prefix, Component suffix) {
        if (audience.isEmpty())
            return;

        try {
            Object packet = NMSUtils.createTeamPacket(
                    name,
                    TeamMode.UPDATE,
                    Optional.ofNullable(displayName).orElse(Component.empty()),
                    friendlyFire,
                    Optional.ofNullable(nameTagVisibilityRule).orElse(NameTagVisibilityRule.ALWAYS),
                    Optional.ofNullable(collisionRule).orElse(CollisionRule.ALWAYS),
                    Optional.ofNullable(formatting).orElse(ChatFormatting.RESET),
                    Optional.ofNullable(prefix).orElse(Component.empty()),
                    Optional.ofNullable(suffix).orElse(Component.empty()),
                    Set.of()
            );

            for (Player target : audience) {
                NMSUtils.sendPacket(
                        target,
                        packet
                );
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendAddEntitiesPacket(Set<Player> audience, Collection<String> addEntities) {
        if (audience.isEmpty())
            return;

        try {
            Object packet = NMSUtils.createTeamPacket(
                    name,
                    TeamMode.ADD_PLAYERS,
                    addEntities
            );
            for (Player target : audience) {
                NMSUtils.sendPacket(
                        target,
                        packet
                );
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendRemoveEntitiesPacket(Set<Player> audience, Collection<String> removeEntities) {
        if (audience.isEmpty())
            return;

        try {
            Object packet = NMSUtils.createTeamPacket(
                    name,
                    TeamMode.REMOVE_PLAYERS,
                    removeEntities
            );
            for (Player target : audience) {
                NMSUtils.sendPacket(
                        target,
                        packet
                );
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendCreateTeamPacket(Set<Player> audience, Component displayName, byte friendlyFire, NameTagVisibilityRule nameTagVisibilityRule, CollisionRule collisionRule, ChatFormatting formatting, Component prefix, Component suffix, Collection<String> entities) {
        if (audience.isEmpty())
            return;

        try {
            Object packet = NMSUtils.createTeamPacket(
                    name,
                    TeamMode.CREATE,
                    Optional.ofNullable(displayName).orElse(Component.empty()),
                    friendlyFire,
                    Optional.ofNullable(nameTagVisibilityRule).orElse(NameTagVisibilityRule.ALWAYS),
                    Optional.ofNullable(collisionRule).orElse(CollisionRule.ALWAYS),
                    Optional.ofNullable(formatting).orElse(ChatFormatting.RESET),
                    Optional.ofNullable(prefix).orElse(Component.empty()),
                    Optional.ofNullable(suffix).orElse(Component.empty()),
                    Optional.ofNullable(entities).orElse(Set.of())
            );

            for (Player target : audience) {
                NMSUtils.sendPacket(
                        target,
                        packet
                );
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendRemoveTeamPacket(Set<Player> audience) {
        if (audience.isEmpty())
            return;

        try {
            Object packet = NMSUtils.createTeamPacket(
                    name,
                    TeamMode.REMOVE
            );

                for (Player target : audience) {
                    NMSUtils.sendPacket(
                            target,
                            packet
                    );
                }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
