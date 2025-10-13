package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.Mathf;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.compression.CompressionEngine;
import net.apartium.cocoabeans.schematic.utils.ByteArraySeekableChannel;
import net.apartium.cocoabeans.schematic.utils.SeekableInputStream;
import net.apartium.cocoabeans.schematic.utils.SeekableOutputStream;
import net.apartium.cocoabeans.structs.Entry;
import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.apartium.cocoabeans.schematic.utils.FileUtils.*;

@ApiStatus.AvailableSince("0.0.45")
public class CocoaSchematicFormat implements SchematicFormat {

    public static BlockChunk temp;
    private boolean test = true;

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
    private final Map<Byte, CompressionEngine> compressionEngines;

    private CompressionEngine defaultCompressionEngineForBlocks;
    private CompressionEngine defaultCompressionEngineForIndexes;
    private Map.Entry<Integer, BlockDataEncoder> preferEncoder;
    private SchematicFactory<?> schematicFactory;

    public CocoaSchematicFormat(Map<Integer, BlockDataEncoder> blockDataEncoderMap, Set<CompressionEngine> compressionEngines, byte defaultCompressionForBlock, byte defaultCompressionForIndexes, SchematicFactory<?> schematicFactory) {
        this.blockDataEncoderMap = new HashMap<>(blockDataEncoderMap);
        if (!blockDataEncoderMap.isEmpty())
            preferEncoder = blockDataEncoderMap.entrySet().iterator().next();

        this.compressionEngines = compressionEngines.stream().collect(Collectors.toMap(CompressionEngine::type, Function.identity()));

        this.defaultCompressionEngineForBlocks = this.compressionEngines.get(defaultCompressionForBlock);
        this.defaultCompressionEngineForIndexes = this.compressionEngines.get(defaultCompressionForIndexes);

        this.schematicFactory = schematicFactory;
    }

    public void registerBlockEncoder(int id, BlockDataEncoder encoder) {
        blockDataEncoderMap.put(id, encoder);
    }

    public void setPreferEncoder(int id, BlockDataEncoder encoder) {
        preferEncoder = Map.entry(id, encoder);
    }

    public void registerCompressionEngine(byte id, CompressionEngine engine) {
        compressionEngines.put(id, engine);
    }

    public void setDefaultCompressionEngineForBlocks(CompressionEngine engine) {
        defaultCompressionEngineForBlocks = engine;
    }

