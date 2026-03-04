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

<code-block lang="java" src="schematic-spigot/CodeSnippetsTest.java" include-symbol="quickStart"/>

## Building Schematics

### Creating from Scratch {id="create-scratch"}

Build schematics programmatically block by block using `SpigotSchematicBuilder`:

<code-block lang="java" src="schematic-spigot/CodeSnippetsTest.java" include-symbol="createSchematicFromScratch"/>


### Capturing from the World {id="capture-world"}

Extract a region from a live Bukkit world using `SpigotSchematicHelper`:

<code-block lang="java" src="schematic-spigot/CodeSnippetsTest.java" include-symbol="captureFromWorld"/>

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

<code-block lang="java" src="schematic-spigot/CodeSnippetsTest.java" include-lines="157-178"/>

`CocoaSchematicFormat` handles reading and writing `.cbschem` files. Create one instance and reuse it:

> For simple structures that use only basic blocks without block states (e.g. stone, dirt, planks),
> pass `Map.of()` as the prop format map.

### Saving to a File {id="save-file"}

<tabs>
    <tab title="saveToFile()">
        <code-block lang="java" src="schematic-spigot/CodeSnippetsTest.java" include-symbol="saveToFile"/>
    </tab>
    <tab title="getSchematicDirectory()">
        <code-block lang="java" src="schematic-spigot/CodeSnippetsTest.java" include-symbol="getSchematicDirectory"/>
    </tab>
</tabs>

### Saving to Bytes (In-Memory) {id="save-bytes"}

Useful for caching, network transfer, or database storage:

<code-block lang="java" src="schematic-spigot/CodeSnippetsTest.java" include-symbol="saveToBytes"/>

### Loading from a File {id="load-file"}

<code-block lang="java" src="schematic-spigot/CodeSnippetsTest.java" include-symbol="loadFromFile"/>

### Loading from Bytes {id="load-bytes"}

<code-block lang="java" src="schematic-spigot/CodeSnippetsTest.java" include-symbol="loadFromBytes"/>

### Full Round-Trip Example {id="round-trip"}

Capture a region, save to disk, reload later, and paste it:

<code-block lang="java" src="schematic-spigot/CodeSnippetsTest.java" include-symbol="roundTrip"/>

## Pasting Schematics

### Basic Pasting {id="paste-basic"}

The simplest paste operation places all non-air blocks at the given location:

<code-block lang="java" src="schematic-spigot/CodeSnippetsTest.java" include-lines="344-347"/>

> By default, blocks only replace air.

### Incremental Pasting {id="paste-incremental"}

<code-block lang="java" src="schematic-spigot/CodeSnippetsTest.java" include-lines="355-369"/>

For large schematics, paste gradually to avoid lag:


### Axis Order Control {id="paste-axis"}

Control placement direction for visual effect:

<code-block lang="java" src="schematic-spigot/CodeSnippetsTest.java" include-symbol="axisOrderControl"/>

## Advanced Placement Control

### Placement Filters {id="filters"}

Control which world blocks are overwritten via `setShouldPlace`:

<tabs>
<tab title="Replace Everything">

<code-block lang="java" src="schematic-spigot/CodeSnippetsTest.java" include-lines="379-381"/>

</tab>
<tab title="Only Air">

<code-block lang="java" src="schematic-spigot/CodeSnippetsTest.java" include-lines="384-386"/>

</tab>
<tab title="Replace Liquids">

<code-block lang="java" src="schematic-spigot/CodeSnippetsTest.java" include-lines="389-394"/>

</tab>
<tab title="Height Restriction">

<code-block lang="java" src="schematic-spigot/CodeSnippetsTest.java" include-lines="397-399"/>
</tab>
</tabs>

### Block Mapping {id="mapping"}

Transform block data before placement via `setMapper`:

<code-block lang="java" src="schematic-spigot/CodeSnippetsTest.java" include-symbol="blockMapping"/>

### Post-Placement Actions {id="post-actions"}

Execute code after each block is placed:

<code-block lang="java" src="schematic-spigot/CodeSnippetsTest.java" include-lines="453-471"/>

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
