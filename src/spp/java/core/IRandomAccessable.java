package spp.java.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface IRandomAccessable extends DataOutput, DataInput{
    public void seek(long pos) throws IOException;
    public long length() throws IOException;
    public void close() throws IOException;
 }
