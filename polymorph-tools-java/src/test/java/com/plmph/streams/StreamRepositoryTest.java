package com.plmph.streams;

import com.plmph.pde.PdeFieldTypes;
import com.plmph.pde.PdeReader;
import org.junit.jupiter.api.Test;

import java.io.File;

import static com.plmph.util.FileUtil.*;
import static org.junit.jupiter.api.Assertions.*;

public class StreamRepositoryTest {

    @Test
    void testCreateStream() throws Exception {
        var streamRepoPath = concatPaths("test-data", "test-stream-repo");
        Stream stream = null;
        try {
            StreamRepository streamRepository = new StreamRepository(streamRepoPath).createStreamRepositoryRootDirectory();
            stream = streamRepository.createStream(createStreamId(0));

            assertTrue(fileExists(stream.getStreamRootDirectory()));
            assertTrue(fileExists(stream.getMetadataFilePath()));

            PdeReader pdeReader = new PdeReader(stream.readMetadataFile()).next();
            byte[] streamIdBytes = pdeReader.readBytes();

            assertArrayEquals(stream.getId().getIdBytes(), streamIdBytes);
        } finally {
            if(stream != null){
                stream.delete();
                deleteFile(streamRepoPath);
            }
        }
    }

    @Test
    void testInitFromRepositoryRoot() {
        var streamRepoPath = concatPaths("test-data", "test-stream-repo");
        StreamId id0 = createStreamId(0);
        StreamId id1 = createStreamId(1);
        StreamId id2 = createStreamId(2);
        Stream stream0 = null;
        Stream stream1 = null;
        Stream stream2 = null;
        try {
            StreamRepository streamRepository = new StreamRepository(streamRepoPath).createStreamRepositoryRootDirectory();
            stream0 = streamRepository.createStream(id0);
            stream1 = streamRepository.createStream(id1);
            stream2 = streamRepository.createStream(id2);

            StreamRepository reloaded = new StreamRepository(streamRepoPath).initFromRepositoryRootDirectory();

            assertEquals(3, reloaded.streams.size());
            assertTrue(reloaded.streams.containsKey(id0));
            assertTrue(reloaded.streams.containsKey(id1));
            assertTrue(reloaded.streams.containsKey(id2));

            assertArrayEquals(id0.getIdBytes(), reloaded.streams.get(id0).getId().getIdBytes());
            assertArrayEquals(id1.getIdBytes(), reloaded.streams.get(id1).getId().getIdBytes());
            assertArrayEquals(id2.getIdBytes(), reloaded.streams.get(id2).getId().getIdBytes());

            assertTrue(fileExists(reloaded.streams.get(id0).getMetadataFilePath()));
            assertTrue(fileExists(reloaded.streams.get(id1).getMetadataFilePath()));
            assertTrue(fileExists(reloaded.streams.get(id2).getMetadataFilePath()));
        } finally {
            if (stream0 != null) {
                stream0.delete();
            }
            if (stream1 != null) {
                stream1.delete();
            }
            if (stream2 != null) {
                stream2.delete();
            }
            deleteFile(streamRepoPath);
        }
    }

    @Test
    public void testAppendWrapped() {
        var streamRepoPath = concatPaths("test-data", "test-stream-repo");
        StreamId id0 = createStreamId(0);
        Stream stream = null;
        try {
            StreamRepository streamRepository = new StreamRepository(streamRepoPath).createStreamRepositoryRootDirectory();
            stream = streamRepository.createStream(id0);

            byte[] shortRecord = new byte[] { 0x01, 0x02, 0x03 }; // length 3 → short form
            byte[] longRecord = new byte[16];                     // length 16 → length-prefixed form
            for (int i = 0; i < longRecord.length; i++) {
                longRecord[i] = (byte) i;
            }

            stream.appendWrapped(shortRecord);
            stream.appendWrapped(null);
            stream.appendWrapped(longRecord);

            String tailPath = stream.getStreamRootDirectory() + File.separator + "data.tail.pde";
            assertTrue(fileExists(tailPath));

            //todo read stream tail file and assert on total expected length
            byte[] tailFile = readFile(tailPath);
            assertEquals(23,  tailFile.length);

            PdeReader reader = new PdeReader(tailFile);

            reader.next();
            assertEquals(PdeFieldTypes.BYTES_0_BYTES + shortRecord.length, reader.fieldType);
            assertArrayEquals(shortRecord, reader.readBytes());

            reader.next();
            assertEquals(PdeFieldTypes.BYTES_NULL, reader.fieldType);
            assertNull(reader.readBytes());

            reader.next();
            assertEquals(PdeFieldTypes.BYTES_1_LENGTH_BYTES, reader.fieldType);
            assertEquals(1, reader.fieldLengthLength);
            assertEquals(16, reader.fieldValueLength);
            assertArrayEquals(longRecord, reader.readBytes());
        } finally {
            if (stream != null) {
                stream.delete();
                deleteFile(streamRepoPath);
            }
        }
    }

    @Test
    public void testReadFromStream() {
        var streamRepoPath = concatPaths("test-data", "test-stream-repo");
        StreamId id0 = createStreamId(0);
        Stream stream = null;
        try {
            StreamRepository streamRepository = new StreamRepository(streamRepoPath).createStreamRepositoryRootDirectory();
            stream = streamRepository.createStream(id0);

            byte[] record0 = new byte[] { 0x01, 0x02 };
            byte[] record1 = new byte[] { 0x03, 0x04, 0x05 };
            byte[] record2 = new byte[] { 0x0A, 0x0B, 0x0C, 0x0D };

            stream.appendWrapped(record0);
            stream.appendWrapped(record1);
            stream.appendWrapped(record2);

            // dest may be larger than the field; bytesRead is the valid length
            byte[] dest = new byte[64];
            int bytesRead = stream.readFromStream(dest);

            assertEquals(1 + record2.length, bytesRead);

            // Unwrap PDE BYTES field and compare payload to last appended record
            PdeReader reader = new PdeReader().setSource(dest, 0, bytesRead).next();
            assertEquals(PdeFieldTypes.BYTES_0_BYTES + record2.length, reader.fieldType);
            assertArrayEquals(record2, reader.readBytes());
        } finally {
            if (stream != null) {
                stream.delete();
                deleteFile(streamRepoPath);
            }
        }
    }

    private static StreamId createStreamId(int firstByte) {
        byte[] id = new byte[32];
        for (int i = 0; i < 32; i++) {
            id[i] = (byte) i;
        }
        id[0] = (byte) (0xFF & firstByte);
        return new StreamId(id);
    }
}