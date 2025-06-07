package net.apartium.cocoabeans.scoreboard.spigot;

import net.apartium.cocoabeans.scoreboard.TeamMode;
import net.apartium.cocoabeans.scoreboard.team.ChatFormatting;
import net.apartium.cocoabeans.scoreboard.team.DisplayTeam;
import net.apartium.cocoabeans.scoreboard.team.CollisionRule;
import net.apartium.cocoabeans.scoreboard.team.NameTagVisibilityRule;
import net.apartium.cocoabeans.structs.Entry;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.*;


public class SpigotDisplayTeam extends DisplayTeam<Player> {

    public SpigotDisplayTeam(String name) {
        super(name, new SpigotBoardPlayerGroup(Collections.newSetFromMap(new WeakHashMap<>())));
    }

    @Override
    public void heartbeat() {
        if (groupWatcher.isDirty()) {
            Set<Player> cache = groupWatcher.getCache();
            Entry<Set<Player>, Boolean> entry = groupWatcher.get();

            if (!entry.value())
                return;

            Set<Player> toAdd = new HashSet<>(entry.key());
            toAdd.removeAll(cache);

            Set<Player> toRemove = new HashSet<>(cache);
            toRemove.removeAll(entry.key());

            sendCreateTeamPacket(
                    toAdd,
                    displayName.getCache(),
                    Optional.ofNullable(friendlyFire.getCache()).orElse((byte) 0x01),
                    nameTagVisibilityRule.getCache(),
                    collisionRule.getCache(),
                    formatting.getCache(),
                    prefix.getCache(),
                    suffix.getCache(),
                    Optional.ofNullable(entities.getCache()).orElse(Set.of())
            );

            sendRemoveTeamPacket(
                    toRemove
            );
        }

        super.heartbeat();
    }

    @Override
    public void sendUpdateTeamPacket(Set<Player> audience, Component displayName, byte friendlyFire, NameTagVisibilityRule nameTagVisibilityRule, CollisionRule collisionRule, ChatFormatting formatting, Component prefix, Component suffix) {
        for (Player target : audience) {
            try {
                NMSUtils.sendPacket(
                        target,
                        NMSUtils.createTeamPacket(
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
                        )
                );
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void sendAddEntitiesPacket(Set<Player> audience, Collection<String> addEntities) {
        for (Player target : audience) {
            try {
                NMSUtils.sendPacket(
                        target,
                        NMSUtils.createTeamPacket(
                                name,
                                TeamMode.ADD_PLAYERS,
                                addEntities
                        )
                );
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void sendRemoveEntitiesPacket(Set<Player> audience, Collection<String> removeEntities) {
        for (Player target : audience) {
            try {
                NMSUtils.sendPacket(
                        target,
                        NMSUtils.createTeamPacket(
                                name,
                                TeamMode.REMOVE_PLAYERS,
                                removeEntities
                        )
                );
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void sendCreateTeamPacket(Set<Player> audience, Component displayName, byte friendlyFire, NameTagVisibilityRule nameTagVisibilityRule, CollisionRule collisionRule, ChatFormatting formatting, Component prefix, Component suffix, Collection<String> entities) {
        try {
            Object teamPacket = NMSUtils.createTeamPacket(
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
                        teamPacket
                );
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendRemoveTeamPacket(Set<Player> audience) {
        try {
            Object teamPacket = NMSUtils.createTeamPacket(
                    name,
                    TeamMode.REMOVE
            );

                for (Player target : audience) {
                    NMSUtils.sendPacket(
                            target,
                            teamPacket
                    );
                }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
