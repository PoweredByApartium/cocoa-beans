package net.apartium.cocoabeans.space.schematic.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.zip.CRC32;

public class FileUtils {

    private static final CRC32 crc = new CRC32();

    public static int readU16(DataInput in) throws IOException {
        return (in.readUnsignedByte() << 8) | (in.readUnsignedByte());
    }

    public static int readU24(DataInput in) throws IOException {
        return (in.readUnsignedByte() << 16) | (in.readUnsignedByte() << 8) | (in.readUnsignedByte());
    }

    public static long readU32(DataInput in) throws IOException {
        return ((long) in.readUnsignedByte() << 24) | ((long) in.readUnsignedByte() << 16) | ((long) in.readUnsignedByte() << 8) | (in.readUnsignedByte());
    }

    public static long readU64(DataInput in) throws IOException {
        return ((long) in.readUnsignedByte() << 56) | ((long) in.readUnsignedByte() << 48) | ((long) in.readUnsignedByte() << 40) | ((long) in.readUnsignedByte() << 32)
                | ((long) in.readUnsignedByte() << 24) | ((long) in.readUnsignedByte() << 16) | ((long) in.readUnsignedByte() << 8) | (in.readUnsignedByte());
    }

    public static UUID toUUID(byte[] in) {
        if (in.length != 16)
            throw new IllegalArgumentException();

        ByteBuffer bb = ByteBuffer.wrap(in);
        long mostSigBits = bb.getLong();
        long leastSigBits = bb.getLong();
        return new UUID(mostSigBits, leastSigBits);
    }

    public static String readString(DataInput in) throws IOException {
        int len = (int) readU32(in);
        if (len < 0)
            throw new EOFException("Negative length!");

        byte[] bytes = new byte[len];
        in.readFully(bytes);

        return new String(bytes, StandardCharsets.UTF_8);
    }

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

    public static byte[] writeU32(int value) {
        return new byte[]{
                (byte) (value >>> 24 & 0xFF),
                (byte) (value >>> 16 & 0xFF),
                (byte) (value >>> 8 & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    public static byte[] writeU24(int value) {
        return new byte[]{
                (byte) (value >>> 16 & 0xFF),
                (byte) (value >>> 8 & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    public static byte[] writeU16(int value) {
        return new byte[]{
                (byte) (value >>> 8 & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    public static byte[] writeString(String value) {
        byte[] data = value.getBytes(StandardCharsets.UTF_8);
        int len = data.length;

        byte[] result = new byte[len + 4];
        System.arraycopy(writeU32(len), 0, result, 0, 4);
        System.arraycopy(data, 0, result, 4, len);

        return result;
    }

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
    public static long crc32(byte[] data) {
        crc.reset();
        crc.update(data, 0, data.length);
        return crc.getValue();
    }


    public static boolean[] toBooleanArray(byte[] data) {
        return toBooleanArray(data, data.length * 8);
    }

    public static boolean[] toBooleanArray(byte[] data, int bitCount) {
        boolean[] result = new boolean[bitCount];

        for (int i = 0; i < bitCount; i++) {
            int byteIndex = i >> 3;
            int bitIndex = i & 7;
            result[i] = ((data[byteIndex] >> bitIndex) & 1) == 1;
        }

        return result;
    }

    public static <T> byte[] createOccupancyMask(List<T> list) {
        return createOccupancyMask(list, Objects::nonNull);
    }

    public static <T> byte[] createOccupancyMask(List<T> list, Function<T, Boolean> filter) {
        byte[] result = new byte[(int) Math.ceil(list.size() / 8.0)];

        for (int i = 0; i < list.size(); i++)
            result[i / 8] = (byte) (result[i / 8] | ((filter.apply(list.get(i)) ? 1 : 0) << (i % 8)));

        return result;
    }

    public static <T> byte[] createOccupancyMask(T[] arr) {
        return createOccupancyMask(arr, Objects::nonNull);
    }

    public static <T> byte[] createOccupancyMask(T[] arr, Function<T, Boolean> filter) {
        byte[] result = new byte[(int) Math.ceil(arr.length / 8.0)];

        for (int i = 0; i < arr.length; i++)
            result[i / 8] = (byte) (result[i / 8] | ((filter.apply(arr[i]) ? 1 : 0) << (i % 8)));

        return result;
    }

}
