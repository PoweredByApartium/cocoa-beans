package net.apartium.cocoabeans.seekable;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ByteArraySeekableChannelTest {

    @Test
    void constructorRejectsNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new ByteArraySeekableChannel(-1));
    }

    @Test
    void readWriteRoundTrip() throws IOException {
        ByteArraySeekableChannel channel = new ByteArraySeekableChannel();
        assertEquals(3, channel.write(ByteBuffer.wrap(new byte[] { 1, 2, 3 })));
        assertEquals(3, channel.position());
        assertEquals(3, channel.size());

        channel.position(0);
        ByteBuffer dst = ByteBuffer.allocate(3);
        assertEquals(3, channel.read(dst));
        assertArrayEquals(new byte[] { 1, 2, 3 }, dst.array());
        assertArrayEquals(new byte[] { 1, 2, 3 }, channel.toByteArray());

        assertEquals(-1, channel.read(ByteBuffer.allocate(1)));
    }

    @Test
    void ofCopiesData() throws IOException {
        byte[] data = new byte[] { 5, 6 };
        ByteArraySeekableChannel channel = ByteArraySeekableChannel.of(data);
        data[0] = 9;

        ByteBuffer dst = ByteBuffer.allocate(2);
        channel.read(dst);
        assertArrayEquals(new byte[] { 5, 6 }, dst.array());
    }

    @Test
    void writeBeyondCurrentSizeZeroFills() throws IOException {
        ByteArraySeekableChannel channel = new ByteArraySeekableChannel();
        channel.position(3);
        assertEquals(1, channel.write(ByteBuffer.wrap(new byte[] { 7 })));
        assertArrayEquals(new byte[] { 0, 0, 0, 7 }, channel.toByteArray());
    }

    @Test
    void truncateShrinksSizeAndMovesPosition() throws IOException {
        ByteArraySeekableChannel channel = new ByteArraySeekableChannel();
        channel.write(ByteBuffer.wrap(new byte[] { 1, 2, 3, 4 }));
        channel.position(4);

        channel.truncate(2);
        assertEquals(2, channel.size());
        assertEquals(2, channel.position());
        assertArrayEquals(new byte[] { 1, 2 }, channel.toByteArray());
    }

    @Test
    void operationsAfterCloseThrow() {
        ByteArraySeekableChannel channel = new ByteArraySeekableChannel();
        channel.close();
        assertFalse(channel.isOpen());

        assertThrows(IOException.class, () -> channel.read(ByteBuffer.allocate(1)));
        assertThrows(IOException.class, () -> channel.write(ByteBuffer.allocate(1)));
        assertThrows(IOException.class, channel::position);
        assertThrows(IOException.class, () -> channel.position(0));
        assertThrows(IOException.class, channel::size);
        assertThrows(IOException.class, () -> channel.truncate(0));
    }

    @Test
    void positionOutOfBoundsThrows() {
        ByteArraySeekableChannel channel = new ByteArraySeekableChannel();
        assertThrows(IOException.class, () -> channel.position(-1));
        assertThrows(IOException.class, () -> channel.position((long) Integer.MAX_VALUE));
    }

    @Test
    void writeZeroLengthReturnsZero() throws IOException {
        ByteArraySeekableChannel channel = new ByteArraySeekableChannel();
        assertEquals(0, channel.write(ByteBuffer.allocate(0)));
        assertEquals(0, channel.size());
    }
}
