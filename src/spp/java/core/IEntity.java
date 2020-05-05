package spp.java.core;

public interface IEntity extends IParticipant{
	/**
	 * The region that entity belongs to.
	 * @return the region id
	 */
	public int getRegion();
	
	/**
	 * The native id of the entity encoded when it born.
	 * @return the native id
	 */
	public int getNativeID();
}
