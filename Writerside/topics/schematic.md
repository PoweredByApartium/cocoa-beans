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

Build schematics programmatically block by block:

```java
SpigotSchematic schematic = factory.createSchematic()
    .platform(MinecraftPlatform.SPIGOT)
    .metadata(meta -> meta
        .put("name", "MyStructure")
        .put("author", "PlayerName")
        .build())
    .setBlock(0, 0, 0, SpigotBlockData.of(Material.STONE))
    .setBlock(1, 0, 0, SpigotBlockData.of(Material.DIRT))
    .setBlock(2, 0, 0, SpigotBlockData.of(Material.GRASS_BLOCK))
    .build();
```

### Loading from Files {id="load-files"}

Load existing schematics in Cocoa format:

```java
import net.apartium.cocoabeans.schematic.format.CocoaSchematicFormat;
import net.apartium.cocoabeans.seekable.SeekableFileInputStream;

File schematicFile = new File("schematics/castle.cbschem");
CocoaSchematicFormat format = new CocoaSchematicFormat(
        // todo api to get stuff for current version
                Map.of(
                        SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of(
                                BlockProp.Legacy.DATA, new ByteBlockPropFormat(LegacyDataProp::new),
                                BlockProp.Legacy.SIGN_LINES, ListStringBlockPropFormat.INSTANCE)
                        )
                ),
                Map.of(
                        BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()
                ),
                Set.of(
                        CompressionEngine.raw(), CompressionEngine.gzip()
                ),
                CompressionType.GZIP.getId(), // compression for blocks
                CompressionType.GZIP.getId(), // compression for indexes
                new SpigotSchematicFactory()
        );


try (SeekableFileInputStream input = new SeekableFileInputStream(schematicFile)) {
    SpigotSchematic schematic = (SpigotSchematic) format.read(input);
    // Use schematic...
}
```

### Transforming Schematics {id="transform"}

Use `toBuilder()` to modify existing schematics:

```java
SpigotSchematic rotated = schematic.toBuilder()
    .rotate(90)                        // Rotate 90 degrees
    .flip(Axis.X)                      // Mirror along X axis
    .translate(new Position(5, 0, 5))  // Move offset
    .build();
```

Available transformations:
- `rotate(90|180|270)` - Rotate schematic
- `flip(Axis)` - Mirror along axis
- `translate(Position)` - Change origin
- `shift(Axis, int)` - Move blocks

## Pasting Schematics

### Basic Pasting {id="paste-basic"}

The simplest paste operation:

```java
Location origin = player.getLocation();
PasteResult result = schematic.paste(origin).performAll();

player.sendMessage("Placed " + result.placed() + " blocks");
```

> By default, blocks only replace air.

### Incremental Pasting {id="paste-incremental"}

Paste gradually to avoid lag:

```java
SpigotPasteOperation operation = schematic.paste(origin);

new BukkitRunnable() {
    @Override
    public void run() {
        if (!operation.hasNext()) {
            this.cancel();
            player.sendMessage("Complete!");
            return;
        }
        
        // Paste 100 blocks per tick
        operation.advanceAllAxis(100);
    }
}.runTaskTimer(plugin, 0L, 1L);
```

### Axis Order Control {id="paste-axis"}

Control placement direction:

```java
// Bottom-to-top
schematic.paste(origin, AxisOrder.YXZ).performAll();

// Layer by layer (one X-Y plane at a time)
while (operation.hasNext()) {
    operation.performAllOnDualAxis();
    // Wait or do other work
}

// Line by line (one X line at a time)
while (operation.hasNext()) {
    operation.performAllOnSingleAxis();
    // Wait or do other work
}
```

## Advanced Placement Control

### Placement Filters {id="filters"}

Control which blocks to place using predicates:

<tabs>
<tab title="Replace Everything">

```java
schematic.paste(origin, AxisOrder.XYZ,
    (block, placement) -> true  // Replace all blocks
).performAll();
```

