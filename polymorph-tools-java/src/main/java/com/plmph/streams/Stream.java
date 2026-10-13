package com.plmph.streams;

import com.plmph.pde.PdeFieldTypes;
import com.plmph.pde.PdeReaderByteBuffer;
import com.plmph.pde.PdeUtil;
import com.plmph.pde.PdeWriter;
import com.plmph.util.PolymorphException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static com.plmph.util.FileUtil.readFile;
import static com.plmph.util.FileUtil.writeFile;

public class Stream {

    public StreamId id;
    public String streamRootDirectory;
    public int segmentSize = 1024 * 1024 * 1024; // 1 GB segment size by default.
    public StreamSegment[] streamSegments;

    public Stream(String streamRootDirectory, StreamId streamId) {
        this.streamRootDirectory = streamRootDirectory;
        this.id = streamId;
    }

    public StreamId getId() {
        return id;
    }

    public String getStreamRootDirectory() {
        return streamRootDirectory;
    }

    public int getSegmentSize() {
        return segmentSize;
    }

    public String getMetadataFilePath() {
        return streamRootDirectory + File.separator + "metadata.pde";
    }

    public String getStreamTailFilePath() {
        return streamRootDirectory + File.separator + "data.tail.pde";
    }

    public byte[] readMetadataFile() {
        return readFile(getMetadataFilePath());
    }

    public void writeStreamMetadataFile() {
        byte[] metadataPde = new byte[64];
        PdeWriter pdeWriter = new PdeWriter(metadataPde);
        pdeWriter.writeBytes(id.getIdBytes());

        writeFile(getMetadataFilePath(), metadataPde, 0, pdeWriter.offset);
    }

    public boolean delete() {
        boolean allDeleted = true;
        File streamRoot = new File(streamRootDirectory);
        File[] files = streamRoot.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.delete()) {
                    allDeleted = false;
                }
            }
        }
        if (!streamRoot.delete()) {
            allDeleted = false;
        }
        return allDeleted;
    }

    public void appendWrapped(byte[] record) {
        String streamTailFilePath = getStreamTailFilePath();

        if (record == null) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(streamTailFilePath, true)) {
                fileOutputStream.write(new byte[]{ (byte) PdeFieldTypes.BYTES_NULL });
            } catch (IOException e) {
                throw new RuntimeException("Error appending to stream tail file", e);
            }
            return;
        }

        byte[] bytesFieldHeader;
        int bytesLength = record.length;
        if (bytesLength < 16) {
            bytesFieldHeader = new byte[1];
            bytesFieldHeader[0] = (byte) (0xFF & (PdeFieldTypes.BYTES_0_BYTES + bytesLength));
        } else {
            int lengthLength = PdeUtil.byteLengthOfInt64Value(bytesLength);
            bytesFieldHeader = new byte[1 + lengthLength];
            int offset = 0;
            bytesFieldHeader[offset++] = (byte) (0xFF & (PdeFieldTypes.BYTES_15_BYTES + lengthLength));
            for (int i = 0, n = lengthLength * 8; i < n; i += 8) {
                bytesFieldHeader[offset++] = (byte) (0xFF & (bytesLength >> i));
            }
        }

        //todo FileUtil.appendToFile()
        try (FileOutputStream fileOutputStream = new FileOutputStream(streamTailFilePath, true)) {
            fileOutputStream.write(bytesFieldHeader);
            fileOutputStream.write(record);
        } catch (IOException e) {
            throw new RuntimeException("Error appending to stream tail file", e);
        }
    }

    /**
     * Reads the last root field from the stream tail into {@code dest}.
     *
     * @return number of bytes written into {@code dest} (encoded PDE field length)
     */
    public int readFromStream(byte[] dest) {
        String streamTailFilePath = getStreamTailFilePath();

        // Map via FFM Arena so closing the arena unmaps and releases the file lock (public API).
        try (RandomAccessFile file = new RandomAccessFile(streamTailFilePath, "r");
             FileChannel channel = file.getChannel();
             Arena arena = Arena.ofConfined()) {

            MemorySegment segment = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length(), arena);
            ByteBuffer buffer = segment.asByteBuffer();

            PdeReaderByteBuffer pdeReader = new PdeReaderByteBuffer(buffer);

            // Iterate field by field; keep the byte offset of the last parsed root field.
            int lastRecordOffset = 0;
            while (pdeReader.hasNext()) {
                pdeReader.next();
                lastRecordOffset = pdeReader.offset;
            }

            // Move to last record and parse it so field size is known.
            buffer.position(lastRecordOffset);
            pdeReader.setSource(buffer).next();

            // Copy only this field's encoded bytes (type + length bytes + value).
            int fieldByteLength = PdeReaderByteBuffer.FIELD_TYPE_BYTE_COUNT
                    + pdeReader.fieldLengthLength
                    + pdeReader.fieldValueLength;
            buffer.get(dest, 0, fieldByteLength);
            return fieldByteLength;

        } catch (IOException e) {
            throw new PolymorphException("Error reading from stream tail file: " + e.getMessage(), e);
        }
    }

    public long latestOffsetForStream(byte[] streamId){
        return 0;
    }

    public void readFromStream(byte[] streamId, long fromOffset){

    }

    public void readFromStream(byte[] streamId, long fromOffset, long toOffset){

    }


    public static class StreamSegment {
        public String filePath;
        public long firstOffset;
    }

    public static class StreamSegmentIndex {
        public int[] relativeRecordOffsets;
        public int[] segmentByteOffsets;
    }

}
