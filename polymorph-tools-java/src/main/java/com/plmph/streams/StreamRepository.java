package com.plmph.streams;

import com.plmph.hex.HexUtil;
import com.plmph.pde.PdeReader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.plmph.util.FileUtil.*;

public class StreamRepository {

    protected String repositoryRootDirectory = null;
    protected Map<StreamId, Stream> streams = new HashMap<>();

    public StreamRepository(String repositoryRootDirectory){
        this.repositoryRootDirectory = repositoryRootDirectory;
    }

    public String getRepositoryRootDirectory() {
        return repositoryRootDirectory;
    }

    public StreamRepository initFromRepositoryRootDirectory(){
        forEachFileInDir(repositoryRootDirectory, this::initStreamFromDirectory);
        return this;
    }

    protected void initStreamFromDirectory(File streamDir) {
        // Path/layout lives on Stream; id is filled in after reading metadata.
        Stream stream = new Stream(streamDir.getAbsolutePath(), new StreamId(new byte[0]));
        PdeReader pdeReader = new PdeReader(stream.readMetadataFile()).next();
        StreamId streamIdObj = new StreamId(pdeReader.readBytes());

        streams.put(streamIdObj, new Stream(streamDir.getAbsolutePath(), streamIdObj));
    }

    public StreamRepository createStreamRepositoryRootDirectory(){
        if(fileExists(repositoryRootDirectory)){
            return this; // already exists - no reason to recreate it.
        }
        if(! makeDirs(repositoryRootDirectory)){
            throw new RuntimeException("Repository root directory was not created: " + repositoryRootDirectory);
        }
        return this;
    }

    public Stream createStream(StreamId streamId){
        String hexStreamName = HexUtil.bytesToHex(streamId.getIdBytes(), 0, 8, new StringBuilder()).toString();
        String streamRootDirectory = repositoryRootDirectory + File.separator + hexStreamName;
        assertFileNotExists(streamRootDirectory, "Stream already exists: ");
        if(!makeDirs(streamRootDirectory)){
            throw new RuntimeException("Failed to create stream directory");
        }
        Stream stream = new Stream(streamRootDirectory, streamId);
        stream.writeStreamMetadataFile();
        streams.put(streamId, stream);
        return stream;
    }


}
