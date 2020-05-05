package spp.java.core.unused;

import java.io.IOException;
import java.io.OutputStream;

import spp.java.core.IParticipantMeta;

public interface IRegionFile {
	public void appendEntity(int block, int indexInBlock, int metaID, byte[] strings, byte[] non_strings)
			throws IOException;
	public int[] getFullBlocks() throws IOException;
	public int getMinNoneDataBlock();
	public void addNewMetadata( IParticipantMeta meta) throws IOException;
	public void close();
}
