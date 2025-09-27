package net.apartium.cocoabeans.space.schematic.format;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.schematic.BlockData;
import net.apartium.cocoabeans.space.schematic.BlockDataEncoder;
import net.apartium.cocoabeans.space.schematic.Dimensions;
import net.apartium.cocoabeans.space.schematic.Schematic;
import net.apartium.cocoabeans.space.schematic.axis.Axis;
import net.apartium.cocoabeans.space.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.space.schematic.compression.CompressionEngine;
import net.apartium.cocoabeans.space.schematic.compression.CompressionType;
import net.apartium.cocoabeans.space.schematic.utils.SeekableInputStream;
import net.apartium.cocoabeans.space.schematic.utils.SeekableOutputStream;
import net.apartium.cocoabeans.structs.Entry;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.apartium.cocoabeans.space.schematic.utils.FileUtils.*;

public class CocoaSchematicFormat implements SchematicFormat {

    public static final String FINGERPRINT = "CBSC";
    public static final int FINGERPRINT_SIZE = 4;
    public final int VERSION = 1;
    public static final long HEADER_START_INDEX = 9;

    public static final int L1_SIZE = 256;
    public static final int L2_SIZE = 64;
    public static final int L3_SIZE = 16;
    public static final int L4_SIZE = 4;

    public static class Headers {

        public static final int ID = 0x01;
        public static final int AXIS_ORDER = 0x02;
        public static final int OFFSET = 0x03;
        public static final int SIZE = 0x04;
        public static final int BLOCK_DATA_ENCODING = 0x05;
        public static final int BLOCK_INFO = 0x06;
        public static final int INDEX_INFO = 0x07;
        public static final int TIMESTAMP = 0x08;
        public static final int AUTHOR = 0x09;
        public static final int TITLE = 0x0A;

    }

    private final Map<Integer, BlockDataEncoder> blockDataEncoderMap;
    private CompressionEngine compressionEngine;
    private CompressionType compressionTypeForBlocks = CompressionType.GZIP;
    private CompressionType compressionTypeForIndexes = CompressionType.RAW;
    private Map.Entry<Integer, BlockDataEncoder> preferEncoder;

    public CocoaSchematicFormat(Map<Integer, BlockDataEncoder> blockDataEncoderMap, CompressionEngine compressionEngine) {
        this.blockDataEncoderMap = new HashMap<>(blockDataEncoderMap);
        this.compressionEngine = compressionEngine;
        if (!blockDataEncoderMap.isEmpty())
            preferEncoder = blockDataEncoderMap.entrySet().iterator().next();
    }

    public void registerBlockEncoder(int id, BlockDataEncoder encoder) {
        blockDataEncoderMap.put(id, encoder);
    }

    public void setPreferEncoder(int id, BlockDataEncoder encoder) {
        preferEncoder = Map.entry(id, encoder);
    }

    public void setCompressionEngine(CompressionEngine compressionEngine) {
        this.compressionEngine = compressionEngine;
    }

    public void setCompressionTypeForBlocks(CompressionType compressionTypeForBlocks) {
        this.compressionTypeForBlocks = compressionTypeForBlocks;
    }

    public void setCompressionTypeForIndexes(CompressionType compressionTypeForIndexes) {
        this.compressionTypeForIndexes = compressionTypeForIndexes;
    }

