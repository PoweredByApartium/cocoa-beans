package net.apartium.cocoabeans.scoreboard.packet;

import net.apartium.cocoabeans.scoreboard.ScoreboardAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;

public record ScorePacket(
        int score,
        Component displayName,
        ScoreboardAction action,
        Style numberStyle
) implements Packet {

}
