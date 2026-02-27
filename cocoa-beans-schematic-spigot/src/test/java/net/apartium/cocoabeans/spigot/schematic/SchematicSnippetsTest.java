package net.apartium.cocoabeans.spigot.schematic;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.spigot.Locations;
import net.apartium.cocoabeans.spigot.SpigotTestBase;
import org.bukkit.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SchematicSnippetsTest extends SpigotTestBase {

    PlayerMock player;

    @Override
    public void initialize() {
        player = server.addPlayer();
    }

    @Test
    void quickStart() {
        Location location = player.getLocation();

        // Load a schematic from a cube around the player
        SpigotSchematic schematic = SpigotSchematicHelper.load(
                "test-schematic",
                player.getName(),
                new Position(location.getX(), location.getY(), location.getZ()).floor(),
                location.getWorld(),
                Locations.toPosition(location.clone().add(5, 5, 5)),
                Locations.toPosition(location.clone().add(-5, -5, -5)),
                SpigotSchematicPlacer.getInstance()
        );
        assertNotNull(schematic);

        // move the player somewhere else
        player.teleport(new Location(location.getWorld(), 0, 100, 0));

        // Create a paste operation
        // SpigotPasteOperation gives flexibility of control to the library user
        SpigotPasteOperation pasteOperation = schematic.paste(location);
        assertNotNull(pasteOperation);

        // just paste all the blocks in one go
        pasteOperation.performAll();

    }
}
