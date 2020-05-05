package spp.java.core;

public class WrongBlockExeption extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6135091889824332288L;

	public WrongBlockExeption(int clientid, int block) {
		super("The client "+ clientid +" does not bind with block "+ block + "!");
	}
}
