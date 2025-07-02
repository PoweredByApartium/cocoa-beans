package net.apartium.cocoabeans.scoreboard.numeric.packet;

import net.apartium.cocoabeans.scoreboard.ScoreboardAction;
import net.apartium.cocoabeans.scoreboard.packet.Packet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;

public record ScorePacket(String entity, int score, ScoreboardAction action, Component fixedContent, Style numerStyle) implements Packet {
}
