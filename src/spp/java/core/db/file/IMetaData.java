package spp.java.core.db.file;

public interface IMetaData {
	public String getName();
	public int getMetaID();
	public void setMetaID(int id);
	public int size();
	public IPropertyMeta getProperty(int index);
	public int mapNameToIndex(String name);
}
