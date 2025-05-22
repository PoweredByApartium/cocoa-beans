package net.apartium.cocoabeans.spigot.scoreboard;

import net.apartium.cocoabeans.scoreboard.TeamMode;
import net.apartium.cocoabeans.scoreboard.spigot.SpigotCocoaBoard;
import net.apartium.cocoabeans.spigot.ServerUtils;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.structs.MinecraftVersion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SpigotCocoaBoard_1_8_R1 extends SpigotCocoaBoard {

    private static final boolean HAS_LINES_MAX_LENGTH = !ServerUtils.getVersion().isHigherThanOrEqual(MinecraftVersion.V1_13);
    private static final int OLD_MAX = 16;
    private static final int NEW_MAX = 1024;
    private static final int MAX_LENGTH = HAS_LINES_MAX_LENGTH ? OLD_MAX : NEW_MAX;

    public SpigotCocoaBoard_1_8_R1(Player player, String objectiveId, Observable<Component> title) {
        super(player, objectiveId, title);

        createBoardAndDisplay();
    }

    @Override
    protected void sendLineChange(int score, ComponentEntry entry) {
        String line = entry.component() != null ? LegacyComponentSerializer.legacySection().serialize(entry.component().get()) : "";
        String prefix;
        String suffix = "";

        if (line.isEmpty()) {
            prefix = COLOR_CODES[score] + ChatColor.RESET;
        } else if (line.length() <= MAX_LENGTH) {
            prefix = line;
        } else {
            int index = line.charAt(MAX_LENGTH - 1) == ChatColor.COLOR_CHAR
                    ? (MAX_LENGTH - 1)
                    : MAX_LENGTH;

            prefix = line.substring(0, index);
            String suffixTmp = line.substring(index);

            suffix = addColor(prefix, chatColorForSuffix(suffixTmp)) + suffixTmp;
        }

        if (prefix.length() > MAX_LENGTH || suffix.length() > MAX_LENGTH) {
            prefix = prefix.substring(0, Math.min(MAX_LENGTH, prefix.length()));
            suffix = suffix.substring(0, Math.min(MAX_LENGTH, suffix.length()));
        }

        sendTeamPacket(score, TeamMode.UPDATE, toObservable(Component.text(prefix)), toObservable(Component.text(suffix)));
    }

    private String addColor(String prefix, ChatColor chatColor) {
        String color = ChatColor.getLastColors(prefix);
        if (chatColor == null || chatColor.isFormat())
            return color.isEmpty() ? ChatColor.RESET.toString() : color;

        return "";
    }

    private ChatColor chatColorForSuffix(String suffix) {
        if (suffix.length() >= 2 && suffix.charAt(0) == ChatColor.COLOR_CHAR)
            return ChatColor.getByChar(suffix.charAt(1));

        return null;
    }

}
