# 🏗 Schematic

Paste schematic files into block worlds using the `cocoa-beans-schematic` modules.

<video
    src="schematic-tower-paste.mp4"
    preview-src="scoreboard-hellowolrd.png"
/>

## Getting Started


### Installation {id="installation"}

<tabs>
<tab title="Gradle">

```groovy
dependencies {
    // Core schematic API
    implementation("dev.apartium.cocoa-beans:cocoa-beans-schematic:%version%")

    // Spigot implementation
    implementation("dev.apartium.cocoa-beans:cocoa-beans-schematic-spigot:%version%")
}
```

</tab>
<tab title="Maven">

```xml
<dependencies>
    <!-- Core schematic API -->
    <dependency>
        <groupId>dev.apartium.cocoa-beans</groupId>
        <artifactId>cocoa-beans-schematic</artifactId>
        <version>%version%</version>
    </dependency>

    <!-- Spigot implementation -->
    <dependency>
        <groupId>dev.apartium.cocoa-beans</groupId>
        <artifactId>cocoa-beans-schematic-spigot</artifactId>
        <version>%version%</version>
    </dependency>
</dependencies>
```

</tab>
</tabs>

### Quick Start {id="quick-start"}
<code-block lang="java" src="schematic-spigot/SchematicSnippetsTest.java" include-symbol="quickStart"/>

## Building Schematics

### Creating from Scratch {id="create-scratch"}

Build schematics programmatically block by block using `SpigotSchematicBuilder`:

```java
BlockData stone = new GenericBlockData(new NamespacedKey("minecraft", "stone"), Map.of());
BlockData planks = new GenericBlockData(new NamespacedKey("minecraft", "oak_planks"), Map.of());

SpigotSchematic schematic = new SpigotSchematicBuilder()
    .metadata(meta -> meta
        .title("My Structure")
        .author("PlayerName")
        .build())
    .size(new AreaSize(3, 2, 3))
    // floor
    .setBlock(0, 0, 0, stone).setBlock(1, 0, 0, stone).setBlock(2, 0, 0, stone)
    .setBlock(0, 0, 1, stone).setBlock(1, 0, 1, stone).setBlock(2, 0, 1, stone)
    .setBlock(0, 0, 2, stone).setBlock(1, 0, 2, stone).setBlock(2, 0, 2, stone)
    // walls (perimeter of y=1)
    .setBlock(0, 1, 0, planks).setBlock(1, 1, 0, planks).setBlock(2, 1, 0, planks)
    .setBlock(0, 1, 1, planks)                           .setBlock(2, 1, 1, planks)
    .setBlock(0, 1, 2, planks).setBlock(1, 1, 2, planks).setBlock(2, 1, 2, planks)
    .build();
```

### Capturing from the World {id="capture-world"}

Extract a region from a live Bukkit world using `SpigotSchematicHelper`:

```java
// Positions of two opposite corners of the region
Position pos0 = Locations.toPosition(player.getLocation().add( 10,  5,  10));
Position pos1 = Locations.toPosition(player.getLocation().add(-10, -5, -10));

SpigotSchematic schematic = SpigotSchematicHelper.load(
    "my-house",                             // title
    player.getName(),                        // author
    Locations.toPosition(player.getLocation()).floor(), // paste origin offset
    player.getWorld(),
    pos0,
    pos1,
    SpigotSchematicPlacer.getInstance()
);

if (schematic == null) {
    player.sendMessage("Failed to capture schematic!");
    return;
}

player.sendMessage("Captured " + schematic.metadata().title() + "!");
```

> Blocks that are `AIR` are skipped automatically during capture.

### Transforming Schematics {id="transform"}

Use `toBuilder()` to create a modified copy of an existing schematic:

```java
SpigotSchematic rotated = schematic.toBuilder()
    .rotate(90)                        // Rotate 90° clockwise (top-down)
    .flip(Axis.X)                      // Mirror along the X axis
    .translate(new Position(5, 0, 5))  // Shift paste offset
    .build();
```

