package spp.java.core;

import spp.java.core.db.file.IMetaData;

public interface IParticipantMeta extends IMetaData{

	public int getNQProperties();
	public int getQuantumProperties();
	public int sizeofNonString();
//	public IParticipant createParticipant();
//	
//	public byte[] toByteArray(boolean stringProperties, IParticipant participant);
}
