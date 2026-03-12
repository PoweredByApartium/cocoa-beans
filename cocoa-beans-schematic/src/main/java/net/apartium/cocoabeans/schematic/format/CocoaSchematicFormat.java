package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.Mathf;
import net.apartium.cocoabeans.schematic.block.*;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.compression.CompressionEngine;
import net.apartium.cocoabeans.seekable.ByteArraySeekableChannel;
import net.apartium.cocoabeans.seekable.SeekableInputStream;
import net.apartium.cocoabeans.seekable.SeekableOutputStream;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import net.apartium.cocoabeans.structs.MinecraftVersion;
import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.apartium.cocoabeans.utils.BufferUtils.*;

/**
 * The primary {@link SchematicFormat} implementation for the Cocoa Beans Schematic (CBSC) format.
 *
 * <h2>File layout</h2>
 * <pre>
 *   [4 bytes]  fingerprint  – ASCII "CBSC" ({@link #FINGERPRINT})
 *   [2 bytes]  version      – unsigned 16-bit format version ({@link #VERSION})
 *   [3 bytes]  header size  – unsigned 24-bit byte count of the header section that follows
 *   [header…]              – sequence of typed header entries (see {@link Headers})
 *   [body…]                – block-data section, index section, and optional body extensions;
 *                            each section's absolute offset and compressed size are stored in the
 *                            corresponding {@code *_INFO} header entry
 * </pre>
 *
 * <h2>Header entries</h2>
 * <p>Each entry begins with a 2-byte (unsigned 16-bit) tag from {@link Headers} followed by a
 * tag-specific payload. The header section ends when all declared bytes have been consumed.
 * Unknown tags are tolerated: their payload is read via a 3-byte length prefix and stored as a
 * raw byte array.</p>
 *
 * <h2>Body sections</h2>
 * <ul>
 *   <li><b>Block-data section</b> – one serialised {@link BlockData} entry per distinct block
 *       type, produced by the active {@link BlockDataEncoder}. Compressed with
 *       {@code defaultCompressionEngineForBlocks}.</li>
 *   <li><b>Index section</b> – spatial index mapping world positions to block-data offsets,
 *       produced by the active {@link IndexEncoder}. Compressed with
 *       {@code defaultCompressionEngineForIndexes}.</li>
 *   <li><b>Body extensions</b> – optional named blobs attached to the schematic via
 *       {@link BodyExtension}. Each extension has its own {@code BODY_EXTENSION} header entry
 *       and is compressed independently.</li>
 * </ul>
 *
 * <h2>Extensibility</h2>
 * <p>Block-data encoders, index encoders, compression engines, and body-extension formats are all
 * registered at construction time or via the {@code register*} / {@code setDefault*} methods,
 * making the format fully pluggable without subclassing.</p>
 *
 * @param <T> the concrete {@link Schematic} type produced by this format's {@link SchematicFactory}
 * @see BlockDataEncoder
 * @see IndexEncoder
 * @see BodyExtensionFormat
 */
@ApiStatus.AvailableSince("0.0.46")
public class CocoaSchematicFormat<T extends Schematic> implements SchematicFormat<T> {

    /** Magic bytes at the start of every CBSC file ({@value}). */
    public static final String FINGERPRINT = "CBSC";

    /** Number of bytes occupied by the fingerprint ({@value}). */
    public static final int FINGERPRINT_SIZE = 4;

    /** Format version written to and expected from every CBSC file ({@value}). */
    public static final int VERSION = 1;

    /**
     * Absolute byte offset in the output stream at which the header section begins.
     * Equals {@code FINGERPRINT_SIZE + 2 (version) + 3 (header length field)} = {@value}.
     */
    public static final long HEADER_START_INDEX = 9;

    /**
     * Tag constants for the typed header entries in a CBSC file.
     *
     * <p>Each constant is a 2-byte (unsigned 16-bit) tag that identifies a header entry type.
     * The tag is written immediately before the entry's payload in the header section.</p>
     */
    public static class Headers {

        private Headers() {}

        /** Axis-order entry: 1 byte axis-order identifier (see {@link AxisOrder#getId()}). */
        public static final int AXIS_ORDER = 0x02;

        /** Schematic origin offset: three signed 32-bit integers (x, y, z). */
        public static final int OFFSET = 0x03;

        /** Schematic dimensions: three unsigned 32-bit integers (width, height, depth). */
        public static final int SIZE = 0x04;

