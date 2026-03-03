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

class SeekableOutputStreamTest {

    @Test
    void writeSingleBytesAndTrackPosition() throws IOException {
        ByteArraySeekableChannel channel = new ByteArraySeekableChannel();
        try (SeekableOutputStream stream = new SeekableOutputStream(channel)) {
            stream.write(1);
            stream.write(0xFF);

            assertEquals(2, stream.position());
            assertEquals(2, stream.size());
            assertArrayEquals(new byte[] { 1, (byte) 0xFF }, channel.toByteArray());
        }
    }

    @Test
    void writeByteArrayWithOffset() throws IOException {
        ByteArraySeekableChannel channel = new ByteArraySeekableChannel();
        try (SeekableOutputStream stream = new SeekableOutputStream(channel)) {
            stream.write(new byte[] { 9, 1, 2, 3, 8 }, 1, 3);

            assertEquals(3, stream.position());
            assertArrayEquals(new byte[] { 1, 2, 3 }, channel.toByteArray());
        }
    }

    @Test
    void writeZeroLengthDoesNotMove() throws IOException {
        ByteArraySeekableChannel channel = new ByteArraySeekableChannel();
        try (SeekableOutputStream stream = new SeekableOutputStream(channel)) {
            stream.write(new byte[] { 1, 2 }, 0, 0);
            assertEquals(0, stream.position());
            assertEquals(0, stream.size());

            stream.write(7);
            assertEquals(1, stream.position());
            assertArrayEquals(new byte[] { 7 }, channel.toByteArray());
        }
    }

    @Test
    void writeRejectsInvalidArguments() throws IOException {
        try (SeekableOutputStream stream = new SeekableOutputStream(new ByteArraySeekableChannel())) {
            assertThrows(NullPointerException.class, () -> stream.write(null, 0, 1));

            byte[] buffer = new byte[2];
            assertThrows(IndexOutOfBoundsException.class, () -> stream.write(buffer, -1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> stream.write(buffer, 0, -1));
            assertThrows(IndexOutOfBoundsException.class, () -> stream.write(buffer, 2, 1));
        }
    }

    @Test
    void positionSetterMovesCursorAndZeroFills() throws IOException {
        ByteArraySeekableChannel channel = new ByteArraySeekableChannel();
        try (SeekableOutputStream stream = new SeekableOutputStream(channel)) {
            stream.position(2);
            stream.write(7);

            assertEquals(3, stream.size());
            assertArrayEquals(new byte[] { 0, 0, 7 }, channel.toByteArray());
        }
    }

    @Test
    void closeClosesUnderlyingChannel() throws IOException {
        ByteArraySeekableChannel channel = new ByteArraySeekableChannel();
        SeekableOutputStream stream = new SeekableOutputStream(channel);
        stream.close();

        assertFalse(channel.isOpen());
        assertThrows(IOException.class, () -> stream.write(1));
    }

    @Test
    void openCreatesFileIfMissing(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("data.bin");
        assertFalse(Files.exists(path));

        try (SeekableOutputStream stream = SeekableOutputStream.open(path)) {
            assertTrue(Files.exists(path));
            stream.write(new byte[] { 4, 5 }, 0, 2);
        }

        assertArrayEquals(new byte[] { 4, 5 }, Files.readAllBytes(path));
    }

    @Test
    void openAppendsWhenPositionedAtEnd(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("data.bin");
        Files.write(path, new byte[] { 1, 2 });

        try (SeekableOutputStream stream = SeekableOutputStream.open(path)) {
            stream.position(stream.size());
            stream.write(3);
        }

        assertArrayEquals(new byte[] { 1, 2, 3 }, Files.readAllBytes(path));
    }
}