Available transformations:
- `rotate(90|180|270)` — Rotate schematic
- `flip(Axis)` — Mirror along axis
- `translate(Position)` — Change paste offset
- `translate(AxisOrder)` — Reorder block storage axis
- `shift(Axis, int)` — Move all blocks along an axis

## Saving and Loading Schematics

### Setting Up the Format {id="format-setup"}

`CocoaSchematicFormat` handles reading and writing `.cbschem` files. Create one instance and reuse it:

```java
CocoaSchematicFormat<SpigotSchematic> format = new CocoaSchematicFormat<>(
    Map.of(
        SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of(
            // Register prop formats for the block properties you want to persist.
            // For basic blocks (stone, planks, etc.) this can be left empty:
            // Map.of()
            BlockProp.WATERLOGGED, new BooleanPropFormat(WaterloggedProp::new),
            BlockProp.DIRECTIONAL,  new BlockFacePropFormat(DirectionalFaceProp::new),
            BlockProp.ORIENTABLE_AXIS, OrientableAxisPropFormat.INSTANCE,
            BlockProp.OPENABLE_OPEN, new BooleanPropFormat(OpenableOpenProp::new),
            BlockProp.POWERABLE_POWERED, new BooleanPropFormat(PowerablePoweredProp::new),
            BlockProp.SLAB_TYPE, SlabTypePropFormat.INSTANCE,
            BlockProp.STAIRS_SHAPE, StairsShapePropFormat.INSTANCE
            // Add more as needed...
        ))
    ),
    Map.of(BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()),
    Set.of(CompressionEngine.raw(), CompressionEngine.gzip()),
    CompressionType.GZIP.getId(),   // compression for block types
    CompressionType.GZIP.getId(),   // compression for block index
    new SpigotSchematicFactory()
);
```

> For simple structures that use only basic blocks without block states (e.g. stone, dirt, planks),
> pass `Map.of()` as the prop format map.

### Saving to a File {id="save-file"}

```java
Path path = Path.of("plugins/MyPlugin/schematics/house.cbschem");
Files.createDirectories(path.getParent());

try (SeekableOutputStream out = SeekableOutputStream.open(path)) {
    format.write(schematic, out);
}
```

### Saving to Bytes (In-Memory) {id="save-bytes"}

Useful for caching, network transfer, or database storage:

```java
ByteArraySeekableChannel channel = new ByteArraySeekableChannel();
format.write(schematic, new SeekableOutputStream(channel));

byte[] bytes = channel.toByteArray();  // hand off to wherever you need them
```

### Loading from a File {id="load-file"}

```java
Path path = Path.of("plugins/MyPlugin/schematics/house.cbschem");

try (SeekableInputStream in = SeekableInputStream.open(path)) {
    SpigotSchematic schematic = (SpigotSchematic) format.read(in);
    // schematic is ready to use
}
```

### Loading from Bytes {id="load-bytes"}

```java
byte[] bytes = // ... from database / network / cache

SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(bytes));
SpigotSchematic schematic = (SpigotSchematic) format.read(in);
```

### Full Round-Trip Example {id="round-trip"}

Capture a region, save to disk, reload later, and paste it:

```java
// ── 1. Capture ──────────────────────────────────────────────────────────────
Position origin = Locations.toPosition(player.getLocation()).floor();
Position pos0   = origin.clone().add( 10,  5,  10);
Position pos1   = origin.clone().add(-10, -5, -10);

SpigotSchematic captured = SpigotSchematicHelper.load(
    "house", player.getName(),
    origin, player.getWorld(),
    pos0, pos1,
    SpigotSchematicPlacer.getInstance()
);

// ── 2. Save ─────────────────────────────────────────────────────────────────
Path file = Path.of("plugins/MyPlugin/schematics/house.cbschem");
Files.createDirectories(file.getParent());
try (SeekableOutputStream out = SeekableOutputStream.open(file)) {
    format.write(captured, out);
}

// ── 3. Reload (e.g. on server restart) ──────────────────────────────────────
SpigotSchematic loaded;
try (SeekableInputStream in = SeekableInputStream.open(file)) {
    loaded = (SpigotSchematic) format.read(in);
}

// ── 4. Paste ─────────────────────────────────────────────────────────────────
PasteResult result = loaded.paste(player.getLocation()).performAll();
player.sendMessage("Placed " + result.blockPlaces() + " blocks!");
```

