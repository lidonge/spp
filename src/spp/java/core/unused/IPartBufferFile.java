package spp.java.core.unused;

import java.io.DataOutput;

public interface IPartBufferFile extends DataOutput{
	public long getCurrentFilePostion();
	public int getCurrentBufferPosition();
	public int getLength();
}
