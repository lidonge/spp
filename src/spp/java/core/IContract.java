package spp.java.core;

public interface IContract {
	public int getDomain();
	public int getContractID();
	
	public IBehavior[] getBehaviors();
	public IParticipant[] getParticipants();
}
