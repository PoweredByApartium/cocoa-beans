package net.apartium.cocoabeans.spigot.fixture;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CocoaPlayerMock extends PlayerMock {

    private Component playerListHeader = null;
    private Component playerListFooter = null;

    public CocoaPlayerMock(@NotNull ServerMock server, @NotNull String name, @NotNull UUID uuid) {
        super(server, name, uuid);
    }

    @Override
    public void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
        this.playerListHeader = header;
        this.playerListFooter = footer;
    }

    @Override
    public @Nullable Component playerListHeader() {
        return playerListHeader;
    }

    @Override
    public @Nullable Component playerListFooter() {
        return playerListFooter;
    }
}
