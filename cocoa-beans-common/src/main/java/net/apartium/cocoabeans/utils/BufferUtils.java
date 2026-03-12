package net.apartium.cocoabeans.utils;

import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.zip.CRC32;

/**
 * Utility class for binary data, encoding, and serialization operations.
 * <p>
 * Provides static helper methods for reading and writing unsigned integers, strings, enums, UUIDs,
 * and for creating occupancy masks, CRC32 checksums, and boolean arrays from bitmasks. These methods
 * are useful for binary protocols, serialization, and data manipulation, but do not perform file I/O.
 * </p>
 * <p>
 * Typical use cases include working with network buffers, binary serialization formats, and low-level
 * data manipulation where precise control over byte representation is required.
 * </p>
 * @since 0.0.46
 */
@ApiStatus.AvailableSince("0.0.46")
public class BufferUtils {

    private BufferUtils() {}

    private static final CRC32 crc = new CRC32();

    /**
     * Reads an unsigned 16-bit integer (2 bytes) from the given DataInput.
     * @param in the DataInput to read from
     * @return the unsigned 16-bit integer as an int
     * @throws IOException if an I/O error occurs
     */
    public static int readU16(DataInput in) throws IOException {
        return (in.readUnsignedByte() << 8) | (in.readUnsignedByte());
    }

    /**
     * Reads an unsigned 24-bit integer (3 bytes) from the given DataInput.
     * @param in the DataInput to read from
     * @return the unsigned 24-bit integer as an int
     * @throws IOException if an I/O error occurs
     */
    public static int readU24(DataInput in) throws IOException {
        return (in.readUnsignedByte() << 16) | (in.readUnsignedByte() << 8) | (in.readUnsignedByte());
    }

    /**
     * Reads an unsigned 32-bit integer (4 bytes) from the given DataInput.
     * @param in the DataInput to read from
     * @return the unsigned 32-bit integer as a long
     * @throws IOException if an I/O error occurs
     */
    public static long readU32(DataInput in) throws IOException {
        return ((long) in.readUnsignedByte() << 24) | ((long) in.readUnsignedByte() << 16) | ((long) in.readUnsignedByte() << 8) | (in.readUnsignedByte());
    }

    /**
     * Reads an unsigned 64-bit integer (8 bytes) from the given DataInput.
     * @param in the DataInput to read from
     * @return the unsigned 64-bit integer as a long
     * @throws IOException if an I/O error occurs
     */
    public static long readU64(DataInput in) throws IOException {
        return ((long) in.readUnsignedByte() << 56) | ((long) in.readUnsignedByte() << 48) | ((long) in.readUnsignedByte() << 40) | ((long) in.readUnsignedByte() << 32)
                | ((long) in.readUnsignedByte() << 24) | ((long) in.readUnsignedByte() << 16) | ((long) in.readUnsignedByte() << 8) | (in.readUnsignedByte());
    }

    /**
     * Converts a 16-byte array to a UUID.
     * @param in the byte array (must be 16 bytes)
     * @return the UUID
     * @throws IllegalArgumentException if the array is not 16 bytes
     */
    public static UUID toUUID(byte[] in) {
        if (in.length != 16)
            throw new IllegalArgumentException();

        ByteBuffer bb = ByteBuffer.wrap(in);
        long mostSigBits = bb.getLong();
        long leastSigBits = bb.getLong();
        return new UUID(mostSigBits, leastSigBits);
    }

    /**
     * Reads a UTF-8 string from the given DataInput. The string is prefixed with its length as an unsigned 32-bit integer.
     * @param in the DataInput to read from
     * @return the decoded string
     * @throws IOException if an I/O error occurs
     */
    public static String readString(DataInput in) throws IOException {
        int len = (int) readU32(in);
        if (len < 0)
            throw new EOFException("Negative length!");

        return readString(in, len);
    }

