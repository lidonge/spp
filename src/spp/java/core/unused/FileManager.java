package spp.java.core.unused;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import spp.java.core.IFileManager;
import spp.java.core.IRandomAccessable;

public class FileManager implements IFileManager {
	private File path;
	
	private static class Raf extends RandomAccessFile implements IRandomAccessable{
		public Raf(File path, String rw) throws FileNotFoundException {
			super(path,rw);
		}
	}
	public FileManager(String dataPath) {
		this.path = new File(dataPath);
		if(!path.exists())
			path.mkdirs();
	}
	@Override
	public File getBaseDir() {
		return path;
	}

	@Override
	public InputStream openForRead(String file) throws IOException {
		File f = new File(path, file);
		if(!f.exists())
			f.createNewFile();
		return new FileInputStream(f);
	}

	@Override
	public OutputStream openForAppend(String file) throws IOException {
		File f = new File(path, file);
		if(!f.exists())
			f.createNewFile();
		return new FileOutputStream(f,true);
	}

	@Override
	public IRandomAccessable openForUpdate(String file) throws IOException {
		File f = new File(path, file);
		if(!f.exists())
			f.createNewFile();
		return new Raf(f,"rw");
	}
	@Override
	public long length(String file) throws IOException {
		File f = new File(path, file);
		return f.length();
	}

}