    public void setDefaultCompressionEngineForIndexes(CompressionEngine engine) {
        defaultCompressionEngineForIndexes = engine;
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
            Map<BlockData, Long> blockIndexes = new IdentityHashMap<>();
            ByteArrayOutputStream blockOut = new ByteArrayOutputStream();
            long offset = 0;

            final AxisOrder axes = schematic.axisOrder();

            Dimensions dimensions = schematic.size();

            long maxDimensions = (long) Math.max(dimensions.width(), Math.max(dimensions.height(), dimensions.depth()));
            long scaler = Mathf.nextPowerOfFour(maxDimensions);
            BlockChunk blockChunk = new BlockChunk(axes, scaler, Position.ZERO, Position.ZERO);
            temp = blockChunk;

            while (iterator.hasNext()) {
                Entry<Position, BlockData> entry = iterator.next();
                Position position = entry.key();
                BlockData blockData = entry.value();

                if (blockData == null)
                    continue;

                blockChunk.setBlock(position, blockData);

                if (blockIndexes.containsKey(blockData))
                    continue;

                blockIndexes.put(blockData, offset);

                byte[] data = preferEncoder.getValue().write(blockData);
                blockOut.write(data);

                offset += data.length;
            }

            byte[] originalBlocksData = blockOut.toByteArray();
            byte[] compressed = defaultCompressionEngineForBlocks.compress(originalBlocksData);

            CompressionBlockInfo blockInfo = new CompressionBlockInfo(
                    defaultCompressionEngineForBlocks.type(),
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

            ByteArraySeekableChannel channel = new ByteArraySeekableChannel();
            SeekableOutputStream indexesOut = new SeekableOutputStream(channel);

            writeIndexes(indexesOut, scaler, blockChunk, blockIndexes);

            byte[] indexesAsBytes = channel.toByteArray();
            compressed = defaultCompressionEngineForIndexes.compress(indexesAsBytes);

            CompressionBlockInfo indexInfo = new CompressionBlockInfo(
                    defaultCompressionEngineForIndexes.type(),
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

    private void writeIndexes(SeekableOutputStream indexesOut, long scaler, BlockChunk blockChunk, Map<BlockData, Long> blockIndexes) throws IOException {
        Map<Pointer, Long> pointerFileOffsets = new IdentityHashMap<>();
        Map<Pointer, Entry<Pointer, Integer>> pointToParent = new IdentityHashMap<>();

        int layers = (int) Math.ceil(Mathf.log4(scaler));
        indexesOut.write((byte) layers);

        ChunkPointer rootPointer = new ChunkPointer(blockChunk);

        pointToParent.put(rootPointer, new Entry<>(rootPointer, 0));

        List<Pointer> nextInQueue = new ArrayList<>();
        nextInQueue.add(rootPointer);

        while (!nextInQueue.isEmpty()) {
            List<Pointer> currentPointers = new ArrayList<>(nextInQueue);
            nextInQueue.clear();

            while (!currentPointers.isEmpty()) {
                Pointer pointer = currentPointers.remove(0);
                long position = indexesOut.position();
                pointerFileOffsets.put(pointer, position);

                if (pointer instanceof ChunkPointer chunkPointer) {
                    writeChunkPointer(
                            indexesOut,
                            chunkPointer,
                            pointerFileOffsets,
                            pointToParent,
                            nextInQueue,
                            blockIndexes,
                            pointer != rootPointer
                    );
                    continue;
                }

                if (pointer instanceof BlockPointer blockPointer) {
                    throw new RuntimeException("BlockPointers are not supported");
//                    Long fileOffset = blockIndexes.get(blockPointer.getData());
//                    if (fileOffset == null)
//                        throw new IllegalStateException("Couldn't find block index for block " + blockPointer.getData());
//
//                    indexesOut.write(writeU64(fileOffset));
//                    continue;
                }


                throw new UnsupportedOperationException("Unsupported pointer type: " + pointer.getClass().getName());
            }
        }
    }

    private void writeChunkPointer(SeekableOutputStream out, ChunkPointer pointer, Map<Pointer, Long> pointerFileOffsets, Map<Pointer, Entry<Pointer, Integer>> pointToParent, List<Pointer> nextInQueue, Map<BlockData, Long> blockIndexes, boolean shouldPoint) throws IOException {
        Entry<Pointer, Integer> entry = pointToParent.get(pointer);
        if (shouldPoint)
            pointParentToChild(out, out.position(), pointerFileOffsets.get(entry.key()), entry.value());

        BlockChunk blockChunk = pointer.getChunk();
        long mask = blockChunk.getMask();

        if (mask == 0L)
            throw new IllegalStateException("Chunk mask is zero");

        out.write(writeU64(mask));
        Pointer[] pointers = blockChunk.getPointers();


        for (int i = 0; i < pointers.length; i++) {
            Pointer child = pointers[i];
            if (pointToParent.containsKey(child))
                throw new RuntimeException("Duplicate pointer at " + child);

            if (child instanceof BlockPointer blockPointer) {
                Long fileOffset = blockIndexes.get(blockPointer.getData());
                    if (fileOffset == null)
                        throw new IllegalStateException("Couldn't find block index for block " + blockPointer.getData());

                    out.write(writeU64(fileOffset));
                    continue;
            }

            pointToParent.put(child, new Entry<>(pointer, i));
            nextInQueue.add(child);
            out.write(new byte[8]);
        }

    }

    private void pointParentToChild(SeekableOutputStream indexesOut, long offset, long parentOffset, int index) throws IOException {
        indexesOut.position(parentOffset + 8 + index * 8L);
        indexesOut.write(writeU64(offset));
        indexesOut.position(offset);
    }

    private static Position add(Position a, Position b) {
        return new Position(
                a.getX() + b.getX(),
                a.getY() + b.getY(),
                a.getZ() + b.getZ()
        );
    }

    private static Position multiply(Position position, long value) {
        return new Position(
                position.getX() * value,
                position.getY() * value,
                position.getZ() * value
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
            byte[] originalBlockData = compressionEngines.get(blockInfo.compressionType()).decompress(compressedBlocks);
            if (originalBlockData.length != blockInfo.uncompressedSize())
                throw new EOFException("Block data length mismatch");

            if (crc32(originalBlockData) != blockInfo.checksum())
                throw new EOFException("Checksum mismatch");

            CompressionBlockInfo indexInfo = (CompressionBlockInfo) headers.get(Headers.INDEX_INFO);
            in.position(indexInfo.offset());
            byte[] compressedIndexInfo = in.readNBytes((int) indexInfo.uncompressedSize());
            byte[] originalIndexInfo = compressionEngines.get(indexInfo.compressionType()).decompress(compressedIndexInfo);
            if (originalIndexInfo.length != indexInfo.uncompressedSize())
                throw new EOFException("Index info length mismatch");

            if (crc32(originalIndexInfo) != indexInfo.checksum())
                throw new EOFException("Checksum mismatch");

            AxisOrder axes = (AxisOrder) headers.get(Headers.AXIS_ORDER);

            SeekableInputStream blockIn = new SeekableInputStream(ByteArraySeekableChannel.of(originalBlockData));
            BlockDataEncoder encoder = (BlockDataEncoder) headers.get(Headers.BLOCK_DATA_ENCODING);

            SeekableInputStream indexIn = new SeekableInputStream(ByteArraySeekableChannel.of(originalIndexInfo));
            DataInputStream indexDin = new DataInputStream(indexIn);

            int layers = indexIn.read();
            long scaler = (long) Math.pow(4, layers);

            Map<Position, BlockData> blocks = new HashMap<>();
            readIndexLayer(
                    indexIn,
                    indexDin,
                    indexIn.position(),
                    scaler,
                    Position.ZERO,
                    axes,
                    blocks,
                    encoder,
                    blockIn
            );


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

    private void readIndexLayer(SeekableInputStream in, DataInputStream din, long fileOffset, long scaler, Position actualPosition, AxisOrder axes, Map<Position, BlockData> result, BlockDataEncoder encoder, SeekableInputStream blockIn) throws IOException {
        if (scaler < 0)
            throw new IllegalStateException("Scaler must be positive!");

        in.position(fileOffset);

        boolean[] mask = toBooleanArray(readU64(din));
        for (int i = 0; i < mask.length; i++) {
            if (!mask[i])
                continue;

            int i0 = i % BlockChunk.SIZE;
            int i1 = (i / BlockChunk.SIZE) % BlockChunk.SIZE;
            int i2 = i / (BlockChunk.SIZE * BlockChunk.SIZE);

            Position chunkPoint = axes.position(i0, i1, i2);
            Position actualPos = add(actualPosition, multiply(chunkPoint, scaler));


            long offsetBefore = in.position();
            if (scaler == 1) {
                // READ BLock
                if (in.position() >= in.size())
                    throw new EOFException("Something went wrong");

                long newPos = readU64(din);
                blockIn.position(newPos);
                result.put(actualPos, encoder.read(blockIn));
                in.position(offsetBefore + 8);
                continue;
            }

            long nextFileOffset = readU64(din);
            readIndexLayer(
                    in,
                    din,
                    nextFileOffset,
                    scaler / 4,
                    actualPos,
                    axes,
                    result,
                    encoder,
                    blockIn
            );

            in.position(offsetBefore + 8L);
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