    @Override
    public void write(Schematic schematic, SeekableOutputStream out) {
        try {
            for (int i = 0; i < FINGERPRINT_SIZE; i++)
                out.write(FINGERPRINT.charAt(i));

            out.write(writeU16(VERSION));


            HeaderResult headerResult = headers(schematic);
            byte[] headers = headerResult.data;
            out.write(writeU24(headers.length));
            out.write(headers);

            Iterator<Entry<Position, BlockData>> iterator = schematic.blocksIterator();
            Map<Position, Long> blockIndexes = new HashMap<>();
            ByteArrayOutputStream blockOut = new ByteArrayOutputStream();
            long offset = 0;
            while (iterator.hasNext()) {
                Entry<Position, BlockData> entry = iterator.next();
                Position position = entry.key();
                BlockData blockData = entry.value();

                if (blockData == null)
                    continue;

                blockIndexes.put(position, offset);
                byte[] data = preferEncoder.getValue().write(blockData);
                blockOut.write(data);
                offset += data.length;
            }

            byte[] originalBlocksData = blockOut.toByteArray();
            byte[] compressed = compressionEngine.compress(compressionTypeForBlocks, originalBlocksData);

            System.out.println("before: " + originalBlocksData.length);
            System.out.println("after: " + compressed.length);
            System.out.println(Arrays.equals(originalBlocksData, compressionEngine.decompress(compressionTypeForBlocks, compressed)));

            CompressionBlockInfo blockInfo = new CompressionBlockInfo(
                    compressionTypeForBlocks,
                    originalBlocksData.length,
                    compressed.length,
                    out.position(),
                    crc32(originalBlocksData)
            );

            long blockStartIndex = HEADER_START_INDEX + headerResult.offsetBlockInfo;
            out.position(blockStartIndex);
            out.write(blockInfo.toByteArray());

            out.position(blockInfo.offset());
            out.write(compressed);

            long indexesStartIndex = HEADER_START_INDEX + headerResult.offsetIndexInfo;
            final AxisOrder axes = schematic.axisOrder();
            List<Map.Entry<Position, Long>> indexes = blockIndexes.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(axes))
                    .toList();

            Dimensions dimensions = schematic.size();

            Dimensions l1Size = splitToChunks(dimensions, L1_SIZE);

            ChunkResult l1Chunks = new ChunkResult(
                    toSimpleArray(splitToChunks(indexes.stream()
                        .map(entry -> new ChunkPointer(entry.getKey(), entry.getValue(), null))
                        .toList(), Position.ZERO, axes, l1Size, L1_SIZE), l1Size, axes, l1Size.toArraySize(), null),
                    Position.ZERO,
                    Dimensions.box(L1_SIZE),
                    1,
                    null
            );


            Map<ChunkResult, Map<ChunkResult, ChunkResult[]>> bridgeFromL2ToL4 = new IdentityHashMap<>();
            Map<ChunkResult, ChunkResult[]> bridgeL2toL3 = new IdentityHashMap<>();

            ChunkResult[] l2 = compact(l1Chunks, axes, L2_SIZE, 64);

            for (ChunkResult chunkResult : l2) {
                if (chunkResult == null)
                    continue;

                ChunkResult[] l3 = compact(chunkResult, axes, L3_SIZE, 64);
                bridgeL2toL3.put(chunkResult, l3);

                for (ChunkResult result : l3) {
                    if (result == null)
                        continue;

                    ChunkResult[] l4 = compact(result, axes, L4_SIZE, 64);
                    bridgeFromL2ToL4.computeIfAbsent(chunkResult, key -> new IdentityHashMap<>())
                            .put(result, l4);
                }
            }


            // DEBUG
            AtomicInteger counter = new AtomicInteger(0);
            bridgeFromL2ToL4.values().stream().map(Map::values).forEach(list -> list.forEach(result -> {
                for (ChunkResult r : result) {
                    if (r == null)
                        continue;

                    for (List<ChunkPointer> chunk : r.chunks) {
                        if (chunk == null)
                            continue;
                        for (ChunkPointer pointer : chunk) {
                            if (pointer == null)
                                continue;

                            counter.incrementAndGet();
                        }
                    }
                }
            }));

            System.out.println("counter: " + counter.get()); // 52

            List<ChunkResult> l4 = bridgeFromL2ToL4.values().stream()
                    .filter(Objects::nonNull)
                    .flatMap(m -> m.values().stream())
                    .filter(Objects::nonNull)
                    .flatMap(Arrays::stream)
                    .filter(Objects::nonNull)
                    .toList();

            List<ChunkPointer> l4Pointers = bridgeFromL2ToL4.values().stream()
                    .filter(Objects::nonNull)
                    .flatMap(m -> m.values().stream())
                    .filter(Objects::nonNull)
                    .flatMap(Arrays::stream)
                    .filter(Objects::nonNull)
                    .flatMap(r -> {
                        List<ChunkPointer>[] chunks = r.chunks;    // or r.getChunks()
                        return chunks == null
                                ? Stream.empty()
                                : Arrays.stream(chunks)
                                .filter(Objects::nonNull)
                                .flatMap(Collection::stream)
                                .filter(Objects::nonNull);
                    })
                    .toList();


            Set<ChunkResult> l3 = l4Pointers.stream()
                    .map(ChunkPointer::prev)
                    .filter(Objects::nonNull)
                    .filter(chunk -> chunk.layer == 3)
                    .collect(Collectors.toSet());

            Set<ChunkResult> finalL2 = Arrays.stream(l2).filter(Objects::nonNull).collect(Collectors.toSet());


            int totalL4Indexes = (int) l4.stream()
                    .map(ChunkResult::chunks)
                    .filter(Objects::nonNull)
                    .flatMap(Arrays::stream)
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .count();

            int totalL3Indexes = (int) l3.stream()
                    .map(ChunkResult::chunks)
                    .filter(Objects::nonNull)
                    .count();

            int totalL2Indexes = (int) finalL2.stream()
                    .map(ChunkResult::chunks).
                    filter(Objects::nonNull)
                    .count();

            int totalL1Indexes = (int) Arrays.stream(l1Chunks.chunks)
                    .filter(Objects::nonNull)
                    .count();

            int totalIndexes = totalL4Indexes + totalL3Indexes + totalL2Indexes + totalL1Indexes;

            System.out.println("l4: " + l4.size() + ", " + totalL4Indexes);
            System.out.println("l3: " + l3.size() + ", " + totalL3Indexes);
            System.out.println("l2: " + finalL2.size() + ", " + totalL2Indexes);
            System.out.println("l1: " + 1 + ", " + totalL1Indexes);
            System.out.println("total: " + totalIndexes);

            Map<ChunkResult, Long> chunkOffset = new IdentityHashMap<>();
            Map<ChunkResult, Map<Integer, Long>> chunkOffsetBy = new IdentityHashMap<>();

            long occupancyMaskLength = (long) Math.ceil((l1Size.width() * l1Size.height() * l1Size.depth()) / 8.0);

            out.write(writeU24((int) axes.getFirst().getAlong(l1Size)));
            out.write(writeU24((int) axes.getSecond().getAlong(l1Size)));
            out.write(writeU24((int) axes.getThird().getAlong(l1Size)));
            out.write(createOccupancyMask(l1Chunks.chunks));

            long indexStart = out.position();
            chunkOffset.put(l1Chunks, indexStart + 3 * 3 + occupancyMaskLength);
            for (int idx = 0; idx < l1Chunks.chunks.length; idx++) {
//                int i0 = (int) (idx % axes.getFirst().getAlong(l1Size));
//                int i1 = (int) ((idx / axes.getFirst().getAlong(l1Size)) % axes.getSecond().getAlong(l1Size));
//                int i2 = (int) (idx / (axes.getFirst().getAlong(l1Size)) * axes.getSecond().getAlong(l1Size));
//                Position offsetPos = axes.position(i0, i1, i2);
                if (l1Chunks.chunks[idx] == null)
                    continue;

                chunkOffsetBy.computeIfAbsent(l1Chunks, key -> new HashMap<>()).put(idx, out.position());
                out.write(new byte[8]);
            }

            for (int i = 0; i < l2.length; i++) {
                ChunkResult chunkResult = l2[i];

                if (chunkResult == null)
                    continue;

                long chunkStart = out.position();

                out.position(chunkOffsetBy.get(chunkResult.prev).get(i));
                out.write(writeU64(chunkStart));

                out.position(chunkStart);
                System.out.println("uooo");
                out.write(createOccupancyMask(chunkResult.chunks));
                chunkOffset.put(chunkResult, chunkStart + 8);
                for (int idx = 0; idx < chunkResult.chunks.length; idx++) {
                    if (chunkResult.chunks[idx] == null)
                        continue;

                    chunkOffsetBy.computeIfAbsent(chunkResult, key -> new HashMap<>()).put(
                            idx,
                            out.position()
                    );

                    out.write(new byte[8]);
                }
            }

            for (int j = 0; j < l2.length; j++) {
                if (l2[j] == null)
                    continue;

                ChunkResult[] l3Result = bridgeL2toL3.get(l2[j]);

                for (int i = 0; i < l3Result.length; i++) {
                    ChunkResult chunkResult = l3Result[i];

                    if (chunkResult == null)
                        continue;

                    long chunkStart = out.position();

                    out.position(chunkOffsetBy.get(chunkResult.prev).get(i));
                    out.write(writeU64(chunkStart));

                    out.position(chunkStart);
                    out.write(createOccupancyMask(chunkResult.chunks));
                    chunkOffset.put(chunkResult, chunkStart + 8);
                    for (int idx = 0; idx < chunkResult.chunks.length; idx++) {
                        if (chunkResult.chunks[idx] == null)
                            continue;

                        chunkOffsetBy.computeIfAbsent(chunkResult, key -> new HashMap<>()).put(
                                idx,
                                out.position()
                        );

                        out.write(new byte[8]);
                    }
                }
            }


//            out.position(HEADER_START_INDEX + headerResult.offsetIndexInfo);
//            out.write(42);
//            out.write(69);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] createOccupancyMask(List<ChunkPointer>[] chunks) {
        byte[] result = new byte[(int) Math.ceil(chunks.length / 8.0)];

        for (int i = 0; i < chunks.length; i++)
            result[i / 8] = (byte) (result[i / 8] | ((chunks[i] == null ? 0 : 1) << (i % 8)));

        System.out.println("why: " + result.length + " | " + chunks.length);
        return result;
    }

