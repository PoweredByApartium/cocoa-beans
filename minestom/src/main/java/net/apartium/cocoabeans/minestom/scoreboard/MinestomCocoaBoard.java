package net.apartium.cocoabeans.minestom.scoreboard;

import net.apartium.cocoabeans.scoreboard.*;
import net.apartium.cocoabeans.state.Observable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.*;
import net.minestom.server.scoreboard.Sidebar;

import java.util.List;
import java.util.Optional;

public class MinestomCocoaBoard extends CocoaBoard {

    private final Player player;
    private static final NamedTextColor[] COLOR_CODES = List.of(
            NamedTextColor.BLACK, NamedTextColor.DARK_BLUE, NamedTextColor.DARK_GREEN, NamedTextColor.DARK_AQUA,
            NamedTextColor.DARK_RED, NamedTextColor.DARK_PURPLE, NamedTextColor.GOLD, NamedTextColor.GRAY,
            NamedTextColor.DARK_GRAY, NamedTextColor.BLUE, NamedTextColor.GREEN, NamedTextColor.AQUA,
            NamedTextColor.RED, NamedTextColor.LIGHT_PURPLE, NamedTextColor.YELLOW, NamedTextColor.WHITE
    ).toArray(new NamedTextColor[0]);

    /**
     * Constructor for CocoaBoard
     *
     * @param objectiveId            scoreboard objective id (Should be unique to support player receiving multiple CocoaBoard)
     * @param title                  title of the scoreboard
     */
    public MinestomCocoaBoard(Player player, String objectiveId, Observable<Component> title) {
        super(objectiveId, title, true);

        this.player = player;

        createBoardAndDisplay();
    }

    public MinestomCocoaBoard(Player player, String objectiveId, Component title) {
        this(player, objectiveId, toObservable(title));
    }

    public MinestomCocoaBoard(Player player, Observable<Component> title) {
        this(player, "cocoa-" + player.getUuid(), title);
    }

    public MinestomCocoaBoard(Player player, Component title) {
        this(player, toObservable(title));
    }

    public MinestomCocoaBoard(Player player, String objectiveId) {
        this(player, objectiveId, toObservable(Component.text("CocoaBoard")));
    }

    public MinestomCocoaBoard(Player player) {
        this(player, "cocoa-" + player.getUuid(), toObservable(Component.text("CocoaBoard")));
    }

    @Override
    protected void sendObjectivePacket(ObjectiveMode mode, Observable<Component> displayName) {
        player.sendPacket(new ScoreboardObjectivePacket(
                objectiveId,
                (byte) mode.getId(),
                Optional.ofNullable(displayName)
                        .map(Observable::get)
                        .orElse(null),
                ScoreboardObjectivePacket.Type.INTEGER,
                null
        ));
    }

    @Override
    protected void sendDisplayPacket() {
        player.sendPacket(new DisplayScoreboardPacket(
                (byte) DisplaySlot.SIDEBAR.getId(),
                objectiveId
        ));
    }

    @Override
    protected void sendLineChange(int score, ComponentEntry line) {
        sendTeamPacket(score, TeamMode.UPDATE, line.component(), null);
    }

    private String colorName(int score) {
        return "§" + Integer.toHexString(score);
    }

    @Override
    protected void sendTeamPacket(int score, TeamMode mode, Observable<Component> prefix, Observable<Component> suffix) {
        player.sendPacket(new TeamsPacket(intoTeamName(score), switch (mode) {
            case CREATE -> new TeamsPacket.CreateTeamAction(
                    Component.empty(),
                    (byte) 1,
                    TeamsPacket.NameTagVisibility.ALWAYS,
                    TeamsPacket.CollisionRule.ALWAYS,
                    COLOR_CODES[score],
                    Optional.ofNullable(prefix)
                            .map(Observable::get)
                            .orElse(Component.empty()),
                    Optional.ofNullable(suffix)
                            .map(Observable::get)
                            .orElse(Component.empty()),
                    List.of(colorName(score))
            );
            case REMOVE -> new TeamsPacket.RemoveTeamAction();
            case UPDATE -> new TeamsPacket.UpdateTeamAction(
                    Component.empty(),
                    (byte) 1,
                    TeamsPacket.NameTagVisibility.ALWAYS,
                    TeamsPacket.CollisionRule.ALWAYS,
                    COLOR_CODES[score],
                    Optional.ofNullable(prefix)
                            .map(Observable::get)
                            .orElse(Component.empty()),
                    Optional.ofNullable(suffix)
                            .map(Observable::get)
                            .orElse(Component.empty())
            );
            default -> throw new UnsupportedOperationException("Doesn't have implementation for " + mode.name());
        }));
    }

    @Override
    protected void sendScorePacket(int score, Observable<Component> displayName, ScoreboardAction action, Style numberStyle) {
        if (action == ScoreboardAction.REMOVE) {
            player.sendPacket(new ResetScorePacket(colorName(score), objectiveId));
            return;
        }

        Component displayNameComponent = Optional.ofNullable(displayName)
                .map(Observable::get)
                .orElse(null);

        if (displayNameComponent == Component.empty())
            displayNameComponent = null;

        Sidebar.NumberFormat numberFormat = displayNameComponent != null || numberStyle == null
                ? Sidebar.NumberFormat.blank()
                : Sidebar.NumberFormat.styled(Component.text(score).style(numberStyle));

        player.sendPacket(new UpdateScorePacket(
                colorName(score),
                objectiveId,
                score,
                displayNameComponent,
                numberFormat
        ));
    }
}
