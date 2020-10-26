package spp.java.core.db.file;

import java.nio.ByteBuffer;

public interface IDataWriter {
	public int size();
	public void write(ByteBuffer buff);
	public int getID();
	public int getMetadataID();
}