    /**
     * Reads a UTF-8 string of the specified length from the given DataInput.
     * @param in the DataInput to read from
     * @param len the length of the string in bytes
     * @return the decoded string
     * @throws IOException if an I/O error occurs
     */
    public static String readString(DataInput in, int len) throws IOException {
        byte[] bytes = new byte[len];
        in.readFully(bytes);

        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Writes a 64-bit unsigned integer to a byte array (big-endian).
     * @param value the value to write
     * @return the byte array representing the value
     */
    public static byte[] writeU64(long value) {
        return new byte[]{
                (byte) (value >>> 56 & 0xFF),
                (byte) (value >>> 48 & 0xFF),
                (byte) (value >>> 40 & 0xFF),
                (byte) (value >>> 32 & 0xFF),
                (byte) (value >>> 24 & 0xFF),
                (byte) (value >>> 16 & 0xFF),
                (byte) (value >>> 8 & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    /**
     * Writes a 32-bit unsigned integer to a byte array (big-endian).
     * @param value the value to write
     * @return the byte array representing the value
     */
    public static byte[] writeU32(int value) {
        return new byte[]{
                (byte) (value >>> 24 & 0xFF),
                (byte) (value >>> 16 & 0xFF),
                (byte) (value >>> 8 & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    /**
     * Writes a 24-bit unsigned integer to a byte array (big-endian).
     * @param value the value to write
     * @return the byte array representing the value
     */
    public static byte[] writeU24(int value) {
        return new byte[]{
                (byte) (value >>> 16 & 0xFF),
                (byte) (value >>> 8 & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    /**
     * Writes a 16-bit unsigned integer to a byte array (big-endian).
     * @param value the value to write
     * @return the byte array representing the value
     */
    public static byte[] writeU16(int value) {
        return new byte[]{
                (byte) (value >>> 8 & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    /**
     * Encodes a string as a byte array prefixed with its length as a 32-bit unsigned integer.
     * @param value the string to encode
     * @return the byte array containing the length and UTF-8 encoded string
     */
    public static byte[] writeString(String value) {
        byte[] data = value.getBytes(StandardCharsets.UTF_8);
        int len = data.length;

        byte[] result = new byte[len + 4];
        System.arraycopy(writeU32(len), 0, result, 0, 4);
        System.arraycopy(data, 0, result, 4, len);

        return result;
    }

    /**
     * Encodes a string as a List of bytes, prefixed with its length as a 32-bit unsigned integer.
     * @param value the string to encode
     * @return the list of bytes containing the length and UTF-8 encoded string
     */
    public static List<Byte> writeStringAsList(String value) {
        byte[] data = value.getBytes(StandardCharsets.UTF_8);
        int len = data.length;

        List<Byte> result = new ArrayList<>(len + 4);
        for (byte b : writeU32(len))
            result.add(b);

        for (byte b : data)
            result.add(b);

        return result;
    }

    /**
     * Computes the CRC32 checksum of the given byte array.
     * @param data the data to checksum
     * @return the CRC32 checksum value
     */
    public static long crc32(byte[] data) {
        crc.reset();
        crc.update(data, 0, data.length);
        return crc.getValue();
    }

    /**
     * Converts a long value to a boolean array representing its bits.
     * @param data the long value
     * @return a boolean array of length 64, where each element represents a bit
     */
    public static boolean[] toBooleanArray(long data) {
        boolean[] result = new boolean[64];

        for (int i = 0; i < 64; i++) {
            result[i] = (data & (1L << i)) != 0;
        }

        return result;
    }

    /**
     * Reads an enum value from the DataInput, using a fallback ordinal if the name is not found.
     * @param in the DataInput to read from
     * @param clazz the enum class
     * @param values a supplier for the enum values array
     * @return the enum value
     */
    public static <T extends Enum<T>> T readEnum(DataInput in, Class<T> clazz, Supplier<T[]> values) throws IOException {
        String name = readString(in);
        int fallbackOrdinal = in.readInt();

        try {
            return Enum.valueOf(clazz, name);
        } catch (IllegalArgumentException e) {
            return values.get()[fallbackOrdinal];
        }
    }

    /**
     * Serializes an enum value to a byte array (name as string, then ordinal as int).
     * @param value the enum value
     * @return the byte array representing the enum
     */
    public static <T extends Enum<T>> byte[] writeEnum(Enum<T> value) throws IOException {
        String name = value.name();
        int ordinal = value.ordinal();

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteArray);

        out.write(writeString(name));
        out.writeInt(ordinal);

        return byteArray.toByteArray();
    }

    /**
     * Reads a map of enum keys to boolean values from a DataInputStream.
     * @param in the DataInputStream to read from
     * @param keyType the enum key class
     * @param keyValues a supplier for the enum key values array
     * @return the map of enum keys to booleans
     */
    public static <K extends Enum<K>> Map<K, Boolean> readMapOfEnumBoolean(DataInputStream in, Class<K> keyType, Supplier<K[]> keyValues) {
        Map<K, Boolean> result = new HashMap<>();

        try {
            while (in.available() > 0) {
                result.put(
                        readEnum(in, keyType, keyValues),
                        in.readBoolean()
                );
            }

            return result;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read enum map", e);
        }
    }

    /**
     * Serializes a map of enum keys to boolean values to a byte array.
     * @param map the map to serialize
     * @return the byte array representing the map
     */
    public static <K extends Enum<K>> byte[] writeMapOfEnumBoolean(Map<K, Boolean> map) {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream(1 + map.size() * 16);
        DataOutputStream out = new DataOutputStream(byteArray);

        try {
            for (Map.Entry<K, Boolean> entry : map.entrySet()) {
                out.write(writeEnum(entry.getKey()));
                out.writeBoolean(entry.getValue());
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write enum map", e);
        }

        return byteArray.toByteArray();
    }

    /**
     * Reads a map of enum keys to enum values from a DataInputStream.
     * @param in the DataInputStream to read from
     * @param keyType the enum key class
     * @param keyValues a supplier for the enum key values array
     * @param valueType the enum value class
     * @param valueValues a supplier for the enum value values array
     * @return the map of enum keys to enum values
     */
    public static <K extends Enum<K>, V extends Enum<V>> Map<K, V> readMapOfEnums(DataInputStream in, Class<K> keyType, Supplier<K[]> keyValues, Class<V> valueType, Supplier<V[]> valueValues) {
        Map<K, V> result = new HashMap<>();

        try {
            while (in.available() > 0) {
                result.put(
                        readEnum(in, keyType, keyValues),
                        readEnum(in, valueType, valueValues)
                );
            }

            return result;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read map of enums", e);
        }
    }

    /**
     * Serializes a map of enum keys to enum values to a byte array.
     * @param map the map to serialize
     * @return the byte array representing the map
     */
    public static <K extends Enum<K>, V extends Enum<V>> byte[] writeMapOfEnums(Map<K, V> map) {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream(1 + map.size() * 16);
        DataOutputStream out = new DataOutputStream(byteArray);

        try {
            for (Map.Entry<K, V> entry : map.entrySet()) {
                out.write(writeEnum(entry.getKey()));
                out.write(writeEnum(entry.getValue()));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write map of enums", e);
        }

        return byteArray.toByteArray();
    }

    /**
     * Creates an occupancy mask (bitmask) for a list, where each bit represents whether the element passes the filter.
     * @param list the list to create a mask for
     * @return the occupancy mask as a byte array
     */
    public static <T> byte[] createOccupancyMask(List<T> list) {
        return createOccupancyMask(list, Objects::nonNull);
    }

    /**
     * Creates an occupancy mask (bitmask) for a list, using a custom filter.
     * @param list the list to create a mask for
     * @param filter the filter to determine if an element is present
     * @return the occupancy mask as a byte array
     */
    public static <T> byte[] createOccupancyMask(List<T> list, Predicate<T> filter) {
        byte[] result = new byte[(int) Math.ceil(list.size() / 8.0)];

        for (int i = 0; i < list.size(); i++)
            result[i / 8] = (byte) (Byte.toUnsignedInt(result[i / 8]) | ((filter.test(list.get(i)) ? 1 : 0) << (i % 8)));

        return result;
    }

    /**
     * Creates an occupancy mask (bitmask) for an array, where each bit represents whether the element passes the filter.
     * @param arr the array to create a mask for
     * @return the occupancy mask as a byte array
     */
    public static <T> byte[] createOccupancyMask(T[] arr) {
        return createOccupancyMask(arr, Objects::nonNull);
    }

    /**
     * Creates an occupancy mask (bitmask) for an array, using a custom filter.
     * @param arr the array to create a mask for
     * @param filter the filter to determine if an element is present
     * @return the occupancy mask as a byte array
     */
    public static <T> byte[] createOccupancyMask(T[] arr, Predicate<T> filter) {
        byte[] result = new byte[(int) Math.ceil(arr.length / 8.0)];

        for (int i = 0; i < arr.length; i++)
            result[i / 8] = (byte) (Byte.toUnsignedInt(result[i / 8]) | ((filter.test(arr[i]) ? 1 : 0) << (i % 8)));

        return result;
    }

}
