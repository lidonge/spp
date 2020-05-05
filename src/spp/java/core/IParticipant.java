package spp.java.core;

public interface IParticipant {
	public IParticipantMeta getMetadata();
	public String getStringProperty(int index);
	public byte[] getNonStringProperties();
	public byte[] getNullStatus();
}
