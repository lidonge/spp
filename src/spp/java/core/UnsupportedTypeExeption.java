package spp.java.core;

public class UnsupportedTypeExeption extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6135091889824332288L;

	public UnsupportedTypeExeption(int type) {
		super("The type "+ type +" does not supported!");
	}
}