        /** Block-data encoder identifier: unsigned 16-bit encoder ID. */
        public static final int BLOCK_DATA_ENCODING = 0x05;

        /** Compression metadata for the block-data body section ({@link CompressionBlockInfo}). */
        public static final int BLOCK_INFO = 0x06;

        /** Index encoder identifier: unsigned 16-bit encoder ID. */
        public static final int INDEX_ENCODING = 0x07;

        /** Compression metadata for the index body section ({@link CompressionBlockInfo}). */
        public static final int INDEX_INFO = 0x08;

        /** Creation timestamp: unsigned 64-bit epoch milliseconds. */
        public static final int TIMESTAMP = 0x09;

        /** Optional author string: length-prefixed UTF-8. */
        public static final int AUTHOR = 0x0A;

        /** Optional title string: length-prefixed UTF-8. */
        public static final int TITLE = 0x0B;

        /**
         * Minecraft platform entry: four unsigned 32-bit version fields
         * (major, update, minor, protocol) followed by two length-prefixed UTF-8 strings
         * (platform name, platform version).
         */
        public static final int PLATFORM = 0x0C;

        /**
         * Body-extension entry (repeatable): unsigned 64-bit extension ID followed by
         * {@link CompressionBlockInfo} metadata for the extension's body section.
         */
        public static final int BODY_EXTENSION = 0x0E;

    }

    /** Map from encoder ID to the {@link BlockDataEncoder} registered for that ID. */
    private final Map<Integer, BlockDataEncoder> blockDataEncoderMap;

    /** Map from encoder ID to the {@link IndexEncoder} registered for that ID. */
    private final Map<Integer, IndexEncoder> indexEncoderMap;

    /** Map from compression-type byte to the {@link CompressionEngine} registered for it. */
    private final Map<Byte, CompressionEngine> compressionEngines;

    /** Map from body-extension ID to the {@link BodyExtensionFormat} used to read/write it. */
    private final Map<Long, BodyExtensionFormat<?>> bodyExtensionFormat;

    /** Compression engine applied to the block-data body section when writing. */
    private CompressionEngine defaultCompressionEngineForBlocks;

    /** Compression engine applied to the index body section when writing. */
    private CompressionEngine defaultCompressionEngineForIndexes;

    /** Compression engine applied to body-extension sections when writing. */
    private CompressionEngine defaultCompressionEngineForBodyExtensions;

    /** The block-data encoder (and its registered ID) used when writing a new schematic. */
    private Map.Entry<Integer, BlockDataEncoder> preferBlockEncoder;

    /** The index encoder (and its registered ID) used when writing a new schematic. */
    private Map.Entry<Integer, IndexEncoder> preferIndexEncoder;

    /** Factory that constructs the concrete {@link Schematic} instance during {@link #read}. */
    private final SchematicFactory<T> schematicFactory;

    /**
     * Creates a new {@code CocoaSchematicFormat} with the given encoders and compression settings.
     *
     * <p>The first entry of each encoder map is used as the preferred encoder for writing unless
     * overridden by {@link #setPreferBlockEncoder} or {@link #setPreferIndexEncoder}.</p>
     *
     * @param blockDataEncoderMap       map from encoder ID to {@link BlockDataEncoder}
     * @param indexEncoderMap           map from encoder ID to {@link IndexEncoder}
     * @param compressionEngines        set of available {@link CompressionEngine} implementations
     * @param defaultCompressionForBlock     compression-type byte used for the block-data section
     * @param defaultCompressionForIndexes   compression-type byte used for the index section
     * @param schematicFactory          factory used to construct {@link Schematic} instances on read
     */
    public CocoaSchematicFormat(Map<Integer, BlockDataEncoder> blockDataEncoderMap, Map<Integer, IndexEncoder> indexEncoderMap, Set<CompressionEngine> compressionEngines, byte defaultCompressionForBlock, byte defaultCompressionForIndexes, SchematicFactory<T> schematicFactory) {
        this.blockDataEncoderMap = new HashMap<>(blockDataEncoderMap);
        if (!blockDataEncoderMap.isEmpty())
            preferBlockEncoder = blockDataEncoderMap.entrySet().iterator().next();

        this.indexEncoderMap = new HashMap<>(indexEncoderMap);
        if (!indexEncoderMap.isEmpty())
            preferIndexEncoder = indexEncoderMap.entrySet().iterator().next();

        this.compressionEngines = compressionEngines.stream().collect(Collectors.toMap(CompressionEngine::type, Function.identity()));

        this.defaultCompressionEngineForBlocks = this.compressionEngines.get(defaultCompressionForBlock);
        this.defaultCompressionEngineForIndexes = this.compressionEngines.get(defaultCompressionForIndexes);
        this.defaultCompressionEngineForBodyExtensions = this.compressionEngines.get(defaultCompressionForBlock);

        this.schematicFactory = schematicFactory;
        this.bodyExtensionFormat = new HashMap<>();
    }

