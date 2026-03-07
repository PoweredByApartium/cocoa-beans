package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.spigot.schematic.prop.BambooProp;
import org.bukkit.block.data.type.Bamboo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class BambooPropFormatTest {

    @Test
    void roundtripAllLeaves() {
        BambooPropFormat format = BambooPropFormat.INSTANCE;

        for (Bamboo.Leaves leaves : Bamboo.Leaves.values()) {
            byte[] encoded = format.encode(new BambooProp(leaves));
            BambooProp decoded = (BambooProp) format.decode(encoded);

            assertEquals(leaves, decoded.value());
        }
    }

    @Test
    void encodeRejectsNullValue() {
        BambooPropFormat format = BambooPropFormat.INSTANCE;

        assertThrowsExactly(IllegalArgumentException.class, () -> format.encode(() -> null));
    }

    @Test
    void encodeRejectsWrongValueType() {
        BambooPropFormat format = BambooPropFormat.INSTANCE;

        assertThrowsExactly(IllegalArgumentException.class, () -> format.encode(() -> "nope"));
    }

    @Test
    void decodeRejectsInvalidData() {
        BambooPropFormat format = BambooPropFormat.INSTANCE;

        assertThrowsExactly(IllegalArgumentException.class, () -> format.decode(new byte[0]));
    }
}
