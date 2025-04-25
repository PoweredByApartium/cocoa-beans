package net.apartium.cocoabeans.spigot.scoreboard;

import net.apartium.cocoabeans.minecraft.TeamMode;
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

    private static final boolean hasLinesMaxLength = !ServerUtils.getVersion().isHigherThanOrEqual(MinecraftVersion.V1_13);
    private static final int OLD_MAX = 16;
    private static final int NEW_MAX = 1024;

    public SpigotCocoaBoard_1_8_R1(Player player, String objectiveId, Observable<Component> title) {
        super(player, objectiveId, title);

        createBoardAndDisplay();
    }

    @Override
    protected void sendLineChange(int score, ComponentEntry entry) {
        int maxLength = hasLinesMaxLength ? OLD_MAX : NEW_MAX;
        String line = entry.component() != null ? LegacyComponentSerializer.legacySection().serialize(entry.component().get()) : "";
        String prefix;
        String suffix = "";

        if (line.isEmpty()) {
            prefix = COLOR_CODES[score] + ChatColor.RESET;
        } else if (line.length() <= maxLength) {
            prefix = line;
        } else {
            int index = line.charAt(maxLength - 1) == ChatColor.COLOR_CHAR
                    ? (maxLength - 1) : maxLength;
            prefix = line.substring(0, index);
            String suffixTmp = line.substring(index);
            ChatColor chatColor = null;

            if (suffixTmp.length() >= 2 && suffixTmp.charAt(0) == ChatColor.COLOR_CHAR) {
                chatColor = ChatColor.getByChar(suffixTmp.charAt(1));
            }

            String color = ChatColor.getLastColors(prefix);
            boolean addColor = chatColor == null || chatColor.isFormat();

            suffix = (addColor ? (color.isEmpty() ? ChatColor.RESET.toString() : color) : "") + suffixTmp;
        }

        if (prefix.length() > maxLength || suffix.length() > maxLength) {
            prefix = prefix.substring(0, Math.min(maxLength, prefix.length()));
            suffix = suffix.substring(0, Math.min(maxLength, suffix.length()));
        }

        sendTeamPacket(score, TeamMode.UPDATE, toObservable(Component.text(prefix)), toObservable(Component.text(suffix)));
    }

}
