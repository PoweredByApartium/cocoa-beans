package net.apartium.cocoabeans.seekable;

import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@ApiStatus.AvailableSince("0.0.46")
public final class SeekableOutputStream extends OutputStream {

    private final SeekableByteChannel channel;
    private final ByteBuffer one = ByteBuffer.allocate(1);

    public SeekableOutputStream(SeekableByteChannel channel) {
        this.channel = channel;
    }

    /**
     * Open a file as SeekableOutputStream and if the file doesn't exist we create one
     * @param path path to the file
     * @return new instance of SeekableOutputStream to the file
     */
    public static SeekableOutputStream open(Path path) throws IOException {
        return new SeekableOutputStream(
                Files.newByteChannel(
                        path,
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
    public void write(int b) throws IOException {
        one.clear();
        one.put((byte) b);
        one.flip();
        channel.write(one);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (b == null)
            throw new NullPointerException();

        if (off < 0 || len < 0 || off + len > b.length)
            throw new IndexOutOfBoundsException();

        ByteBuffer buf = ByteBuffer.wrap(b, off, len);
        channel.write(buf);
    }

    /**
     * SeekableByteChannel doesn't have a buffer so nothing we need to do
     */
    @Override
    public void flush() {
        // ignored
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}