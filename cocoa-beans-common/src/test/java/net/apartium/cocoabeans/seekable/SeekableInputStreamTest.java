package net.apartium.cocoabeans.seekable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeekableInputStreamTest {

    @Test
    void readSingleBytesAndTrackPosition() throws IOException {
        ByteArraySeekableChannel channel = ByteArraySeekableChannel.of(new byte[] { 10, 20 });
        try (SeekableInputStream stream = new SeekableInputStream(channel)) {
            assertEquals(2, stream.size());
            assertEquals(0, stream.position());

            assertEquals(10, stream.read());
            assertEquals(1, stream.position());

            assertEquals(20, stream.read());
            assertEquals(2, stream.position());

            assertEquals(-1, stream.read());
        }
    }

    @Test
    void readIntoBufferUsesOffsetAndLength() throws IOException {
        ByteArraySeekableChannel channel = ByteArraySeekableChannel.of(new byte[] { 1, 2, 3, 4 });
        try (SeekableInputStream stream = new SeekableInputStream(channel)) {
            byte[] buffer = new byte[] { 9, 9, 9, 9, 9 };
            int read = stream.read(buffer, 1, 3);

            assertEquals(3, read);
            assertArrayEquals(new byte[] { 9, 1, 2, 3, 9 }, buffer);
            assertEquals(3, stream.position());
        }
    }

    @Test
    void readWithZeroLengthReturnsZeroAndDoesNotMove() throws IOException {
        ByteArraySeekableChannel channel = ByteArraySeekableChannel.of(new byte[] { 7 });
        try (SeekableInputStream stream = new SeekableInputStream(channel)) {
            byte[] buffer = new byte[1];
            assertEquals(0, stream.read(buffer, 0, 0));
            assertEquals(0, stream.position());
            assertEquals(7, stream.read());
        }
    }

    @Test
    void readAtEofReturnsMinusOne() throws IOException {
        ByteArraySeekableChannel channel = ByteArraySeekableChannel.of(new byte[] { 1 });
        try (SeekableInputStream stream = new SeekableInputStream(channel)) {
            byte[] buffer = new byte[1];
            assertEquals(1, stream.read(buffer, 0, 1));
            assertEquals(-1, stream.read(buffer, 0, 1));
            assertEquals(1, stream.position());
        }
    }

    @Test
    void readRejectsInvalidArguments() throws IOException {
        try (SeekableInputStream stream = new SeekableInputStream(new ByteArraySeekableChannel())) {
            assertThrows(NullPointerException.class, () -> stream.read(null, 0, 1));

            byte[] buffer = new byte[2];
            assertThrows(IndexOutOfBoundsException.class, () -> stream.read(buffer, -1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> stream.read(buffer, 0, -1));
            assertThrows(IndexOutOfBoundsException.class, () -> stream.read(buffer, 2, 1));
        }
    }

    @Test
    void skipAndAvailableAreBounded() throws IOException {
        ByteArraySeekableChannel channel = ByteArraySeekableChannel.of(new byte[] { 1, 2, 3, 4, 5 });
        try (SeekableInputStream stream = new SeekableInputStream(channel)) {
            assertEquals(5, stream.available());
            assertEquals(2, stream.skip(2));
            assertEquals(3, stream.available());
            assertEquals(3, stream.skip(10));
            assertEquals(0, stream.available());
            assertEquals(0, stream.skip(0));
        }
    }

    @Test
    void positionSetterMovesCursor() throws IOException {
        ByteArraySeekableChannel channel = ByteArraySeekableChannel.of(new byte[] { 1, 2, 3 });
        try (SeekableInputStream stream = new SeekableInputStream(channel)) {
            stream.position(1);
            assertEquals(1, stream.position());
            assertEquals(2, stream.read());
        }
    }

    @Test
    void closeClosesUnderlyingChannel() throws IOException {
        ByteArraySeekableChannel channel = new ByteArraySeekableChannel();
        SeekableInputStream stream = new SeekableInputStream(channel);
        stream.close();

        assertFalse(channel.isOpen());
        assertThrows(IOException.class, stream::read);
    }

    @Test
    void openCreatesFileIfMissing(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("data.bin");
        assertFalse(Files.exists(path));

        try (SeekableInputStream stream = SeekableInputStream.open(path)) {
            assertTrue(Files.exists(path));
            assertEquals(0, stream.size());
            assertEquals(-1, stream.read());
        }
    }

    @Test
    void openReadsExistingData(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("data.bin");
        Files.write(path, new byte[] { 8, 9 });

        try (SeekableInputStream stream = SeekableInputStream.open(path)) {
            byte[] buffer = new byte[2];
            assertEquals(2, stream.read(buffer, 0, 2));
            assertArrayEquals(new byte[] { 8, 9 }, buffer);
        }
    }
}
