package net.apartium.cocoabeans.space.schematic.format;

import net.apartium.cocoabeans.space.BoxRegion;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.schematic.BlockData;
import net.apartium.cocoabeans.space.schematic.BlockDataEncoder;
import net.apartium.cocoabeans.space.schematic.Dimensions;
import net.apartium.cocoabeans.space.schematic.Schematic;
import net.apartium.cocoabeans.space.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.space.schematic.compression.CompressionEngine;
import net.apartium.cocoabeans.space.schematic.compression.CompressionType;
import net.apartium.cocoabeans.space.schematic.utils.FileUtils;
import net.apartium.cocoabeans.space.schematic.utils.SeekableInputStream;
import net.apartium.cocoabeans.space.schematic.utils.SeekableOutputStream;
import net.apartium.cocoabeans.structs.Entry;

import java.io.*;
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

    public static final Dimensions L1_DIM = Dimensions.box(L1_SIZE);
    public static final Dimensions L2_DIM = Dimensions.box(L2_SIZE);
    public static final Dimensions L3_DIM = Dimensions.box(L3_SIZE);
    public static final Dimensions L4_DIM = Dimensions.box(L4_SIZE);

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

            Dimensions l1Size = splitToChunk(dimensions, L1_DIM);

            offset = 0;
            ByteArrayOutputStream indexesOut = new ByteArrayOutputStream();

            indexesOut.write(writeU24((int) axes.getFirst().getAlong(l1Size)));
            offset += 3;

            indexesOut.write(writeU24((int) axes.getSecond().getAlong(l1Size)));
            offset += 3;

            indexesOut.write(writeU24((int) axes.getThird().getAlong(l1Size)));
            offset += 3;

            ChunkResult l1 = new ChunkResult(
                    null,
                    Position.ZERO,
                    Position.ZERO,
                    1,
                    indexes.stream()
                            .map(entry -> new ChunkPointer(entry.getKey(), entry.getValue()))
                            .toList(),
                    List.of(
                            L1_DIM, L2_DIM, L3_DIM, L4_DIM
                    ),
                    axes
            );

            l1.createAllChildren();
            System.out.println(l1);



//            out.position(HEADER_START_INDEX + headerResult.offsetIndexInfo);
//            out.write(42);
//            out.write(69);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        private final List<ChunkPointer> leftOverByChild;
        private final List<Dimensions> layerSize;
        private final AxisOrder axes;

        private boolean calculated = false;
        private boolean empty = false;

        private ChunkResult(ChunkResult parent, Position actualPoint, Position chunkPoint, int layer, List<ChunkPointer> leftOverByChild, List<Dimensions> layerSize, AxisOrder axes) {
            this.parent = parent;
            this.actualPoint = actualPoint;
            this.chunkPoint = chunkPoint;
            this.layer = layer;
            this.leftOverByChild = leftOverByChild;
            this.layerSize = layerSize;
            this.axes = axes;
        }

        public byte[] createOccupancyMask() {
            if (isLastLayer())
                return FileUtils.createOccupancyMask(this.leftOverByChild, Objects::nonNull); // TODO fix it to work on last layer

            return FileUtils.createOccupancyMask(this.children, result -> result != null && !result.isEmpty());
        }

        public ChunkResult getParent() {
            return parent;
        }

        public ChunkResult[] getChildren() {
            return children;
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
            return this.layer == (this.layerSize.size() + 1);
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

            if (isLastLayer()) {
                calculated = true;
                return;
            }

            this.empty = leftOverByChild.isEmpty();

            if (this.empty) {
                calculated = true;
                return;
            }

            Dimensions layerSize = this.layerSize.get(this.layer - 1);

            for (int idx = 0; idx < CHILD_SIZE; idx++) {
                int i0 = idx % (int) axes.getFirst().getAlong(CHUNK_DIM);
                int i1 = (int) ((idx / axes.getFirst().getAlong(CHUNK_DIM)) % axes.getSecond().getAlong(CHUNK_DIM));
                int i2 = (int) (idx / (axes.getFirst().getAlong(CHUNK_DIM) * axes.getSecond().getAlong(CHUNK_DIM)));


                Position chunkPoint = axes.position(i0, i1, i2);
                Position actualPoint = add(this.actualPoint, multiply(chunkPoint, layerSize));

                BoxRegion region = layerSize.toBoxRegion(chunkPoint);

                ChunkResult chunkResult = new ChunkResult(
                        this,
                        actualPoint,
                        chunkPoint,
                        this.layer + 1,
                        this.leftOverByChild.stream()
                                .filter(inChunk(region))
                                .sorted((a, b) -> axes.compare(a.position, b.position))
                                .toList(),
                        this.layerSize,
                        this.axes
                );

                children[idx] = chunkResult;
            }

            calculated = true;
        }

        public void createAllChildren() {
            if (isLastLayer())
                return;

            createChildren();
            for (ChunkResult child : children) {
                if (child == null)
                    continue;

                child.createAllChildren();
            }
        }

        private Predicate<? super ChunkPointer> inChunk(BoxRegion region) {
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