</tab>
<tab title="Only Air">

```java
schematic.paste(origin, AxisOrder.XYZ,
    (block, placement) -> block.getType() == Material.AIR
).performAll();
```

</tab>
<tab title="Replace Liquids">

```java
schematic.paste(origin, AxisOrder.XYZ,
    (block, placement) -> {
        Material type = block.getType();
        return type == Material.WATER || type == Material.LAVA;
    }
).performAll();
```

</tab>
<tab title="Height Restriction">

```java
schematic.paste(origin, AxisOrder.XYZ,
    (block, placement) -> block.getY() < 64
).performAll();
```

</tab>
</tabs>

### Block Mapping {id="mapping"}

Transform blocks before placement:

```java
schematic.paste(
    origin,
    AxisOrder.XYZ,
    (block, placement) -> true,
    (placement) -> {
        BlockData data = placement.block();
        
        // Replace stone with diamond blocks
        if (data.material().equals("STONE")) {
            return SpigotBlockData.of(Material.DIAMOND_BLOCK);
        }
        
        return data;
    }
).performAll();
```

### Post-Placement Actions {id="post-actions"}

Execute code after each block is placed:

```java
SpigotPasteOperation operation = schematic.paste(origin);

// Spawn particles
operation.addPostPlaceAction((block, blockData) -> {
    block.getWorld().spawnParticle(
        Particle.CLOUD,
        block.getLocation().add(0.5, 0.5, 0.5),
        5
    );
});

// Log placement
operation.addPostPlaceAction((block, blockData) -> {
    plugin.getLogger().info("Placed " + blockData.material() + 
                           " at " + block.getLocation());
});

operation.performAll();
```

### Dynamic Configuration {id="dynamic"}

Change settings mid-operation:

```java
SpigotPasteOperation operation = schematic.paste(origin);

// Start pasting
operation.advanceAllAxis(50);

// Change filter
operation.setShouldPlace((block, placement) -> 
    block.getY() < 100  // Now only below Y=100
);

// Change mapper
operation.setMapper(placement -> 
    SpigotBlockData.of(Material.STONE)  // Now everything is stone
);

// Continue pasting with new settings
operation.performAll();
```

## Complete Examples

### Example 1: Simple Structure {id="example-simple"}

Create and paste a small structure:

```java
public void createSimpleHouse(Player player) {
    SpigotSchematicFactory factory = new SpigotSchematicFactory();
    
    // Build a 5x5x5 house
    SpigotSchematic house = factory.createSchematic()
        .platform(MinecraftPlatform.SPIGOT)
        .metadata(meta -> meta.put("name", "Simple House").build())
        // Floor
        .setBlock(0, 0, 0, SpigotBlockData.of(Material.OAK_PLANKS))
        .setBlock(1, 0, 0, SpigotBlockData.of(Material.OAK_PLANKS))
        // Add more blocks...
        .build();
    
    // Paste at player location
    house.paste(player.getLocation()).performAll();
    player.sendMessage("House created!");
}
```

### Example 2: Async Pasting with Progress {id="example-async"}

Paste large schematics with progress updates:

```java
public void pasteWithProgress(Plugin plugin, Player player, SpigotSchematic schematic) {
    Location origin = player.getLocation();
    SpigotPasteOperation operation = schematic.paste(origin);
    
    // Calculate total blocks
    AreaSize size = schematic.size();
    long total = size.getWidth() * size.getHeight() * size.getLength();
    AtomicLong placed = new AtomicLong(0);
    
    new BukkitRunnable() {
        @Override
        public void run() {
            if (!operation.hasNext()) {
                this.cancel();
                player.sendMessage("§aComplete! Placed " + placed.get() + " blocks");
                return;
            }
            
            PasteResult result = operation.advanceAllAxis(150);
            placed.addAndGet(result.placed());
            
            // Update progress
            double progress = (placed.get() * 100.0) / total;
            player.sendActionBar(String.format("§7Pasting: §e%.1f%%", progress));
        }
    }.runTaskTimer(plugin, 0L, 1L);
}
```

