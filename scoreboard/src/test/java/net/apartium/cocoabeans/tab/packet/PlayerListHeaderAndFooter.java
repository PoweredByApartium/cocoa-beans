package net.apartium.cocoabeans.tab.packet;

import net.apartium.cocoabeans.packet.Packet;
import net.kyori.adventure.text.Component;

public record PlayerListHeaderAndFooter(
        Component header,
        Component footer
) implements Packet {
}
