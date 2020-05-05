package spp.java.core;

public interface IDomain {
	public int getDomainID();
	public int registerMember(IMemberContract contract);
	public IMember getMember(int contractID);
	public int signContract(IExecutableContract contract);
	public IExecutableContract getContract();
}
