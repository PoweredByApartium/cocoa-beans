package net.apartium.cocoabeans.spigot.scoreboard;

import net.apartium.cocoabeans.scoreboard.CocoaBoard;
import net.apartium.cocoabeans.scoreboard.TeamMode;
import net.apartium.cocoabeans.state.Observable;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SpigotCocoaBoard_1_20_R1 extends SpigotCocoaBoard {

    public SpigotCocoaBoard_1_20_R1(Player player, String objectiveId, Observable<Component> title) {
        super(player, objectiveId, title);

        createBoardAndDisplay();
    }

    @Override
    protected void sendLineChange(int score, CocoaBoard.ComponentEntry line, TeamMode mode) {
        sendTeamPacket(score, mode, line.component(), null);
    }

}
