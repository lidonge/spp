package spp.java.core.db.file;

public interface IIndex {
	public int getIndexID();
	public int getMetadataID();
	public long getPosition();
	public int getLength();
	public void setPosition(long pos);
	public void setLength(int length);
}
