package spp.java.core.db.file;

public interface IPropertyMeta {
	public static final int TYPE_STRING = 1;
	public static final int TYPE_INT = 2;
	public static final int TYPE_SHORT = 3;
	public static final int TYPE_BYTE = 4;
	public static final int TYPE_LONG = 5;
	public static final int TYPE_DOUBLE = 6;
	public static final int TYPE_FLOAT = 7;
	public static final int TYPE_TIMESTAMP = 8;
	
	public static final byte MODIFIER_QUANTUM = 0x01;
	public static final byte MODIFIER_NULLABLE = 0x02;
	public static final byte MODIFIER_NON_NAGATIVE = 0x04;
	
	
	public int getPropertyIndex();
	public String getName();
	public int getType();
	public byte getModifiers();
	public boolean isNullable();
	public boolean isQuantum();
	public boolean isNon_Nagative();
}
