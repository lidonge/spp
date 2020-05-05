package spp.java.core.db.file;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class MetaFileWriter {

	private void writeVarChar(DataOutput out, String s) throws IOException{
		if(s.length() > 256) {
			throw new IOException("The name length too large:  "+ s);
		}
		out.writeByte(s.length());
		out.write(s.getBytes());;
	}

	public boolean addNewMetadata(IMetaData meta, String file, short version) throws IOException {
		boolean ret = false;
		File f = new File(file);
		boolean newFile = false;
		if(!f.exists())
			newFile = f.createNewFile();
		RandomAccessFile fout = null;
		FileLock fl = null;  //or fc.lock();
		try {
			fout = new RandomAccessFile(file,"rw");
			FileChannel fc = fout.getChannel();
			while((fl = fc.tryLock()) == null) {
				try {
					Thread.currentThread().sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			if(fl != null) {
				ret = true;
				if(newFile) {
					fout.writeShort(version);
					fout.writeShort(0);
				}
				fout.seek(2);
				int maxMetaID = fout.readUnsignedShort()+1;
				fout.seek(2);
				fout.writeShort(maxMetaID);
				meta.setMetaID(maxMetaID);
				fout.seek(fout.length());
				addNewMetadata(fout, meta);
			}
		}finally {
			if(fl != null)
				fl.release();
			if(fout != null)
				fout.close();
		}
		return ret;
	}
	public void addNewMetadata(DataOutput dout, IMetaData meta) throws IOException {	
		String clsname = meta.getName();
		int propsCount = meta.size();

		dout.writeShort(meta.getMetaID());
		writeVarChar(dout, clsname);
		dout.writeShort(propsCount);

		for(int i = 0;i<propsCount;i++) {
			IPropertyMeta pmeta = meta.getProperty(i);
			writeVarChar(dout, pmeta.getName());
			dout.writeByte(pmeta.getType());
			dout.writeByte(pmeta.getModifiers());
		}
	}
}
