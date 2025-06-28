package net.apartium.cocoabeans.scoreboard.numeric.fixture;

import net.apartium.cocoabeans.scoreboard.*;
import net.apartium.cocoabeans.scoreboard.fixture.MockPlayer;
import net.apartium.cocoabeans.scoreboard.numeric.packet.DisplayPacket;
import net.apartium.cocoabeans.scoreboard.numeric.packet.ObjectivePacket;
import net.apartium.cocoabeans.scoreboard.numeric.packet.ScorePacket;
import net.apartium.cocoabeans.state.CompoundRecords;
import net.apartium.cocoabeans.state.LazyWatcher;
import net.apartium.cocoabeans.state.Observable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;

import java.util.Map;
import java.util.Set;

public class TestNumericDisplay extends ScoreboardNumericDisplay<MockPlayer> {

    public TestNumericDisplay(String objectiveId, BoardPlayerGroup<MockPlayer> group, Observable<Component> displayName) {
        super(objectiveId, group, displayName);
    }

    @Override
    public void sendDisplayPacket(Set<MockPlayer> audience, DisplaySlot slot, String objectiveId) {
        if (audience.isEmpty())
            return;

        DisplayPacket packet = new DisplayPacket(slot, objectiveId);
        for (MockPlayer player : audience)
            player.sendPacket(packet);
    }

    @Override
    public void sendScorePacket(Set<MockPlayer> audience, String entity, int score, ScoreboardAction action, Component fixedContent, Style numberStyle) {
        if (audience.isEmpty())
            return;

        ScorePacket packet = new ScorePacket(entity, score, action, fixedContent, numberStyle);
        for (MockPlayer player : audience)
            player.sendPacket(packet);
    }

    @Override
    public void sendObjectivePacket(Set<MockPlayer> audience, ObjectiveMode mode, Component displayName) {
        if (audience.isEmpty())
            return;

        ObjectivePacket packet =  new ObjectivePacket(mode, renderType, displayName);
        for (MockPlayer player : audience)
            player.sendPacket(packet);
    }

    public Map<String, LazyWatcher<CompoundRecords.RecordOf3<Integer, Component, Style>>> getEntities() {
        return entities;
    }

}
