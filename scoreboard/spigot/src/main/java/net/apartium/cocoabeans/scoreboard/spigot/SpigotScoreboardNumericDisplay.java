package net.apartium.cocoabeans.scoreboard.spigot;

import net.apartium.cocoabeans.scoreboard.*;
import net.apartium.cocoabeans.state.CompoundRecords;
import net.apartium.cocoabeans.state.DirtyWatcher;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.structs.Entry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.entity.Player;

import java.util.*;

public class SpigotScoreboardNumericDisplay extends ScoreboardNumericDisplay<Player> {

    public SpigotScoreboardNumericDisplay(String objectiveId, BoardPlayerGroup<Player> group, Observable<Component> displayName) {
        super(objectiveId, group, displayName);
    }

    @Override
    public void heartbeat() {
        super.heartbeat();
        if (groupWatcher.isDirty()) {
            Set<Player> cache = Optional.ofNullable(groupWatcher.getCache()).orElse(Collections.emptySet());
            Entry<Set<Player>, Boolean> entry = groupWatcher.get();

            if (!entry.value())
                return;

            Set<Player> toAdd = new HashSet<>(entry.key());
            toAdd.removeAll(cache);

            Set<Player> toRemove = new HashSet<>(cache);
            toRemove.removeAll(entry.key());

            // Add

            sendObjectivePacket(
                    toAdd,
                    ObjectiveMode.CREATE,
                    Optional.ofNullable(displayName.getCache()).orElse(Component.empty())
            );

            for (DisplaySlot slot : displaySlots)
                sendDisplayPacket(
                        toAdd,
                        slot,
                        objectiveId
                );

            for (Map.Entry<String, DirtyWatcher<CompoundRecords.RecordOf3<Integer, Component, Style>>> entity : entities.entrySet()) {
                sendScorePacket(
                        toAdd,
                        entity.getKey(),
                        entity.getValue().getCache().arg0(),
                        ScoreboardAction.CREATE_OR_UPDATE,
                        entity.getValue().getCache().arg1(),
                        entity.getValue().getCache().arg2()
                );
            }

            // Remove

            sendObjectivePacket(
                    toRemove,
                    ObjectiveMode.REMOVE,
                    null
            );
        }
    }

    @Override
    public void sendDisplayPacket(Set<Player> audience, DisplaySlot slot, String objectiveId) {
        if (audience.isEmpty())
            return;

        try {
            Object packet = NMSUtils.createDisplayPacket(objectiveId, slot);
            for (Player player : audience)
                NMSUtils.sendPacket(player, packet);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendScorePacket(Set<Player> audience, String entity, int score, ScoreboardAction action, Component fixedContent, Style numberStyle) {
        if (audience.isEmpty())
            return;

        try {
            Object packet = NMSUtils.createScorePacket(
                    entity,
                    objectiveId,
                    Observable.immutable(fixedContent),
                    score,
                    action,
                    numberStyle
            );

            for (Player player : audience)
                NMSUtils.sendPacket(player, packet);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendObjectivePacket(Set<Player> audience, ObjectiveMode mode, Component displayName) {
        if (audience.isEmpty())
            return;

        try {
            Object packet = NMSUtils.createObjectivePacket(
                    objectiveId,
                    mode,
                    renderType,
                    Observable.immutable(displayName)
            );

            for (Player player : audience)
                NMSUtils.sendPacket(player, packet);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
