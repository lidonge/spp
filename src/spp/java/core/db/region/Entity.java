package spp.java.core.db.region;

import java.util.ArrayList;

import spp.java.core.IEntity;
import spp.java.core.IParticipantMeta;
import spp.java.core.db.file.IPropertyMeta;
import spp.java.core.util.EncodingTool;

public class Entity implements IEntity {
	private IParticipantMeta meta;
	private ArrayList<String> strProps;
	private byte[] nonStrProps;
	private byte[] buffer = new byte[8];
	private byte[] nullStatus;
	private int regionID;
	private int nativeID;
	
	protected Entity(int regionID, int nativeID, IParticipantMeta meta) {
		this.regionID = regionID;
		this.nativeID = nativeID;
		this.meta = meta;
		
		strProps = new ArrayList<>(meta.getNQProperties());
		for(int i = 0;i<meta.getNQProperties();i++)
			strProps.add(null);
		nonStrProps = new byte[meta.sizeofNonString()];
		nullStatus = new byte[(meta.size() - 1)/ 8 + 1];
	}
	@Override
	public IParticipantMeta getMetadata() {
		return meta;
	}

	@Override
	public String getStringProperty(int index) {
		return strProps.get(index);
	}

	@Override
	public byte[] getNonStringProperties() {
		return nonStrProps;
	}

	@Override
	public int getRegion() {
		return this.regionID;
	}

	@Override
	public int getNativeID() {
		return this.nativeID;
	}
	
	@Override
	public byte[] getNullStatus() {
		return this.nullStatus;
	}
	
	protected void setString(int index, String value) {
		strProps.set(index, value);
		setNotNull(index);
	}
	
	protected void setNoString(int index, String value) {
		IPropertyMeta propMeta = this.meta.getProperty(index);
		int size = EncodingTool.toByteArray(propMeta.getType(), value, buffer);
		int beg = propMeta.getPostionIfNonString();
		System.arraycopy(buffer, 0, nonStrProps, beg, size);
		setNotNull(index);
	}
	protected void setNotNull(int index) {
		int pos = index /8;
		int idx = index % 8;
		byte b = (byte) (0x01 << idx);
		nullStatus[pos] |= b;
	}
}