## Pasting Schematics

### Basic Pasting {id="paste-basic"}

The simplest paste operation places all non-air blocks at the given location:

```java
Location origin = player.getLocation();
PasteResult result = schematic.paste(origin).performAll();

player.sendMessage("Placed " + result.blockPlaces() + " blocks");
```

> By default, blocks only replace air.

### Incremental Pasting {id="paste-incremental"}

For large schematics, paste gradually to avoid lag:

```java
SpigotPasteOperation operation = schematic.paste(origin);

new BukkitRunnable() {
    @Override
    public void run() {
        if (!operation.hasNext()) {
            this.cancel();
            player.sendMessage("Paste complete!");
            return;
        }

        // Paste up to 100 blocks per tick
        operation.advanceAllAxis(100);
    }
}.runTaskTimer(plugin, 0L, 1L);
```

### Axis Order Control {id="paste-axis"}

Control placement direction for visual effect:

```java
// Bottom-to-top (tall structures look best)
schematic.paste(origin, AxisOrder.YXZ).performAll();

// One full Y-layer at a time
SpigotPasteOperation operation = schematic.paste(origin, AxisOrder.YXZ);
while (operation.hasNext()) {
    operation.performAllOnDualAxis();   // advances one Y-layer
    // wait a tick, play a sound, etc.
}

// One line at a time (finest granularity)
while (operation.hasNext()) {
    operation.performAllOnSingleAxis();
}
```

## Advanced Placement Control

### Placement Filters {id="filters"}

Control which world blocks are overwritten via `setShouldPlace`:

<tabs>
<tab title="Replace Everything">

```java
SpigotPasteOperation op = schematic.paste(origin);
op.setShouldPlace((block, placement) -> true);
op.performAll();
```

</tab>
<tab title="Only Air">

```java
SpigotPasteOperation op = schematic.paste(origin);
op.setShouldPlace((block, placement) -> block.getType() == Material.AIR);
op.performAll();
```

</tab>
<tab title="Replace Liquids">

```java
SpigotPasteOperation op = schematic.paste(origin);
op.setShouldPlace((block, placement) -> {
    Material type = block.getType();
    return type == Material.WATER || type == Material.LAVA;
});
op.performAll();
```

</tab>
<tab title="Height Restriction">

```java
SpigotPasteOperation op = schematic.paste(origin);
op.setShouldPlace((block, placement) -> block.getY() < 64);
op.performAll();
```

</tab>
</tabs>

### Block Mapping {id="mapping"}

Transform block data before placement via `setMapper`:

```java
BlockData diamond = new GenericBlockData(
    new NamespacedKey("minecraft", "diamond_block"), Map.of());

SpigotPasteOperation op = schematic.paste(origin);

// Swap every stone block for diamond
op.setMapper(placement -> {
    if (placement.block().type().key().equals("stone")) {
        return diamond;
    }
    return placement.block();
});

op.performAll();
```

### Post-Placement Actions {id="post-actions"}

Execute code after each block is placed:

```java
SpigotPasteOperation operation = schematic.paste(origin);

// Spawn particles at every placed block
operation.addPostPlaceAction((block, blockData) ->
    block.getWorld().spawnParticle(
        Particle.CLOUD,
        block.getLocation().add(0.5, 0.5, 0.5),
        5
    )
);

// Log each placement
operation.addPostPlaceAction((block, blockData) ->
    plugin.getLogger().info(
        "Placed " + blockData.type().key() + " at " + block.getLocation()
    )
);

operation.performAll();
```

Multiple actions are executed in the order they were registered.

### Dynamic Configuration {id="dynamic"}

Filters and mappers can be swapped mid-operation:

```java
SpigotPasteOperation operation = schematic.paste(origin);

// Paste first 50 blocks normally
operation.advanceAllAxis(50);

// Switch to stone-only from here on
BlockData stone = new GenericBlockData(new NamespacedKey("minecraft", "stone"), Map.of());
operation.setMapper(p -> stone);

// Also skip blocks that are not air
operation.setShouldPlace((block, p) -> block.getType() == Material.AIR);

operation.performAll();
```

