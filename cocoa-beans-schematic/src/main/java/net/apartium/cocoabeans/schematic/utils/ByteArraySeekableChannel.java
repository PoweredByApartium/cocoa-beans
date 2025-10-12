package net.apartium.cocoabeans.schematic.utils;

import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.Arrays;

// todo maybe move to common?
@ApiStatus.AvailableSince("0.0.45")
public final class ByteArraySeekableChannel implements SeekableByteChannel {

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private byte[] buf;
    private int size;
    private long pos;
    private boolean open = true;

    public ByteArraySeekableChannel(int initialCapacity) {
        if (initialCapacity < 0) throw new IllegalArgumentException("initialCapacity < 0");
        this.buf = new byte[initialCapacity];
        this.size = 0;
        this.pos = 0;
    }

    public ByteArraySeekableChannel() {
        this(0);
    }

    public static ByteArraySeekableChannel of(byte[] data) {
        ByteArraySeekableChannel ch = new ByteArraySeekableChannel(data.length);
        ch.buf = Arrays.copyOf(data, data.length);
        ch.size = data.length;
        return ch;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        ensureOpen();
        if (pos >= size)
            return -1;
        int n = Math.min(dst.remaining(), (int) (size - pos));
        dst.put(buf, (int) pos, n);
        pos += n;
        return n;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        ensureOpen();
        int n = src.remaining();
        if (n == 0)
            return 0;
        growForWrite(pos, n);
        src.get(buf, (int) pos, n);
        pos += n;
        size = Math.max(size, (int) pos);
        return n;
    }

    @Override
    public long position() throws IOException {
        ensureOpen();
        return pos;
    }

    @Override
    public SeekableByteChannel position(long newPos) throws IOException {
        ensureOpen();
        if (newPos < 0 || newPos > MAX_ARRAY_SIZE)
            throw new IOException("position out of bounds: " + newPos);

        this.pos = newPos;
        return this;
    }

    @Override
    public long size() throws IOException {
        ensureOpen();
        return size;
    }

    @Override
    public SeekableByteChannel truncate(long newSize) throws IOException {
        ensureOpen();
        if (newSize < 0 || newSize > MAX_ARRAY_SIZE)
            throw new IOException("truncate out of bounds");

        int ns = (int) newSize;
        if (ns < size) {
            size = ns;
            if (pos > size)
                pos = size;
        }

        return this;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void close() {
        open = false;
    }

    public byte[] toByteArray() {
        return Arrays.copyOf(buf, size);
    }

    private void ensureOpen() throws IOException {
        if (!open)
            throw new IOException("channel closed");
    }

    private void growForWrite(long writePos, int len) throws IOException {
        long end = writePos + len;
        if (end > MAX_ARRAY_SIZE)
            throw new IOException("buffer too large");

        int required = (int) end;

        if (writePos > size) {
            ensureCapacity((int) writePos);
            Arrays.fill(buf, size, (int) writePos, (byte) 0);
            size = (int) writePos;
        }
        ensureCapacity(required);
    }

    private void ensureCapacity(int min) {
        if (min <= buf.length)
            return;

        int cur = buf.length;
        int grow = Math.max(min, (int) Math.min((long)cur + (cur >> 1) + 1, MAX_ARRAY_SIZE));
        buf = Arrays.copyOf(buf, grow);
    }
}