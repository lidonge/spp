package spp.java.core.db.file;

import java.io.DataInput;
import java.io.IOException;

public interface IDataReader<T> {
	public T read(DataInput in, int metaid, int len) throws IOException;
}
