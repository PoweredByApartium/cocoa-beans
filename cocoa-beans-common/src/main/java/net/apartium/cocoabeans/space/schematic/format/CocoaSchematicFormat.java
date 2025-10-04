package net.apartium.cocoabeans.space.schematic.format;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.Region;
import net.apartium.cocoabeans.space.schematic.*;
import net.apartium.cocoabeans.space.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.space.schematic.compression.CompressionEngine;
import net.apartium.cocoabeans.space.schematic.compression.CompressionType;
import net.apartium.cocoabeans.space.schematic.utils.ByteArraySeekableChannel;
import net.apartium.cocoabeans.space.schematic.utils.FileUtils;
import net.apartium.cocoabeans.space.schematic.utils.SeekableInputStream;
import net.apartium.cocoabeans.space.schematic.utils.SeekableOutputStream;
import net.apartium.cocoabeans.structs.Entry;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;

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
    public static final int L5_SIZE = 1;

    public static final int CHUNK_AXIS_SIZE = 4;

    public static final Dimensions L1_DIM = Dimensions.box(L1_SIZE);
    public static final Dimensions L2_DIM = Dimensions.box(L2_SIZE);
    public static final Dimensions L3_DIM = Dimensions.box(L3_SIZE);
    public static final Dimensions L4_DIM = Dimensions.box(L4_SIZE);
    public static final Dimensions L5_DIM = Dimensions.box(L5_SIZE);

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
    private CompressionType compressionTypeForIndexes = CompressionType.GZIP;
    private Map.Entry<Integer, BlockDataEncoder> preferEncoder;
    private SchematicFactory schematicFactory;

    public CocoaSchematicFormat(Map<Integer, BlockDataEncoder> blockDataEncoderMap, CompressionEngine compressionEngine, SchematicFactory schematicFactory) {
        this.blockDataEncoderMap = new HashMap<>(blockDataEncoderMap);
        this.compressionEngine = compressionEngine;
        if (!blockDataEncoderMap.isEmpty())
            preferEncoder = blockDataEncoderMap.entrySet().iterator().next();

        this.schematicFactory = schematicFactory;
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
            // TODO may wanna equal block data to not have to write many times and just point same point
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

            offset = 0;
            
            final AxisOrder axes = schematic.axisOrder();
            List<Map.Entry<Position, Long>> indexes = blockIndexes.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(axes))
                    .toList();

            Dimensions dimensions = schematic.size();

            Dimensions l1Size = splitToChunk(dimensions, L1_DIM);

            ByteArraySeekableChannel channel = new ByteArraySeekableChannel();
            SeekableOutputStream indexesOut = new SeekableOutputStream(channel);

            ChunkResult[][][] chunkResults = new ChunkResult
                    [(int) axes.getFirst().getAlong(l1Size)]
                    [(int) axes.getFirst().getAlong(l1Size)]
                    [(int) axes.getFirst().getAlong(l1Size)];

            // TODO may optimize it later
            List<Dimensions> sizes = List.of(
                    L1_DIM, L2_DIM, L3_DIM, L4_DIM, L5_DIM
            );

            for (int i0 = 0; i0 < chunkResults.length; i0++) {
                for (int i1 = 0; i1 < chunkResults[0].length; i1++) {
                    for (int i2 = 0; i2 < chunkResults[0][0].length; i2++) {
                        Position chunkPoint = axes.position(i0, i1, i2);
                        Position actualPoint = multiply(chunkPoint, L1_DIM);

                        Region region = L1_DIM.toBoxRegion(actualPoint);

                        List<ChunkPointer> pointers = indexes.stream()
                                .map(entry -> new ChunkPointer(entry.getKey(), entry.getValue()))
                                .filter(pointer -> region.contains(subtract(pointer.position, actualPoint)))
                                .toList();

                        if (pointers.isEmpty())
                            continue;

                        ChunkResult chunkResult = new ChunkResult(
                                null,
                                actualPoint,
                                chunkPoint,
                                1,
                                pointers,
                                sizes,
                                axes
                        );

                        chunkResults[i0][i1][i2] = chunkResult;
                        chunkResult.createAllChildren();
                    }
                }
            }

            indexesOut.write(writeU24((int) axes.getFirst().getAlong(l1Size)));
            offset += 3;

            indexesOut.write(writeU24((int) axes.getSecond().getAlong(l1Size)));
            offset += 3;

            indexesOut.write(writeU24((int) axes.getThird().getAlong(l1Size)));
            offset += 3;

            ChunkResult[] l1AsSimpleArray = toSimpleArray(chunkResults, l1Size, axes, new ChunkResult[0]);
            byte[] occupancyMask = createOccupancyMask(l1AsSimpleArray);

            indexesOut.write(occupancyMask);
            offset += occupancyMask.length;

            for (ChunkResult l1 : l1AsSimpleArray) {
                if (l1 == null)
                    continue;

                l1.setOffset(offset);
                offset = writeIndexChildren(offset, indexesOut, l1);
            }

            List<ChunkResult> listL2 = Arrays.stream(l1AsSimpleArray).filter(Objects::nonNull).flatMap(result -> Arrays.stream(result.getChildren())).filter(Objects::nonNull).toList();
            offset = computeResult(indexesOut, listL2, offset);

            List<ChunkResult> listL3 = listL2.stream().flatMap(result -> Arrays.stream(result.getChildren())).filter(Objects::nonNull).toList();
            offset = computeResult(indexesOut, listL3, offset);


            List<ChunkResult> listL4 = listL3.stream().flatMap(result -> Arrays.stream(result.getChildren())).filter(Objects::nonNull).toList();
            offset = computeResult(indexesOut, listL4, offset);

            List<ChunkResult> listL5 = listL4.stream().flatMap(result -> Arrays.stream(result.getChildren())).filter(Objects::nonNull).toList();
            for (ChunkResult result : listL5) {
                pointToParent(indexesOut, result, offset);

                byte[] mask = result.createOccupancyMask();

                indexesOut.write(mask);
                offset += mask.length;

                for (ChunkPointer child : result.getChildrenAsPointer()) {
                    if (child == null)
                        continue;

                    indexesOut.write(writeU64(child.fileOffset));
                    offset += 8;
                }
            }

            byte[] indexesAsBytes = channel.toByteArray();
            compressed = compressionEngine.compress(compressionTypeForIndexes, indexesAsBytes);

            CompressionBlockInfo indexInfo = new CompressionBlockInfo(
                    compressionTypeForIndexes,
                    indexesAsBytes.length,
                    compressed.length,
                    out.position(),
                    crc32(indexesAsBytes)
            );

            long indexStartIndex = HEADER_START_INDEX + headerResult.offsetIndexInfo;
            out.position(indexStartIndex);
            out.write(indexInfo.toByteArray());

            out.position(indexInfo.offset());
            out.write(compressed);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void pointToParent(SeekableOutputStream indexesOut, ChunkResult result, long offset) throws IOException {
        result.setOffset(offset);

        // Point parent to here
        long parentIndex = Arrays.stream(result.getParent().getChildren()).filter(Objects::nonNull).toList().indexOf(result);

        if (parentIndex == -1)
            throw new RuntimeException("Parent index out of bounds!");

        indexesOut.position(result.getParent().getOffset() + 8 + parentIndex * 8);
        indexesOut.write(writeU64(result.getOffset()));

        indexesOut.position(result.getOffset());
    }

    private long computeResult(SeekableOutputStream indexesOut, List<ChunkResult> chunkResults, long offset) throws IOException {
        for (ChunkResult result : chunkResults) {
            pointToParent(indexesOut, result, offset);
            offset = writeIndexChildren(offset, indexesOut, result);
        }

        return offset;
    }

    private long writeIndexChildren(long offset, SeekableOutputStream indexesOut, ChunkResult result) throws IOException {
        byte[] mask = result.createOccupancyMask();

        indexesOut.write(mask);
        offset += mask.length;

        for (ChunkResult child : result.getChildren()) {
            if (child == null)
                continue;

            indexesOut.write(new byte[8]); // Write pos later
            offset += 8;
        }
        return offset;
    }

    private <T> T[] toSimpleArray(T[][][] arr, Dimensions size, AxisOrder axes, T[] simple) {
        T[] result = Arrays.copyOf(simple, size.toAreaSize());

        for (int i0 = 0; i0 < arr.length; i0++) {
            for (int i1 = 0; i1 < arr[i0].length; i1++) {
                for (int i2 = 0; i2 < arr[i0][i1].length; i2++) {
                    int idx = (int) (i0 + (i1 * axes.getFirst().getAlong(size)) + (i2 * axes.getFirst().getAlong(size) * axes.getSecond().getAlong(size)));
                    result[idx] = arr[i0][i1][i2];
                }
            }
        }

        return result;
    }



    private Dimensions splitToChunk(Dimensions dimensions, Dimensions chunkSize) {
        return new Dimensions(
                Math.ceil(dimensions.width() / chunkSize.width()),
                Math.ceil(dimensions.height() / chunkSize.height()),
                Math.ceil(dimensions.depth() / chunkSize.depth())
        );
    }


    private record ChunkPointer(Position position, long fileOffset) { }
    private static class ChunkResult {

        public static final int CHILD_SIZE = 64;
        public static final Dimensions CHUNK_DIM = new Dimensions(4, 4, 4);

        private final ChunkResult parent;
        private final Position actualPoint;
        private final Position chunkPoint;
        private final int layer;
        private final ChunkResult[] children = new ChunkResult[CHILD_SIZE];
        private final ChunkPointer[] childrenAsPointer = new ChunkPointer[CHILD_SIZE];
        private final List<ChunkPointer> leftOverByChild;
        private final List<Dimensions> layersSize;
        private final AxisOrder axes;
        private final Dimensions size;

        private final boolean empty;
        private boolean calculated = false;

        private long offset;

        private ChunkResult(ChunkResult parent, Position actualPoint, Position chunkPoint, int layer, List<ChunkPointer> leftOverByChild, List<Dimensions> layersSize, AxisOrder axes) {
            this.parent = parent;
            this.actualPoint = actualPoint;
            this.chunkPoint = chunkPoint;
            this.layer = layer;
            this.leftOverByChild = leftOverByChild;
            this.layersSize = layersSize;
            this.size = this.layersSize.get(this.layer - 1);
            this.axes = axes;
            this.empty = leftOverByChild.isEmpty();
        }

        public byte[] createOccupancyMask() {
            if (isLastLayer())
                return FileUtils.createOccupancyMask(this.childrenAsPointer, Objects::nonNull);

            return FileUtils.createOccupancyMask(this.children, result -> result != null && !result.isEmpty());
        }

        public ChunkResult getParent() {
            return parent;
        }

        public ChunkResult[] getChildren() {
            return children;
        }

        public ChunkPointer[] getChildrenAsPointer() {
            return childrenAsPointer;
        }

        public int getLayer() {
            return layer;
        }

        public Position getActualPoint() {
            return actualPoint;
        }

        public Position getChunkPoint() {
            return chunkPoint;
        }

        public boolean isLastLayer() {
            return this.layer == (this.layersSize.size());
        }

        public boolean isCalculated() {
            return this.calculated;
        }

        public boolean isEmpty() {
            return this.empty;
        }

        private void createChildren() {
            if (this.calculated)
                return;

            if (this.empty) {
                calculated = true;
                return;
            }

            if (isLastLayer()) {
                for (ChunkPointer pointer : leftOverByChild) {
                    Position chunkPoint = subtract(pointer.position, this.actualPoint);
                    int index = (int) (axes.getFirst().getAlong(chunkPoint)
                            + (axes.getSecond().getAlong(chunkPoint) * axes.getFirst().getAlong(CHUNK_DIM)
                            + (axes.getThird().getAlong(chunkPoint) * axes.getFirst().getAlong(CHUNK_DIM) * axes.getSecond().getAlong(CHUNK_DIM))));

                    if (index < 0 || index >= CHUNK_DIM.toAreaSize())
                        throw new IndexOutOfBoundsException("index is out of bound: " + index);

                    if (childrenAsPointer[index] != null)
                        throw new RuntimeException("Chunk pointer found 2 times: " + index + " a: " + childrenAsPointer[index] + " b: " + pointer);

                    childrenAsPointer[index] = pointer;
                }

                calculated = true;
                return;
            }


            for (int idx = 0; idx < CHILD_SIZE; idx++) {
                int i0 = idx % (int) axes.getFirst().getAlong(CHUNK_DIM);
                int i1 = (int) ((idx / axes.getFirst().getAlong(CHUNK_DIM)) % axes.getSecond().getAlong(CHUNK_DIM));
                int i2 = (int) (idx / (axes.getFirst().getAlong(CHUNK_DIM) * axes.getSecond().getAlong(CHUNK_DIM)));


                Position chunkPoint = axes.position(i0, i1, i2);
                Position actualPoint = add(this.actualPoint, multiply(chunkPoint, size));

                Region region = size.toBoxRegion(actualPoint);

                ChunkResult chunkResult = new ChunkResult(
                        this,
                        actualPoint,
                        chunkPoint,
                        this.layer + 1,
                        this.leftOverByChild.stream()
                                .filter(inChunk(region))
                                .sorted((a, b) -> axes.compare(a.position, b.position))
                                .toList(),
                        this.layersSize,
                        this.axes
                );

                children[idx] = chunkResult;
            }

            calculated = true;
        }

        public void createAllChildren() {
            createChildren();

            if (isLastLayer())
                return;

            for (int i = 0; i < children.length; i++) {
                ChunkResult child = children[i];
                if (child == null)
                    continue;

                child.createAllChildren();

                if (child.isEmpty())
                    children[i] = null;
            }
        }

        private Predicate<? super ChunkPointer> inChunk(Region region) {
            return pointer -> {
                Position position = subtract(pointer.position, this.actualPoint);
                return region.contains(position);
            };
        }

        @Override
        public String toString() {
            return "ChunkResult{" +
                    "actualPoint=" + actualPoint +
                    ", chunkPoint=" + chunkPoint +
                    ", layer=" + layer +
                    ", children=" + Arrays.toString(children) +
                    ", leftOverByChild=" + leftOverByChild +
                    ", axes=" + axes +
                    ", calculated=" + calculated +
                    ", empty=" + empty +
                    '}';
        }

        public long getOffset() {
            return offset;
        }

        public void setOffset(long offset) {
            this.offset = offset;
        }
    }

    private static Position subtract(Position a, Position b) {
        return new Position(
                a.getX() - b.getX(),
                a.getY() - b.getY(),
                a.getZ() - b.getZ()
        );
    }

    private static Position add(Position a, Position b) {
        return new Position(
                a.getX() + b.getX(),
                a.getY() + b.getY(),
                a.getZ() + b.getZ()
        );
    }

    private static Position multiply(Position position, Dimensions dimensions) {
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

        out.write(writeU16(Headers.OFFSET));
        out.write(writeU24((int) schematic.offset().getX()));
        out.write(writeU24((int) schematic.offset().getY()));
        out.write(writeU24((int) schematic.offset().getZ()));

        out.write(writeU16(Headers.SIZE));
        out.write(writeU24((int) schematic.size().width()));
        out.write(writeU24((int) schematic.size().height()));
        out.write(writeU24((int) schematic.size().depth()));

        out.write(writeU16(Headers.BLOCK_DATA_ENCODING));
        out.write(writeU16(preferEncoder.getKey()));

        out.write(writeU16(Headers.BLOCK_INFO));
        int blockInfoIndex = out.size();
        out.write(new byte[CompressionBlockInfo.SIZE]);

        out.write(writeU16(Headers.INDEX_INFO));
        int indexInfoIndex = out.size();
        out.write(new byte[CompressionBlockInfo.SIZE]);

        out.write(writeU16(Headers.TIMESTAMP));
        out.write(writeU64(schematic.created().toEpochMilli()));

        out.write(writeU16(Headers.AUTHOR));
        out.write(writeString(schematic.author()));

        out.write(writeU16(Headers.TITLE));
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
            Map<Integer, Object> headers = parseHeaders(header);
            assertBaseHeaders(headers);

            CompressionBlockInfo blockInfo = (CompressionBlockInfo) headers.get(Headers.BLOCK_INFO);
            in.position(blockInfo.offset());
            byte[] compressedBlocks = in.readNBytes((int) blockInfo.compressedSize());
            byte[] originalBlockData = compressionEngine.decompress(blockInfo.type(), compressedBlocks);
            if (originalBlockData.length != blockInfo.uncompressedSize())
                throw new EOFException("Block data length mismatch");

            if (crc32(originalBlockData) != blockInfo.checksum())
                throw new EOFException("Checksum mismatch");

            CompressionBlockInfo indexInfo = (CompressionBlockInfo) headers.get(Headers.INDEX_INFO);
            in.position(indexInfo.offset());
            byte[] compressedIndexInfo = in.readNBytes((int) indexInfo.uncompressedSize());
            byte[] originalIndexInfo = compressionEngine.decompress(indexInfo.type(), compressedIndexInfo);
            if (originalIndexInfo.length != indexInfo.uncompressedSize())
                throw new EOFException("Index info length mismatch");

            if (crc32(originalIndexInfo) != indexInfo.checksum())
                throw new EOFException("Checksum mismatch");

            AxisOrder axes = (AxisOrder) headers.get(Headers.AXIS_ORDER);

            SeekableInputStream blockIn = new SeekableInputStream(ByteArraySeekableChannel.of(originalBlockData));
            BlockDataEncoder encoder = (BlockDataEncoder) headers.get(Headers.BLOCK_DATA_ENCODING);

            SeekableInputStream indexIn = new SeekableInputStream(ByteArraySeekableChannel.of(originalIndexInfo));
            DataInputStream indexDin = new DataInputStream(indexIn);
            Dimensions l1Size = axes.dimensions(
                    readU24(indexDin),
                    readU24(indexDin),
                    readU24(indexDin)
            );

            Map<Position, BlockData> blocks = new HashMap<>();
            boolean[] mask = toBooleanArray(indexIn.readNBytes((int) Math.ceil(l1Size.toAreaSize() / 8.0)), l1Size.toAreaSize());
            for (int i = 0; i < mask.length; i++) {
                if (!mask[i])
                    continue;

                int i0 = (int) (i % axes.getFirst().getAlong(l1Size));
                int i1 = (int) ((i / axes.getFirst().getAlong(l1Size)) % axes.getSecond().getAlong(l1Size));
                int i2 = (int) (i / (axes.getFirst().getAlong(l1Size) * axes.getSecond().getAlong(l1Size)));

                Position l1ChunkPoint = axes.position(i0, i1, i2);
                Position l1ActualPoint = multiply(l1ChunkPoint, L1_DIM);

                boolean[] l1Mask = toBooleanArray(indexIn.readNBytes(8), 64);
                for (int j = 0; j < l1Mask.length; j++) {
                    if (!l1Mask[j])
                        continue;

                    i0 = j % CHUNK_AXIS_SIZE;
                    i1 = (j / CHUNK_AXIS_SIZE) % CHUNK_AXIS_SIZE;
                    i2 = j / (CHUNK_AXIS_SIZE * CHUNK_AXIS_SIZE);

                    Position l2ChunkPoint = axes.position(i0, i1, i2);
                    Position l2ActualPoint = add(l1ActualPoint, multiply(l2ChunkPoint, L1_DIM));

                    long l2Pos = readU64(indexDin);
                    long beforeL2Offset = indexIn.position();

                    indexIn.position(l2Pos);
                    boolean[] l2Mask = toBooleanArray(indexIn.readNBytes(8), ChunkResult.CHILD_SIZE);
                    for (int k = 0; k < l2Mask.length; k++) {
                        if (!l2Mask[k])
                            continue;

                        i0 = k % CHUNK_AXIS_SIZE;
                        i1 = (k / CHUNK_AXIS_SIZE) % CHUNK_AXIS_SIZE;
                        i2 = k / (CHUNK_AXIS_SIZE * CHUNK_AXIS_SIZE);

                        Position l3ChunkPoint = axes.position(i0, i1, i2);
                        Position l3ActualPoint = add(l2ActualPoint, multiply(l3ChunkPoint, L2_DIM));

                        long l3Pos = readU64(indexDin);
                        long beforeL3Offset = indexIn.position();

                        indexIn.position(l3Pos);
                        boolean[] l3Mask = toBooleanArray(indexIn.readNBytes(8), ChunkResult.CHILD_SIZE);
                        for (int l = 0; l < l3Mask.length; l++) {
                            if (!l3Mask[l])
                                continue;

                            i0 = l % CHUNK_AXIS_SIZE;
                            i1 = (l / CHUNK_AXIS_SIZE) % CHUNK_AXIS_SIZE;
                            i2 = l / (CHUNK_AXIS_SIZE * CHUNK_AXIS_SIZE);

                            Position l4ChunkPoint = axes.position(i0, i1, i2);
                            Position l4ActualPoint = add(l3ActualPoint, multiply(l4ChunkPoint, L3_DIM));

                            long l4Pos = readU64(indexDin);
                            long beforeL4Offset = indexIn.position();

                            indexIn.position(l4Pos);
                            boolean[] l4Mask = toBooleanArray(indexIn.readNBytes(8), ChunkResult.CHILD_SIZE);
                            for (int n = 0; n < l4Mask.length; n++) {
                                if (!l4Mask[n])
                                    continue;

                                i0 = n % CHUNK_AXIS_SIZE;
                                i1 = (n / CHUNK_AXIS_SIZE) % CHUNK_AXIS_SIZE;
                                i2 = n / (CHUNK_AXIS_SIZE * CHUNK_AXIS_SIZE);

                                Position l5ChunkPoint = axes.position(i0, i1, i2);
                                Position l5ActualPoint = add(l4ActualPoint, multiply(l5ChunkPoint, L4_DIM));

                                long l5Pos = readU64(indexDin);
                                long beforeL5Offset = indexIn.position();

                                indexIn.position(l5Pos);
                                boolean[] l5Mask = toBooleanArray(indexIn.readNBytes(8), ChunkResult.CHILD_SIZE);
                                for (int m = 0; m < l5Mask.length; m++) {
                                    if (!l5Mask[m])
                                        continue;

                                    i0 = m % CHUNK_AXIS_SIZE;
                                    i1 = (m / CHUNK_AXIS_SIZE) % CHUNK_AXIS_SIZE;
                                    i2 = m / (CHUNK_AXIS_SIZE * CHUNK_AXIS_SIZE);

                                    Position chunkPoint = axes.position(i0, i1, i2);
                                    Position pos = add(l5ActualPoint, chunkPoint);

                                    long offset = readU64(indexDin);
                                    blockIn.position(offset);

                                    if (blocks.containsKey(pos))
                                        throw new RuntimeException("Something went very wrong!!!");

                                    blocks.put(pos, encoder.read(blockIn));
                                }


                                indexIn.position(beforeL5Offset);
                            }


                            indexIn.position(beforeL4Offset);
                        }

                        indexIn.position(beforeL3Offset);
                    }

                    indexIn.position(beforeL2Offset);
                }
            }

            return schematicFactory.createSchematic(
                    (UUID) headers.get(Headers.ID),
                    (Instant) headers.get(Headers.TIMESTAMP),
                    (String) headers.get(Headers.AUTHOR),
                    (String) headers.get(Headers.TITLE),
                    blocks,
                    (Dimensions) headers.get(Headers.SIZE),
                    (AxisOrder) headers.get(Headers.AXIS_ORDER),
                    (Position) headers.get(Headers.OFFSET)

            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertBaseHeaders(Map<Integer, Object> headers) {
        if (headers.isEmpty())
            throw new IllegalStateException("No headers found!");

        if (!headers.containsKey(Headers.ID))
            throw new IllegalStateException("ID Header not found!");

        if (!headers.containsKey(Headers.AXIS_ORDER))
            throw new IllegalStateException("Axis Order Header not found!");

        if (!headers.containsKey(Headers.OFFSET))
            throw new IllegalStateException("Offset Header not found!");

        if (!headers.containsKey(Headers.SIZE))
            throw new IllegalStateException("Size Header not found!");

        if (!headers.containsKey(Headers.BLOCK_DATA_ENCODING))
            throw new IllegalStateException("BlockDataEncoding Header not found!");

        if (!headers.containsKey(Headers.BLOCK_INFO))
            throw new IllegalStateException("BlockInfo Header not found!");

        if (!headers.containsKey(Headers.INDEX_INFO))
            throw new IllegalStateException("IndexInfo Header not found!");

        if (!headers.containsKey(Headers.TIMESTAMP))
            throw new IllegalStateException("Timestamp Header not found!");

        if (!headers.containsKey(Headers.AUTHOR))
            throw new IllegalStateException("Author Header not found!");

        if (!headers.containsKey(Headers.TITLE))
            throw new IllegalStateException("Title Header not found!");
    }

    private Map<Integer, Object> parseHeaders(byte[] headers) throws IOException {
        int index = 0;

        Map<Integer, Object> map = new HashMap<>();

        while (index < headers.length) {
            int id = (headers[index] << 8) | (headers[index + 1]);
            index += 2;

            index = switch (id) {
                case Headers.ID -> {
                    map.put(Headers.ID, toUUID(splitFromAToB(headers, index, index + 16)));
                    yield index + 16;
                }
                case Headers.AXIS_ORDER -> {
                    map.put(Headers.AXIS_ORDER, AxisOrder.byId(headers[index]));
                    yield index + 1;
                }

                case Headers.OFFSET -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 9)));
                    map.put(Headers.OFFSET, new Position(readU24(in), readU24(in), readU24(in)));
                    yield index + 9;
                }

                case Headers.SIZE -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 9)));
                    map.put(Headers.SIZE, new Dimensions(readU24(in), readU24(in), readU24(in)));
                    yield index + 9;
                }

                case Headers.BLOCK_DATA_ENCODING -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 2)));
                    map.put(Headers.BLOCK_DATA_ENCODING, blockDataEncoderMap.get(readU16(in)));
                    yield index + 2;
                }

                case Headers.BLOCK_INFO -> {
                    map.put(Headers.BLOCK_INFO, CompressionBlockInfo.fromBytes(splitFromAToB(headers, index, index + CompressionBlockInfo.SIZE)));
                    yield index + CompressionBlockInfo.SIZE;
                }

                case Headers.INDEX_INFO -> {
                    map.put(Headers.INDEX_INFO, CompressionBlockInfo.fromBytes(splitFromAToB(headers, index, index + CompressionBlockInfo.SIZE)));
                    yield index + CompressionBlockInfo.SIZE;
                }

                case Headers.TIMESTAMP -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 8)));
                    map.put(Headers.TIMESTAMP, Instant.ofEpochMilli(readU64(in)));
                    yield index + 8;
                }

                case Headers.AUTHOR -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 4)));
                    int length = (int) readU32(in);

                    in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 4 + length)));
                    map.put(Headers.AUTHOR, readString(in));
                    yield index + 4 + length;
                }

                case  Headers.TITLE -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 4)));
                    int length = (int) readU32(in);

                    in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 4 + length)));
                    map.put(Headers.TITLE, readString(in));
                    yield index + 4 + length;
                }

                default -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 3)));
                    int length = readU24(in);
                    map.put(id, splitFromAToB(headers, index + 3, index + 3 + length)); // TODO add way to parse those bytes
                    yield index + 3 + length;

//                    throw new IllegalStateException("Unexpected value: " + id + " (index: " + index + ")");
                }
            };
        }

        return map;
    }

    private byte[] splitFromAToB(byte[] data, int a, int b) {
        if (data.length <= a)
            throw new IndexOutOfBoundsException("Couldn't start from the end!");

        if (data.length < b)
            throw new IndexOutOfBoundsException("Couldn't reach after the end!");

        byte[] result = new byte[b - a];
        System.arraycopy(data, a, result, 0, b - a);
        return result;
    }

}