    private ChunkResult[] compact(ChunkResult prevResult, AxisOrder axes, int newSize, int arraySize) {
        List<ChunkPointer>[] chunks = prevResult.chunks;

        ChunkResult[] result = new ChunkResult[arraySize];

        Dimensions prevSize = prevResult.size;
        Dimensions size = Dimensions.box(newSize);

        for (int idx = 0; idx < chunks.length; idx++) {
            List<ChunkPointer> chunk = chunks[idx];

            if (chunk == null)
                continue;

            int i0 = (int) (idx % axes.getFirst().getAlong(prevSize));
            int i1 = (int) ((idx / axes.getFirst().getAlong(prevSize)) % axes.getSecond().getAlong(prevSize));
            int i2 = (int) (idx / (axes.getFirst().getAlong(prevSize)) * axes.getSecond().getAlong(prevSize));

            Position offset = add(prevResult.offset, multiply(axes.position(i0, i1, i2), size));

            List<ChunkPointer>[] lists = toSimpleArray(splitToChunks(chunk, offset, axes, size, newSize), size, axes, size.toArraySize(), prevResult);
            result[idx] = new ChunkResult(
                    lists,
                    offset,
                    size,
                    prevResult.layer + 1,
                    prevResult
            );
        }

        return result;
    }

    private List<ChunkPointer>[] toSimpleArray(List<ChunkPointer>[][][] chunks, Dimensions size, AxisOrder axes, int arraySize, ChunkResult prev) {
        List<ChunkPointer>[] result = new List[arraySize];

        for (int j0 = 0; j0 < chunks.length; j0++) {
            for (int j1 = 0; j1 < chunks[j0].length; j1++) {
                for (int j2 = 0; j2 < chunks[j0][j1].length; j2++) {
                    List<ChunkPointer> pointers = chunks[j0][j1][j2];
                    if (pointers == null)
                        continue;

                    int chunkIdx = (int) (j0 + (j1 * axes.getFirst().getAlong(size)) + (j2 * axes.getFirst().getAlong(size) * axes.getSecond().getAlong(size)));
                    System.out.println("meow: " + j0 + ", " + j1 + ", " + j2 + " (" + size + ":" + arraySize + ")");

                    List<ChunkPointer> chunk = result[chunkIdx];
                    if (chunk == null) {
                        chunk = new ArrayList<>();
                        result[chunkIdx] = chunk;
                    }


                    for (ChunkPointer pointer : pointers) {
                        chunk.add(new ChunkPointer(pointer.position, pointer.fileOffset, prev));
                    }
                }
            }
        }

        return result;
    }