## Complete Examples

### Example 1: Capture, Save, and Load {id="example-save-load"}

Plugin command that captures a selection and saves it to disk:

```java
public void onSaveCommand(Player player, String name) {
    SchematicSettings settings = getSettings(player);
    if (settings == null) {
        player.sendMessage("§cSelect a region first!");
        return;
    }

    SpigotSchematic schematic = SpigotSchematicHelper.load(
        name,
        player.getName(),
        Locations.toPosition(player.getLocation()).floor(),
        player.getWorld(),
        settings.getPos0(),
        settings.getPos1(),
        SpigotSchematicPlacer.getInstance()
    );

    if (schematic == null) {
        player.sendMessage("§cFailed to read region!");
        return;
    }

    Path file = dataFolder.toPath().resolve(name + ".cbschem");
    try (SeekableOutputStream out = SeekableOutputStream.open(file)) {
        format.write(schematic, out);
        player.sendMessage("§aSaved §e" + name + "§a to disk.");
    } catch (IOException e) {
        player.sendMessage("§cFailed to save: " + e.getMessage());
    }
}

public SpigotSchematic loadFromDisk(String name) throws IOException {
    Path file = dataFolder.toPath().resolve(name + ".cbschem");
    try (SeekableInputStream in = SeekableInputStream.open(file)) {
        return (SpigotSchematic) format.read(in);
    }
}
```

### Example 2: Async Paste with Progress {id="example-async"}

Show progress while pasting a large schematic:

```java
public void pasteWithProgress(Plugin plugin, Player player, SpigotSchematic schematic) {
    Location origin = player.getLocation();
    SpigotPasteOperation operation = schematic.paste(origin);

    AreaSize size = schematic.size();
    long totalVolume = (long) (size.width() * size.height() * size.depth());
    AtomicLong placed = new AtomicLong(0);

    new BukkitRunnable() {
        @Override
        public void run() {
            if (!operation.hasNext()) {
                this.cancel();
                player.sendMessage("§aComplete! Placed " + placed.get() + " blocks.");
                return;
            }

            PasteResult result = operation.advanceAllAxis(150);
            placed.addAndGet(result.blockPlaces());

            double progress = Math.min(placed.get() * 100.0 / totalVolume, 100.0);
            player.sendActionBar(Component.text(
                String.format("Pasting: %.1f%%", progress),
                NamedTextColor.YELLOW
            ));
        }
    }.runTaskTimer(plugin, 0L, 1L);
}
```

### Example 3: Layer-by-Layer with Sound Effects {id="example-layers"}

Paste one Y-layer per tick with a sound:

```java
public void pasteLayerByLayer(Plugin plugin, Location origin, SpigotSchematic schematic) {
    SpigotPasteOperation operation = schematic.paste(origin, AxisOrder.YXZ);

    operation.addPostPlaceAction((block, data) ->
        block.getWorld().spawnParticle(
            Particle.VILLAGER_HAPPY,
            block.getLocation().add(0.5, 0.5, 0.5),
            3, 0.2, 0.2, 0.2, 0
        )
    );

    new BukkitRunnable() {
        @Override
        public void run() {
            if (!operation.hasNext()) {
                this.cancel();
                return;
            }

            operation.performAllOnDualAxis();   // one complete Y-layer

            origin.getWorld().playSound(
                origin,
                Sound.BLOCK_STONE_PLACE,
                0.5f, 1.2f
            );
        }
    }.runTaskTimer(plugin, 0L, 2L);
}
```

### Example 4: Block Substitution on Paste {id="example-substitute"}

Replace worn materials when pasting a renovation schematic:

