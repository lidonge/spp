package spp.java.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IFileManager {
	public File getBaseDir();
	public InputStream openForRead(String file) throws IOException;
	public OutputStream openForAppend(String file) throws IOException;
	public IRandomAccessable openForUpdate(String file) throws IOException;
	public long length(String file) throws IOException;
}
