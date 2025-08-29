package net.apartium.cocoabeans.scoreboard;

import net.apartium.cocoabeans.packet.Packet;
import net.apartium.cocoabeans.scoreboard.packet.DisplayPacket;
import net.apartium.cocoabeans.scoreboard.packet.ObjectivePacket;
import net.apartium.cocoabeans.scoreboard.packet.ScorePacket;
import net.apartium.cocoabeans.scoreboard.packet.TeamPacket;
import net.apartium.cocoabeans.state.Observable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestBoard extends CocoaBoard {

    private final List<Packet> packets = new ArrayList<>();

    public TestBoard(String objectiveId, Observable<Component> title, boolean isCustomScoreSupported) {
        super(objectiveId, title, isCustomScoreSupported);

        createBoardAndDisplay();
    }

    @Override
    protected void sendObjectivePacket(ObjectiveMode mode, Observable<Component> displayName) {
        packets.add(new ObjectivePacket(mode, Optional.ofNullable(displayName).map(Observable::get).orElse(null)));
    }

    @Override
    protected void sendDisplayPacket() {
        packets.add(new DisplayPacket());
    }

    @Override
    protected void sendLineChange(int score, ComponentEntry line, TeamMode mode) {
        sendTeamPacket(score, mode, line.component(), null);
    }

    @Override
    protected void sendTeamPacket(int score, TeamMode mode, Observable<Component> prefix, Observable<Component> suffix) {
        packets.add(new TeamPacket(
                score,
                mode,
                Optional.ofNullable(prefix).map(Observable::get).orElse(null),
                Optional.ofNullable(suffix).map(Observable::get).orElse(null)
        ));
    }

    @Override
    protected void sendScorePacket(int score, Observable<Component> displayName, ScoreboardAction action, Style numberStyle) {
        packets.add(new ScorePacket(
                score,
                Optional.ofNullable(displayName).map(Observable::get).orElse(null),
                action,
                numberStyle
        ));
    }

    public List<Packet> getPackets() {
        return packets;
    }
}