    /**
     * Registers a {@link BodyExtensionFormat} for the given extension ID.
     *
     * @param id     unique identifier for the body extension
     * @param format format used to read and write the extension's payload
     */
    public void registerBodyExtensionFormat(long id, BodyExtensionFormat<?> format) {
        bodyExtensionFormat.put(id, format);
    }

    /**
     * Registers multiple {@link BodyExtensionFormat} entries at once.
     *
     * @param bodyExtensionFormat map from extension ID to its format
     */
    public void registerAllBodyExtensionsFormat(Map<Long, BodyExtensionFormat<?>> bodyExtensionFormat) {
        this.bodyExtensionFormat.putAll(bodyExtensionFormat);
    }

    /**
     * Registers a {@link BlockDataEncoder} under the given ID.
     *
     * <p>The registered encoder can be selected during reading when a file declares this ID in its
     * {@link Headers#BLOCK_DATA_ENCODING} header entry.</p>
     *
     * @param id      the numeric encoder identifier written to and read from the file header
     * @param encoder the encoder implementation to register
     */
    public void registerBlockEncoder(int id, BlockDataEncoder encoder) {
        blockDataEncoderMap.put(id, encoder);
    }

    /**
     * Registers an {@link IndexEncoder} under the given ID.
     *
     * <p>The registered encoder can be selected during reading when a file declares this ID in its
     * {@link Headers#INDEX_ENCODING} header entry.</p>
     *
     * @param id      the numeric encoder identifier written to and read from the file header
     * @param encoder the encoder implementation to register
     */
    public void registerIndexEncoder(int id, IndexEncoder encoder) {
        indexEncoderMap.put(id, encoder);
    }

    /**
     * Sets the {@link BlockDataEncoder} that will be used when writing new schematic files.
     *
     * <p>Also registers {@code encoder} under {@code id} so it can be resolved during reads.</p>
     *
     * @param id      the numeric encoder identifier to write in the file header
     * @param encoder the preferred encoder
     */
    public void setPreferBlockEncoder(int id, BlockDataEncoder encoder) {
        preferBlockEncoder = Map.entry(id, encoder);
    }

    /**
     * Sets the {@link IndexEncoder} that will be used when writing new schematic files.
     *
     * <p>Also registers {@code encoder} under {@code id} so it can be resolved during reads.</p>
     *
     * @param id      the numeric encoder identifier to write in the file header
     * @param encoder the preferred encoder
     */
    public void setPreferIndexEncoder(int id, IndexEncoder encoder) {
        preferIndexEncoder = Map.entry(id, encoder);
    }

    /**
     * Registers a {@link CompressionEngine} under the given type byte.
     *
     * @param id     the compression-type identifier stored in {@link CompressionBlockInfo}
     * @param engine the engine implementation to register
     */
    public void registerCompressionEngine(byte id, CompressionEngine engine) {
        compressionEngines.put(id, engine);
    }

    /**
     * Sets the compression engine used for the block-data body section when writing.
     *
     * @param engine the engine to use
     */
    public void setDefaultCompressionEngineForBlocks(CompressionEngine engine) {
        defaultCompressionEngineForBlocks = engine;
    }

    /**
     * Sets the compression engine used for the index body section when writing.
     *
     * @param engine the engine to use
     */
    public void setDefaultCompressionEngineForIndexes(CompressionEngine engine) {
        defaultCompressionEngineForIndexes = engine;
    }

    /**
     * Sets the compression engine used for body-extension sections when writing.
     *
     * @param engine the engine to use
     */
    public void setDefaultCompressionEngineForBodyExtensions(CompressionEngine engine) {
        defaultCompressionEngineForBodyExtensions = engine;
    }

