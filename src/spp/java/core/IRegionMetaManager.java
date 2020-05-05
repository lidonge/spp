package spp.java.core;

import java.io.IOException;
import java.io.OutputStream;

public interface IRegionMetaManager {
	public int getMetadatas();
	
	IParticipantMeta getMetadata(int index);
	
	IParticipantMeta nameToMetadata(String name);
	
	void addNewMetadata(OutputStream out, IParticipantMeta meta) throws IOException;
}