### Example 3: Conditional Replacement {id="example-conditional"}

Replace only specific blocks in the world:

```java
public void replaceOldBlocks(Location origin, SpigotSchematic schematic) {
    schematic.paste(
        origin,
        AxisOrder.YXZ,  // Bottom to top
        (block, placement) -> {
            Material current = block.getType();
            // Only replace old-looking blocks
            return current == Material.COBBLESTONE ||
                   current == Material.MOSSY_COBBLESTONE ||
                   current == Material.STONE_BRICKS;
        },
        (placement) -> {
            // Map old materials to new ones
            String material = placement.block().material();
            if (material.equals("COBBLESTONE")) {
                return SpigotBlockData.of(Material.STONE_BRICKS);
            }
            return placement.block();
        }
    ).performAll();
}
```

### Example 4: Layer-by-Layer with Effects {id="example-layers"}

Paste one layer at a time with visual effects:

```java
public void pasteWithEffects(Plugin plugin, Location origin, SpigotSchematic schematic) {
    SpigotPasteOperation operation = schematic.paste(origin, AxisOrder.YXZ);
    
    // Add particle effect to each block
    operation.addPostPlaceAction((block, data) -> {
        block.getWorld().spawnParticle(
            Particle.VILLAGER_HAPPY,
            block.getLocation().add(0.5, 0.5, 0.5),
            3, 0.2, 0.2, 0.2, 0
        );
    });
    
    // Paste one Y-layer per tick
    new BukkitRunnable() {
        @Override
        public void run() {
            if (!operation.hasNext()) {
                this.cancel();
                return;
            }
            
            // Paste entire layer
            PasteResult result = operation.performAllOnDualAxis();
            
            // Play sound
            origin.getWorld().playSound(
                origin, 
                Sound.BLOCK_STONE_PLACE, 
                0.5f, 
                1.2f
            );
        }
    }.runTaskTimer(plugin, 0L, 2L);  // Every 2 ticks
}
```

## API Reference {id="api"}

### Core Classes

**`Schematic<T>`** - Represents a 3D schematic
- `size()` - Get dimensions
- `metadata()` - Get custom metadata
- `getBlockData(x, y, z)` - Get block at position
- `toBuilder()` - Modify schematic

**`SchematicBuilder<T>`** - Build schematics
- `platform(platform)` - Set platform
- `setBlock(x, y, z, data)` - Place block
- `rotate(degrees)` - Transform
- `build()` - Create schematic

**`PasteOperation`** - Control pasting
- `hasNext()` - Check if more blocks remain
- `performAll()` - Paste everything
- `advanceAllAxis(n)` - Paste n blocks
- `performAllOnDualAxis()` - Paste one layer
- `performAllOnSingleAxis()` - Paste one line

### Spigot Classes

**`SpigotSchematic`** - Spigot implementation
- `paste(origin)` - Simple paste
- `paste(origin, axisOrder, filter, mapper)` - Full control

**`SpigotPasteOperation`** - Spigot paste operation
- `setShouldPlace(predicate)` - Change filter
- `setMapper(function)` - Change mapper
- `addPostPlaceAction(action)` - Add callback

**`PasteResult`** - Paste outcome
- `placed()` - Blocks placed count
- `remaining()` - Blocks skipped count

## Performance Tips {id="performance"}

1. **Use incremental pasting** for large schematics (100-200 blocks per tick)
2. **Choose axis order wisely** - `YXZ` for tall structures, `XZY` for wide ones
3. **Limit post-placement actions** - they run for every block
4. **Pre-calculate estimates** to show progress accurately

---

**Version**: 0.0.46+  
**Repository**: [GitHub](https://github.com/PoweredByApartium/cocoa-beans)