```java
public void pasteRenovation(Location origin, SpigotSchematic schematic) {
    BlockData newBricks = new GenericBlockData(
        new NamespacedKey("minecraft", "stone_bricks"), Map.of());

    SpigotPasteOperation op = schematic.paste(origin, AxisOrder.YXZ);

    // Only overwrite "old" blocks
    op.setShouldPlace((block, placement) -> {
        Material type = block.getType();
        return type == Material.COBBLESTONE
            || type == Material.MOSSY_COBBLESTONE
            || type == Material.CRACKED_STONE_BRICKS;
    });

    // Upgrade to fresh stone bricks
    op.setMapper(placement -> {
        String key = placement.block().type().key();
        if (key.equals("cobblestone") || key.equals("mossy_cobblestone")) {
            return newBricks;
        }
        return placement.block();
    });

    op.performAll();
}
```

## API Reference {id="api"}

### Core Classes

**`Schematic<T>`** — Represents an immutable 3D schematic
- `size()` — `AreaSize` with `width()`, `height()`, `depth()`
- `metadata()` — `SchematicMetadata` with `title()` and `author()`
- `offset()` — Origin offset as `Position`
- `axisOrder()` — Block storage order
- `getBlockData(x, y, z)` — Block at relative position (`null` = air)
- `blocksIterator()` — Iterate all non-air blocks
- `toBuilder()` — Create a mutable copy

**`SchematicBuilder<T>`** — Fluent schematic builder
- `metadata(Function<SchematicMetadataBuilder, SchematicMetadata>)` — Set title, author, custom keys
- `size(AreaSize)` — Set bounding box
- `setBlock(x, y, z, BlockData)` — Place a block
- `removeBlock(x, y, z)` — Remove a block
- `rotate(90|180|270)` — Rotate content
- `flip(Axis)` — Mirror content
- `translate(Position)` — Change paste offset
- `shift(Axis, int)` — Shift all blocks
- `build()` — Produce the schematic

**`CocoaSchematicFormat<T>`** — Serializes/deserializes `.cbschem` files
- `write(schematic, SeekableOutputStream)` — Write schematic
- `read(SeekableInputStream)` — Read schematic

**`PasteOperation`** — Stateful paste iterator
- `hasNext()` — Whether more blocks remain
- `performAll()` — Place everything at once
- `advanceAllAxis(n)` — Place up to *n* blocks
- `performAllOnDualAxis()` — Place one layer (two axes fixed)
- `performAllOnSingleAxis()` — Place one line (one axis fixed)

**`PasteResult`** — Outcome of a paste step
- `blockPlaces()` — Number of blocks placed
- `leftOver()` — Remaining quota (when using `advanceAllAxis`)

### Spigot Classes

**`SpigotSchematic`** — Spigot implementation of `Schematic`
- `paste(Location)` — Create paste operation (replaces air only)
- `paste(Location, AxisOrder)` — With explicit axis order

**`SpigotPasteOperation`** — Spigot paste operation
- `setShouldPlace(BiPredicate<Block, BlockPlacement>)` — Override placement filter
- `setMapper(Function<BlockPlacement, BlockData>)` — Override block mapper
- `addPostPlaceAction(BiConsumer<Block, BlockData>)` — Add callback
- `origin()` — Location where the paste started

**`SpigotSchematicHelper`** — Captures schematics from the world
- `load(title, author, playerPos, world, pos0, pos1, placer)` — Extract region

**`SeekableOutputStream`** — Writable seekable channel
- `SeekableOutputStream.open(Path)` — Open/create a file

**`SeekableInputStream`** — Readable seekable channel
- `SeekableInputStream.open(Path)` — Open an existing file

**`ByteArraySeekableChannel`** — In-memory seekable channel
- `ByteArraySeekableChannel()` — Empty, for writing
- `ByteArraySeekableChannel.of(byte[])` — Pre-filled, for reading
- `toByteArray()` — Extract written bytes

## Performance Tips {id="performance"}

1. **Use incremental pasting** for large schematics — 100–200 blocks per tick is a safe budget
2. **Choose axis order wisely** — how you would like to build the structure
3. **Minimise post-placement actions** — they run per block; keep them cheap
4. **Re-use `CocoaSchematicFormat`** — constructing it is expensive; create one instance at plugin startup
5. **Save with GZIP compression** (default) for disk storage; consider `CompressionType.RAW` for hot in-memory caches where CPU matters more than size

---

**Version**: 0.0.46
**Repository**: [GitHub](https://github.com/PoweredByApartium/cocoa-beans)
