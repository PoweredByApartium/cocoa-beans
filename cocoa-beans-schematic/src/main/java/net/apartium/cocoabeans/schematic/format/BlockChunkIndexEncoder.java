package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.Mathf;
import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.schematic.utils.ByteArraySeekableChannel;
import net.apartium.cocoabeans.schematic.utils.SeekableInputStream;
import net.apartium.cocoabeans.schematic.utils.SeekableOutputStream;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.structs.Entry;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.*;

import static net.apartium.cocoabeans.schematic.utils.FileUtils.*;
import static net.apartium.cocoabeans.schematic.utils.FileUtils.writeU64;
import static net.apartium.cocoabeans.space.Position.add;
import static net.apartium.cocoabeans.space.Position.multiply;

public class BlockChunkIndexEncoder implements IndexEncoder {

    public static final int ID = 0b1;

    @Override
    public BlockIterator read(SeekableInputStream indexIn, AxisOrder axisOrder, BlockDataEncoder blockDataEncoder, SeekableInputStream blockIn) throws IOException {
        int layers = indexIn.read();
        long scaler = (long) Math.pow(4, layers);

        BlockChunk[] blockChunk = new BlockChunk[]{new BlockChunk(axisOrder, 1, Position.ZERO, Position.ZERO)};

        readIndexLayer(
                indexIn,
                new DataInputStream(indexIn),
                blockChunk,
                indexIn.position(),
                scaler,
                Position.ZERO,
                axisOrder,
                blockDataEncoder,
                blockIn
        );

        return new BlockChunkIterator(blockChunk[0]);
    }

    private void readIndexLayer(SeekableInputStream in, DataInputStream din, BlockChunk[] blockChunk, long fileOffset, long scaler, Position actualPosition, AxisOrder axes, BlockDataEncoder encoder, SeekableInputStream blockIn) throws IOException {
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

                blockChunk[0] = setBlock(blockChunk[0], new BlockPlacement(actualPos, encoder.read(blockIn)));
                in.position(offsetBefore + 8);
                continue;
            }

            long nextFileOffset = readU64(din);
            readIndexLayer(
                    in,
                    din,
                    blockChunk,
                    nextFileOffset,
                    scaler / 4,
                    actualPos,
                    axes,
                    encoder,
                    blockIn
            );

            in.position(offsetBefore + 8L);
        }
    }

    @Override
    public byte[] write(BlockIterator placements, AxisOrder axisOrder, Map<BlockData, Long> blockIndexes) {
        ByteArraySeekableChannel channel = new ByteArraySeekableChannel();
        SeekableOutputStream indexesOut = new SeekableOutputStream(channel);

        BlockChunk blockChunk = convertToBlockChunk(placements, axisOrder);
        try {
            writeIndexes(indexesOut, (long) blockChunk.getScaler(), blockChunk, blockIndexes);
            return channel.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BlockChunk convertToBlockChunk(Iterator<BlockPlacement> placements, AxisOrder axisOrder) {
        BlockChunk blockChunk = new BlockChunk(axisOrder, 1, Position.ZERO, Position.ZERO);
        while (placements.hasNext())
            blockChunk = setBlock(blockChunk, placements.next());

        return blockChunk;
    }

    private BlockChunk setBlock(BlockChunk blockChunk, BlockPlacement placement) {
        Position pos = placement.position();

        int maxAxis = (int) Math.max(pos.getX(), Math.max(pos.getY(), pos.getZ()));
        if (maxAxis >= blockChunk.getScaler())
            blockChunk = new BlockChunk(blockChunk.getAxisOrder(), Mathf.nextPowerOfFour(maxAxis) * 4, Position.ZERO, Position.ZERO, blockChunk);

        blockChunk.setBlock(placement);
        return blockChunk;
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

                if (pointer instanceof BlockPointer)
                    throw new RuntimeException("BlockPointers are not supported while outside of chunk pointers");



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

}
