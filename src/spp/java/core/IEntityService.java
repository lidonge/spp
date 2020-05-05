package spp.java.core;

public interface IEntityService {
	/**
	 * Modify the property of the entity. This action is quantum action.
	 * @param propertyName the name of the property
	 * @param value the string value, implementor should check and convert to the actual type.
	 */
	public void update(String propertyName, String value);

	/**
	 * A quantum call to modify a sets of properties. Should update property one by one, and 
	 * finally commit all.
	 * @param propertyName the name of the property
	 * @param value the string value, implementor should check and convert to the actual type.
	 * @see commitBatch
	 */
	public void updateBatch(String propertyName, String value);
	
	/**
	 * commit the modifies.
	 */
	public void commitBatch();
	
	/**
	 *  A quantum call to increase a number property.
	 * @param propertyName
	 * @param value
	 */
	public void increase(String propertyName, int value);
	/**
	 *  A quantum call to increase a number property.
	 * @param propertyName
	 * @param value
	 */
	public void increase(String propertyName, float value);
	/**
	 *  A quantum call to increase a number property.
	 * @param propertyName
	 * @param value
	 */
	public void increase(String propertyName, double value);

	/**
	 *  A quantum call to decrease a number property.
	 * @param propertyName
	 * @param value
	 */
	public void decrease(String propertyName, int value);
	/**
	 *  A quantum call to decrease a number property.
	 * @param propertyName
	 * @param value
	 */
	public void decrease(String propertyName, float value);
	/**
	 *  A quantum call to decrease a number property.
	 * @param propertyName
	 * @param value
	 */
	public void decrease(String propertyName, double value);
}