    private record ChunkPointer(Position position, long fileOffset, ChunkResult prev) { }
    private record ChunkResult(List<ChunkPointer>[] chunks, Position offset, Dimensions size, int layer, ChunkResult prev) {

        @Override
        public @NotNull String toString() {
            return "ChunkResult{" + "chunks=" + Arrays.stream(chunks)
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .map(p -> p.position + ": " + p.fileOffset)
                    .collect(Collectors.joining(", ", "[", "]")) +
                    ", layer=" + layer +
                    ", prev=" + prev +
                    '}';
        }
    }

    private List<ChunkPointer>[][][] splitToChunks(List<ChunkPointer> indexes, Position offset, AxisOrder axes, Dimensions chunkDim, int chunkSize) {
        List<ChunkPointer>[][][] splitToChunks = new List
                [
                (int) axes.getFirst().getAlong(chunkDim)
                ][
                (int) axes.getSecond().getAlong(chunkDim)
                ][
                (int) axes.getThird().getAlong(chunkDim)
                ];

        for (ChunkPointer pointer : indexes) {
            int idx0 = (int) (axes.getFirst().getAlong(subtract(pointer.position(), offset)) / chunkSize);
            int idx1 = (int) (axes.getSecond().getAlong(subtract(pointer.position(), offset)) / chunkSize);
            int idx2 = (int) (axes.getThird().getAlong(subtract(pointer.position(), offset)) / chunkSize);

            List<ChunkPointer> chunk = splitToChunks[idx0][idx1][idx2];
            if (chunk == null) {
                chunk = new ArrayList<>();
                splitToChunks[idx0][idx1][idx2] = chunk;
            }

            chunk.add(pointer);
        }



        return splitToChunks;
    }

