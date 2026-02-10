# 🏗 Schematic

A schematic represents a snapshot of blocks in a three-dimensional space that can be stored, transformed, and pasted efficiently across Minecraft platforms.

In Cocoa-Beans, schematics are designed as a low-level, format-aware, platform-agnostic API, built to solve long-standing pain points in existing solutions such as the WorldEdit API.

## Why another schematic API?
Most existing schematic systems focus on immediate world mutation:

* tightly coupled to a specific server platform
* expensive to transform (rotate / flip / reorder)
* hard to stream, paginate, or partially paste
* opaque file formats that are difficult to extend

Cocoa-Beans takes a different approach.

### Design goals

- **Platform agnostic core**  
  A schematic is *data*, not a world operation.

- **Separation of concerns**  
  Reading, writing, transforming, and pasting are independent steps.

- **Deterministic iteration order**  
  Axis order is explicit and configurable.

- **Efficient storage**  
  Block data and block indexes are stored separately, compressed independently, and validated via checksums.

- **Streaming-friendly pasting**  
  Paste operations can be advanced incrementally, not only “all at once”.

---


## What is a `Schematic`?

At its core, a `Schematic` is an **immutable 3D structure** with metadata and a defined coordinate system.

```java
public interface Schematic {
    MinecraftPlatform originPlatform();
    Instant created();
    SchematicMetadata metadata();

    Position offset();
    AreaSize size();
    AxisOrder axisOrder();

    @Nullable BlockData getBlockData(int x, int y, int z);

    BlockIterator blocksIterator();
    BlockIterator sortedIterator(AxisOrder axisOrder);

    SchematicBuilder<?> toBuilder();
}
```