    /**
     * Serialises a single {@link BodyExtension} into the output stream and back-patches the
     * corresponding {@link Headers#BODY_EXTENSION} header entry with the resulting
     * {@link CompressionBlockInfo}.
     *
     * @param out             the output stream to write the compressed extension data into
     * @param bodyExtension   the extension to serialise
     * @param headerResult    the pre-written header result whose body-extension offset map is used
     *                        to locate the placeholder in the header
     * @throws IllegalStateException if the extension ID was not declared in the header or if no
     *                               format is registered for it
     * @throws IOException           if an I/O error occurs
     */
    private <E> void writeBodyExtension(SeekableOutputStream out, BodyExtension<E> bodyExtension, HeaderResult headerResult) throws IOException {
        Long offset = headerResult.bodyExtensionsOffset().get(bodyExtension.id());
        if (offset == null)
            throw new IllegalStateException("Body extension not found in header!\nSomething went wrong with schematic!");

        if (!bodyExtensionFormat.containsKey(bodyExtension.id()))
            throw new IllegalStateException("Unknown body extension format: " + bodyExtension.id());

        BodyExtensionFormat<E> format = (BodyExtensionFormat<E>) bodyExtensionFormat.get(bodyExtension.id());

        byte[] data = format.write(bodyExtension);
        byte[] compressed = compressionEngines.get(defaultCompressionEngineForBodyExtensions.type()).compress(data);

        CompressionBlockInfo bodyExtensionInfo = new CompressionBlockInfo(
                defaultCompressionEngineForBodyExtensions.type(),
                data.length,
                compressed.length,
                out.position(),
                crc32(data)
        );

        out.position(HEADER_START_INDEX + offset);
        out.write(bodyExtensionInfo.toByteArray());

        out.position(bodyExtensionInfo.offset());
        out.write(compressed);
    }

