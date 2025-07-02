package net.apartium.cocoabeans.spigot.scoreboard;

import net.apartium.cocoabeans.scoreboard.*;
import net.apartium.cocoabeans.state.ViewerGroup;
import net.apartium.cocoabeans.state.Observable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @see ScoreboardNumericDisplay
 */
public class SpigotScoreboardNumericDisplay extends ScoreboardNumericDisplay<Player> {

    public SpigotScoreboardNumericDisplay(String objectiveId, ViewerGroup<Player> group, Observable<Component> displayName) {
        super(objectiveId, group, displayName);
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
                    fixedContent,
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
                    displayName
            );

            for (Player player : audience)
                NMSUtils.sendPacket(player, packet);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
