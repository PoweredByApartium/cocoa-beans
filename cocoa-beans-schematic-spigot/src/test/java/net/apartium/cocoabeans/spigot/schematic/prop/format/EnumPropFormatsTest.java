package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import org.bukkit.Axis;
import org.bukkit.Instrument;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.type.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class EnumPropFormatsTest {

    private record EnumFormatCase(String name, EnumPropFormat<?> format, Supplier<? extends Enum<?>[]> values) {}

    private static List<EnumFormatCase> cases() {
        return List.of(
                new EnumFormatCase("Rail.Shape", RailShapePropFormat.INSTANCE, Rail.Shape::values),
                new EnumFormatCase("PointedDripstone.Thickness", PointedDripstoneThicknessPropFormat.INSTANCE, PointedDripstone.Thickness::values),
                new EnumFormatCase("BigDripleaf.Tilt", BigDripleafTiltPropFormat.INSTANCE, BigDripleaf.Tilt::values),
                new EnumFormatCase("StructureBlock.Mode", StructureBlockModePropFormat.INSTANCE, StructureBlock.Mode::values),
                new EnumFormatCase("Chest.Type", ChestTypePropFormat.INSTANCE, Chest.Type::values),
                new EnumFormatCase("FaceAttachable.AttachedFace", FaceAttachableAttachedFacePropFormat.INSTANCE, FaceAttachable.AttachedFace::values),
                new EnumFormatCase("Comparator.Mode", ComparatorModePropFormat.INSTANCE, Comparator.Mode::values),
                new EnumFormatCase("TechnicalPiston.Type", TechnicalPistonTypePropFormat.INSTANCE, TechnicalPiston.Type::values),
                new EnumFormatCase("Door.Hinge", DoorHingePropFormat.INSTANCE, Door.Hinge::values),
                new EnumFormatCase("Bell.Attachment", BellAttachmentPropFormat.INSTANCE, Bell.Attachment::values),
                new EnumFormatCase("Slab.Type", SlabTypePropFormat.INSTANCE, Slab.Type::values),
                new EnumFormatCase("Stairs.Shape", StairsPropFormat.INSTANCE, Stairs.Shape::values),
                new EnumFormatCase("Bed.Part", BedPartPropFormat.INSTANCE, Bed.Part::values),
                new EnumFormatCase("SculkSensor.Phase", SculkSensorPhasePropFormat.INSTANCE, SculkSensor.Phase::values),
                new EnumFormatCase("Directional.BlockFace", DirectionalPropFormat.INSTANCE, BlockFace::values),
                new EnumFormatCase("BlockFace", new BlockFacePropFormat(value -> () -> value), BlockFace::values),
                new EnumFormatCase("Bisected.Half", BisectedHalfPropFormat.INSTANCE, Bisected.Half::values),
                new EnumFormatCase("Jigsaw.Orientation", JigsawOrientationPropFormat.INSTANCE, Jigsaw.Orientation::values),
                new EnumFormatCase("Instrument", NoteBlockInstrumentPropFormat.INSTANCE, Instrument::values),
                new EnumFormatCase("Axis", OrientableAxisPropFormat.INSTANCE, Axis::values)
        );
    }

    @Test
    void roundtripAllValues() {
        for (EnumFormatCase formatCase : cases()) {
            for (Enum<?> value : formatCase.values().get()) {
                byte[] encoded = formatCase.format().encode(() -> value);
                assertNotNull(encoded, formatCase.name() + " encoded is null");
                BlockProp<?> decoded = formatCase.format().decode(encoded);
                assertEquals(value, decoded.value(), formatCase.name() + " roundtrip mismatch");
            }
        }
    }

    @Test
    void encodeRejectsNullValue() {
        for (EnumFormatCase formatCase : cases()) {
            assertThrowsExactly(IllegalArgumentException.class, () -> formatCase.format().encode(() -> null), formatCase.name());
        }
    }

    @Test
    void encodeRejectsWrongValueType() {
        for (EnumFormatCase formatCase : cases()) {
            assertThrowsExactly(IllegalArgumentException.class, () -> formatCase.format().encode(() -> "nope"), formatCase.name());
        }
    }

    @Test
    void decodeRejectsInvalidData() {
        for (EnumFormatCase formatCase : cases()) {
            assertThrowsExactly(IllegalArgumentException.class, () -> formatCase.format().decode(new byte[0]), formatCase.name());
        }
    }
}