    /**
     * Serialises {@code schematic} to {@code out} in the CBSC format.
     *
     * <p>The method writes the fingerprint and version, generates the header section (with
     * placeholder entries for block and index compression info), encodes and compresses the
     * block-data and index body sections, back-patches the header placeholders with the actual
     * {@link CompressionBlockInfo} values, and finally writes any body extensions.</p>
     *
     * @param schematic the schematic to serialise
     * @param out       the output stream to write to; must support seeking for back-patching
     * @throws UncheckedIOException if an I/O error occurs
     */
    @Override
    public void write(T schematic, SeekableOutputStream out) {
        try {
            for (int i = 0; i < FINGERPRINT_SIZE; i++)
                out.write(FINGERPRINT.charAt(i));

            out.write(writeU16(VERSION));

            Map.Entry<Integer, BlockDataEncoder> blockEncoder = preferBlockEncoder;
            Map.Entry<Integer, IndexEncoder> indexEncoder = preferIndexEncoder;

            HeaderResult headerResult = headers(schematic, blockEncoder, indexEncoder);
            byte[] headers = headerResult.data;
            out.write(writeU24(headers.length));
            out.write(headers);

            BlockIterator iterator = schematic.blocksIterator();
            Map<BlockData, Long> blockIndexes = new IdentityHashMap<>();
            ByteArrayOutputStream blockOut = new ByteArrayOutputStream();
            long offset = 0;

            final AxisOrder axes = schematic.axisOrder();

            AreaSize dimensions = schematic.size();

            long maxDimensions = (long) Math.max(dimensions.width(), Math.max(dimensions.height(), dimensions.depth()));
            long scaler = Mathf.nextPowerOfFour(maxDimensions);
            MutableBlockChunk blockChunk = new MutableBlockChunkImpl(axes, scaler, Position.ZERO, Position.ZERO);

            while (iterator.hasNext()) {
                BlockPlacement placement = iterator.next();
                BlockData blockData = placement.block();
                blockChunk.setBlock(placement);

                if (blockIndexes.containsKey(blockData))
                    continue;

                blockIndexes.put(blockData, offset);

                byte[] data = blockEncoder.getValue().write(blockData);
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


            byte[] indexesAsBytes = indexEncoder.getValue().write(schematic.blocksIterator(), schematic.axisOrder(), blockIndexes);
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

            for (BodyExtension<?> bodyExtension : schematic.bodyExtensions())
                writeBodyExtension(out, bodyExtension, headerResult);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Captures the serialised header bytes together with the byte offsets (relative to
     * {@link #HEADER_START_INDEX}) at which the block-info and index-info placeholders were
     * written, so they can be back-patched after the body sections are finalised.
     *
     * @param data                 the raw header bytes
     * @param offsetBlockInfo      byte offset of the {@link Headers#BLOCK_INFO} placeholder
     * @param offsetIndexInfo      byte offset of the {@link Headers#INDEX_INFO} placeholder
     * @param bodyExtensionsOffset map from body-extension ID to the byte offset of its
     *                             {@link CompressionBlockInfo} placeholder in the header
     */
    private record HeaderResult(byte[] data, long offsetBlockInfo, long offsetIndexInfo, Map<Long, Long> bodyExtensionsOffset) { }

    /**
     * Writes the optional {@link Headers#AUTHOR} entry to {@code out} if the metadata contains
     * a non-null author string. Does nothing otherwise.
     *
     * @param metadata schematic metadata to read the author from
     * @param out      the header output stream
     * @throws IOException if an I/O error occurs
     */
    private void writeAuthorHeader(SchematicMetadata metadata, OutputStream out) throws IOException {
        String author = metadata.author();
        if (author == null)
            return;

        out.write(writeU16(Headers.AUTHOR));
        out.write(writeString(author));
    }

    /**
     * Writes the optional {@link Headers#TITLE} entry to {@code out} if the metadata contains
     * a non-null title string. Does nothing otherwise.
     *
     * @param metadata schematic metadata to read the title from
     * @param out      the header output stream
     * @throws IOException if an I/O error occurs
     */
    private void writeTitleHeader(SchematicMetadata metadata, OutputStream out) throws IOException {
        String title = metadata.title();
        if (title == null)
            return;

        out.write(writeU16(Headers.TITLE));
        out.write(writeString(title));
    }

    /**
     * Builds the full header section for the given schematic.
     *
     * <p>Writes all mandatory header entries in a fixed order, followed by optional entries
     * ({@link Headers#AUTHOR}, {@link Headers#TITLE}) and one {@link Headers#BODY_EXTENSION}
     * entry per body extension. The {@link Headers#BLOCK_INFO} and {@link Headers#INDEX_INFO}
     * entries are written as zero-filled placeholders; their actual values are back-patched by
     * {@link #write} after the body sections are serialised.</p>
     *
     * @param schematic    the schematic whose metadata and settings are serialised
     * @param blockEncoder the block-data encoder entry (id + instance) to record in the header
     * @param indexEncoder the index encoder entry (id + instance) to record in the header
     * @return a {@link HeaderResult} containing the header bytes and placeholder offsets
     * @throws IOException if an I/O error occurs while building the header
     */
    private HeaderResult headers(T schematic, Map.Entry<Integer, BlockDataEncoder> blockEncoder, Map.Entry<Integer, IndexEncoder> indexEncoder) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bOut);

        out.write(writeU16(Headers.AXIS_ORDER));
        out.write(schematic.axisOrder().getId());

        out.write(writeU16(Headers.OFFSET));
        out.writeInt((int) schematic.offset().getX());
        out.writeInt((int) schematic.offset().getY());
        out.writeInt((int) schematic.offset().getZ());

        out.write(writeU16(Headers.SIZE));
        out.write(writeU32((int) schematic.size().width()));
        out.write(writeU32((int) schematic.size().height()));
        out.write(writeU32((int) schematic.size().depth()));

        out.write(writeU16(Headers.BLOCK_DATA_ENCODING));
        out.write(writeU16(blockEncoder.getKey()));

        out.write(writeU16(Headers.BLOCK_INFO));
        int blockInfoIndex = out.size();
        out.write(new byte[CompressionBlockInfo.SIZE]);

        out.write(writeU16(Headers.INDEX_ENCODING));
        out.write(writeU16(indexEncoder.getKey()));

        out.write(writeU16(Headers.INDEX_INFO));
        int indexInfoIndex = out.size();
        out.write(new byte[CompressionBlockInfo.SIZE]);

        out.write(writeU16(Headers.TIMESTAMP));
        out.write(writeU64(schematic.created().toEpochMilli()));

        SchematicMetadata metadata = schematic.metadata();

        writeAuthorHeader(metadata, out);
        writeTitleHeader(metadata, out);

        MinecraftPlatform platform = schematic.originPlatform();

        out.write(writeU16(Headers.PLATFORM));
        out.write(writeU32(platform.version().major()));
        out.write(writeU32(platform.version().update()));
        out.write(writeU32(platform.version().minor()));
        out.write(writeU32(platform.version().protocol()));
        out.write(writeString(platform.platformName()));
        out.write(writeString(platform.platformVersion()));

        Map<Long, Long> bodyExtensionsOffset = new HashMap<>();
        for (BodyExtension<?> bodyExtension : schematic.bodyExtensions()) {
            out.write(writeU16(Headers.BODY_EXTENSION));
            out.write(writeU64(bodyExtension.id()));

            long offset = out.size();
            out.write(new byte[CompressionBlockInfo.SIZE]);

            bodyExtensionsOffset.put(bodyExtension.id(), offset);
        }

        return new HeaderResult(
                bOut.toByteArray(),
                blockInfoIndex,
                indexInfoIndex,
                bodyExtensionsOffset
        );
    }

    /**
     * Validates the 4-byte CBSC fingerprint at the current stream position.
     *
     * @param in the stream to read from, positioned at the very start of the file
     * @throws EOFException if any fingerprint byte does not match {@link #FINGERPRINT}
     * @throws IOException  if an I/O error occurs
     */
    private void ensuresFingerprint(SeekableInputStream in) throws IOException {
        for (int i = 0; i < FINGERPRINT_SIZE; i++) {
            if (in.read() != FINGERPRINT.charAt(i))
                throw new EOFException("Invalid fingerprint!");
        }
    }

    /**
     * Reads and validates the 2-byte format version from the stream.
     *
     * @param in the data stream positioned immediately after the fingerprint
     * @throws UnsupportedEncodingException if the version does not equal {@link #VERSION}
     * @throws IOException                  if an I/O error occurs
     */
    private void ensuresVersion(DataInputStream in) throws IOException {
        int version = readU16(in);
        if (version != VERSION)
            throw new UnsupportedEncodingException("Version aren't match\nexpected: " + VERSION + "\ngot: " + version);
    }

    /**
     * Reads all body extensions whose IDs are present in {@code bodyExtensionCompressionInfo} and
     * populates {@code bodyExtensions} with the results.
     *
     * <p>Extensions whose format is not registered are skipped (null is returned by
     * {@link #readBodyExtension} and the entry is omitted).</p>
     *
     * @param in                         the file stream used to seek to each extension's body
     * @param bodyExtensions             output map populated with the deserialised extensions
     * @param bodyExtensionCompressionInfo map from extension ID to its {@link CompressionBlockInfo}
     * @throws IOException if an I/O error occurs while reading any extension
     */
    private void readBodyExtensions(SeekableInputStream in, Map<Long, BodyExtension<?>> bodyExtensions, Map<Long, CompressionBlockInfo> bodyExtensionCompressionInfo) throws IOException {
        for (Map.Entry<Long, CompressionBlockInfo> infoEntry : bodyExtensionCompressionInfo.entrySet()) {
            BodyExtension<?> bodyExtension = readBodyExtension(in, infoEntry.getKey(), infoEntry.getValue());
            if (bodyExtension == null)
                continue;

            bodyExtensions.put(infoEntry.getKey(), bodyExtension);
        }
    }

    /**
     * Deserialises a single body extension from the file stream.
     *
     * <p>The method seeks to the extension's compressed body, decompresses it, verifies the
     * uncompressed size and CRC-32 checksum, and delegates to the registered
     * {@link BodyExtensionFormat} for the final decode.</p>
     *
     * @param in                  the file stream
     * @param bodyExtensionId     the extension's unique identifier
     * @param bodyExtensionInfo   compression metadata (offset, sizes, checksum) for the extension
     * @return the deserialised {@link BodyExtension}
     * @throws IllegalArgumentException if no format is registered for {@code bodyExtensionId}
     * @throws EOFException             if the decompressed size or checksum does not match
     * @throws IOException              if an I/O error occurs
     */
    private BodyExtension<?> readBodyExtension(SeekableInputStream in, long bodyExtensionId, CompressionBlockInfo bodyExtensionInfo) throws IOException {
        BodyExtensionFormat<?> format = bodyExtensionFormat.get(bodyExtensionId);
        if (format == null)
            throw new IllegalArgumentException("Unknown body extension format: " + bodyExtensionId);

        in.position(bodyExtensionInfo.offset());

        byte[] compressedBodyExtensionInfo = in.readNBytes((int) bodyExtensionInfo.compressedSize());
        byte[] originalBodyExtensionInfo = compressionEngines.get(bodyExtensionInfo.compressionType()).decompress(compressedBodyExtensionInfo);
        if (originalBodyExtensionInfo.length != bodyExtensionInfo.uncompressedSize())
            throw new EOFException("Body Extensions info length mismatch\nExpected: " + bodyExtensionInfo.uncompressedSize() + "\nActual:" + originalBodyExtensionInfo.length);

        long hash = crc32(originalBodyExtensionInfo);
        if (hash != bodyExtensionInfo.checksum())
            throw new EOFException("Checksum mismatch\nExpected: " + bodyExtensionInfo.checksum() + "\nActual:" + hash);

        return format.read(new ByteArrayInputStream(originalBodyExtensionInfo), originalBodyExtensionInfo.length);
    }

    /**
     * Deserialises a CBSC schematic from {@code in} and returns an instance of {@code T}.
     *
     * <p>The method validates the fingerprint and version, parses the header section, then reads
     * and decompresses the block-data and index body sections. Each section's integrity is
     * verified against the stored uncompressed size and CRC-32 checksum. The index is decoded via
     * the {@link IndexEncoder} declared in the header, which in turn uses the
     * {@link BlockDataEncoder} to reconstruct individual block states. Body extensions are decoded
     * last. The resulting data is passed to the configured {@link SchematicFactory} to produce the
     * final schematic object.</p>
     *
     * @param in the seekable stream positioned at the start of the CBSC file
     * @return the deserialised schematic
     * @throws UncheckedIOException if any I/O, integrity, or version check fails
     */
    @Override
    public T read(SeekableInputStream in) {
        try {
            DataInputStream din = new DataInputStream(in);

            ensuresFingerprint(in);
            ensuresVersion(din);

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
            byte[] compressedIndexInfo = in.readNBytes((int) indexInfo.compressedSize());
            byte[] originalIndexInfo = compressionEngines.get(indexInfo.compressionType()).decompress(compressedIndexInfo);
            if (originalIndexInfo.length != indexInfo.uncompressedSize())
                throw new EOFException("Index info length mismatch");

            if (crc32(originalIndexInfo) != indexInfo.checksum())
                throw new EOFException("Checksum mismatch");

            AxisOrder axes = (AxisOrder) headers.get(Headers.AXIS_ORDER);

            SeekableInputStream blockIn = new SeekableInputStream(ByteArraySeekableChannel.of(originalBlockData));
            BlockDataEncoder blockEncoder = (BlockDataEncoder) headers.get(Headers.BLOCK_DATA_ENCODING);

            SeekableInputStream indexIn = new SeekableInputStream(ByteArraySeekableChannel.of(originalIndexInfo));
            IndexEncoder indexEncoder = (IndexEncoder) headers.get(Headers.INDEX_ENCODING);
            BlockIterator blocks = indexEncoder.read(indexIn, axes, blockEncoder, blockIn);

            Map<Long, BodyExtension<?>> bodyExtensions = new HashMap<>();
            if (headers.getOrDefault(Headers.BODY_EXTENSION, null) instanceof Map<?, ?> map) {
                Map<Long, CompressionBlockInfo> bodyExtensionCompressionInfo = (Map<Long, CompressionBlockInfo>) map;
                readBodyExtensions(in, bodyExtensions, bodyExtensionCompressionInfo);
            }

            Map<String, Object> metadata = new HashMap<>();
            if (headers.containsKey(Headers.AUTHOR))
                metadata.put("author", headers.get(Headers.AUTHOR));

            if (headers.containsKey(Headers.TITLE))
                metadata.put("title", headers.get(Headers.TITLE));

            return schematicFactory.createSchematic(
                    (Instant) headers.get(Headers.TIMESTAMP),
                    (MinecraftPlatform) headers.get(Headers.PLATFORM),
                    SchematicMetadata.of(metadata),
                    blocks,
                    (AreaSize) headers.get(Headers.SIZE),
                    (AxisOrder) headers.get(Headers.AXIS_ORDER),
                    (Position) headers.get(Headers.OFFSET),
                    bodyExtensions
            );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Asserts that all mandatory header entries are present in {@code headers}.
     *
     * @param headers the parsed header map produced by {@link #parseHeaders}
     * @throws IllegalStateException if any required entry is missing
     */
    private void assertBaseHeaders(Map<Integer, Object> headers) {
        if (headers.isEmpty())
            throw new IllegalStateException("No headers found!");

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

        if (!headers.containsKey(Headers.INDEX_ENCODING))
            throw new IllegalStateException("IndexEncoding Header not found!");

        if (!headers.containsKey(Headers.INDEX_INFO))
            throw new IllegalStateException("IndexInfo Header not found!");

        if (!headers.containsKey(Headers.TIMESTAMP))
            throw new IllegalStateException("Timestamp Header not found!");

        if (!headers.containsKey(Headers.PLATFORM))
            throw new IllegalStateException("Platform Header not found!");
    }

    /**
     * Parses the raw header bytes into a map from header tag to its decoded value.
     *
     * <p>Each iteration reads a 2-byte tag and delegates to a tag-specific branch that consumes
     * the payload and stores the decoded value. Unknown tags are handled by a default branch that
     * reads a 3-byte length prefix and stores the raw payload bytes.</p>
     *
     * @param headers the raw header section bytes (excluding the 3-byte length prefix)
     * @return map from {@link Headers} constant to the decoded header value
     * @throws IOException if an I/O error occurs while parsing a header entry
     */
    private Map<Integer, Object> parseHeaders(byte[] headers) throws IOException {
        int index = 0;

        Map<Integer, Object> map = new HashMap<>();

        while (index < headers.length) {
            int id = (headers[index] << 8) | (Byte.toUnsignedInt(headers[index + 1]));
            index += 2;

            index = switch (id) {
                case Headers.AXIS_ORDER -> {
                    map.put(Headers.AXIS_ORDER, AxisOrder.byId(headers[index]));
                    yield index + 1;
                }

                case Headers.OFFSET -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 12)));
                    map.put(Headers.OFFSET, new Position(in.readInt(), in.readInt(), in.readInt()));
                    yield index + 12;
                }

                case Headers.SIZE -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 12)));
                    map.put(Headers.SIZE, new AreaSize(readU32(in), readU32(in), readU32(in)));
                    yield index + 12;
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

                case Headers.INDEX_ENCODING -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 2)));
                    map.put(Headers.INDEX_ENCODING, indexEncoderMap.get(readU16(in)));
                    yield index + 2;
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

                case Headers.PLATFORM -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(headers, index, index + 20));