    private Dimensions splitToChunks(Dimensions dimensions, int chunkSize) {
        return new Dimensions(
                Math.ceil(dimensions.width() / (double) chunkSize),
                Math.ceil(dimensions.height() / (double) chunkSize),
                Math.ceil(dimensions.depth() / (double) chunkSize)
        );
    }

    private long findLargestByAxis(List<Map.Entry<Position, Long>> indexes, Axis axis) {
        return indexes.stream()
                .max(Comparator.comparingDouble(a -> axis.getAlong(a.getKey())))
                .map(entry -> axis.getAlong(entry.getKey()))
                .map(Double::longValue)
                .orElse(0L);
    }

    private Position subtract(Position a, Position b) {
        return new Position(
                a.getX() - b.getX(),
                a.getY() - b.getY(),
                a.getZ() - b.getZ()
        );
    }

    private Position add(Position a, Position b) {
        return new Position(
                a.getX() + b.getX(),
                a.getY() + b.getY(),
                a.getZ() + b.getZ()
        );
    }

    private Position multiply(Position position, Dimensions dimensions) {
        return new Position(
                position.getX() * dimensions.width(),
                position.getY() * dimensions.height(),
                position.getZ() * dimensions.depth()
        );
    }

    private record HeaderResult(byte[] data, long offsetBlockInfo, long offsetIndexInfo) { }

    private HeaderResult headers(Schematic schematic) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bOut);

        out.write(writeU16(Headers.ID));
        out.write(writeU64(schematic.id().getMostSignificantBits()));
        out.write(writeU64(schematic.id().getLeastSignificantBits()));

        out.write(writeU16(Headers.AXIS_ORDER));
        out.write(schematic.axisOrder().getId());

        out.write(Headers.OFFSET);
        out.write(writeU24((int) schematic.offset().getX()));
        out.write(writeU24((int) schematic.offset().getY()));
        out.write(writeU24((int) schematic.offset().getZ()));

        out.write(Headers.SIZE);
        out.write(writeU24((int) schematic.size().width()));
        out.write(writeU24((int) schematic.size().height()));
        out.write(writeU24((int) schematic.size().depth()));

        out.write(Headers.BLOCK_DATA_ENCODING);
        out.write(writeU16(preferEncoder.getKey()));

        out.write(Headers.BLOCK_INFO);
        int blockInfoIndex = out.size();
        out.write(new byte[CompressionBlockInfo.SIZE]);

        out.write(Headers.INDEX_INFO);
        int indexInfoIndex = out.size();
        out.write(new byte[CompressionBlockInfo.SIZE]);

        out.write(Headers.TIMESTAMP);
        out.write(writeU64(schematic.created().toEpochMilli()));

        out.write(Headers.AUTHOR);
        out.write(writeString(schematic.author()));

        out.write(Headers.TITLE);
        out.write(writeString(schematic.title()));

        return new HeaderResult(
                bOut.toByteArray(),
                blockInfoIndex,
                indexInfoIndex
        );
    }

    @Override
    public Schematic read(SeekableInputStream in) {
        try {
            DataInputStream din = new DataInputStream(in);

            for (int i = 0; i < FINGERPRINT_SIZE; i++) {
                if (in.read() != FINGERPRINT.charAt(i))
                    throw new EOFException("Invalid fingerprint!");
            }

            {
                int version = readU16(din);
                if (version != VERSION)
                    throw new UnsupportedEncodingException("Version aren't match\nexpected: " + VERSION + "\ngot: " + version);
            }

            int headerSize = readU24(din);
            byte[] header = in.readNBytes(headerSize);

            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
