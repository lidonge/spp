package spp.java.core;

import java.io.IOException;

public interface IEntityCreateService {
	public int getBlockID();
	public int getCursor() throws IOException;
	public void appendEntity(int indexInBlock, int metaID, byte[] nullStatus, byte[] strings, byte[] non_strings)
			throws IOException;
}
