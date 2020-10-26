package spp.java.core.db.file;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface IRecordWriter<T> {
	public int write(ByteBuffer buff, T record) throws IOException;
	public long writeIndex(DataOutput out, long curPos) throws IOException;
}