                    MinecraftVersion version = new MinecraftVersion(
                            (int) readU32(in),
                            (int) readU32(in),
                            (int) readU32(in),
                            (int) readU32(in)
                    );

                    int lengthName = (int) readU32(in);
                    in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index + 16, index + 20 + lengthName)));
                    String platformName = readString(in);

                    in = new DataInputStream(new ByteArrayInputStream(headers, index + 20 + lengthName, index + 24 + lengthName));
                    int lengthPlatform = (int) readU32(in);
                    in = new DataInputStream(new ByteArrayInputStream(headers, index + 20 + lengthName, index + 24 + lengthName + lengthPlatform));
                    String platformVersion = readString(in);

                    map.put(Headers.PLATFORM, new MinecraftPlatform(version, platformName, platformVersion));
                    yield index + 24 + lengthName + lengthPlatform;

                }

                case Headers.BODY_EXTENSION -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 8)));
                    long bodyExtensionId = readU64(in);

                    CompressionBlockInfo info = CompressionBlockInfo.fromBytes(splitFromAToB(headers, index + 8, index + 8 + CompressionBlockInfo.SIZE));

                    if (map.computeIfAbsent(Headers.BODY_EXTENSION, key -> new HashMap<>()) instanceof Map<?, ?> bodyExtensionMap)
                        ((Map<Long, CompressionBlockInfo>) bodyExtensionMap).put(bodyExtensionId, info);

                    yield index + 8 + CompressionBlockInfo.SIZE;
                }

                default -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 3)));
                    int length = readU24(in);
                    map.put(id, splitFromAToB(headers, index + 3, index + 3 + length));
                    yield index + 3 + length;

                }
            };
        }

        return map;
    }

    /**
     * Returns a copy of the bytes in {@code data} from index {@code a} (inclusive) to index
     * {@code b} (exclusive).
     *
     * @param data the source array
     * @param a    start index (inclusive)
     * @param b    end index (exclusive)
     * @return a new byte array containing {@code data[a..b)}
     * @throws IndexOutOfBoundsException if {@code a >= data.length} or {@code b > data.length}
     */
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
