package net.apartium.cocoabeans.schematic.utils;

import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

// todo move to common?
@ApiStatus.AvailableSince("0.0.45")
public final class SeekableInputStream extends InputStream {

    private final SeekableByteChannel channel;
    private final ByteBuffer one = ByteBuffer.allocate(1);

    public SeekableInputStream(SeekableByteChannel channel) {
        this.channel = channel;
    }

    /**
     * Open a file as SeekableInputStream and if the file doesn't exist we create one
     * @param path path to the file
     * @return new instance of SeekableInputStream to the file
     */
    public static SeekableInputStream open(Path path) throws IOException {
        return new SeekableInputStream(
                Files.newByteChannel(path,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE,
                        StandardOpenOption.READ
                )
        );
    }

    /**
     * Current position
     * @return current position
     */
    public long position() throws IOException {
        return channel.position();
    }

    /**
     * Jump to new position
     * @param newPos memory address
     */
    public void position(long newPos) throws IOException {
        channel.position(newPos);
    }

    /**
     * size of the entire channel
     * @return file size
     */
    public long size() throws IOException {
        return channel.size();
    }

    @Override
    public int read() throws IOException {
        one.clear();
        int n = channel.read(one);
        return (n == -1) ? -1 : (one.get(0) & 0xFF);
    }

    @Override
    public long skip(long n) throws IOException {
        long current = channel.position();
        long end = channel.size();
        long target = Math.max(0, Math.min(end, current + n));
        channel.position(target);
        return target - current;
    }

    @Override
    public int available() throws IOException {
        long remaining = channel.size() - channel.position();
        return (remaining > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) Math.max(0, remaining);
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
