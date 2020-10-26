package spp.java.core.db.file;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class MetaFileWriter {
	private IDatabase database;
	public MetaFileWriter(IDatabase database) {
		this.database = database;
	}
	/**
	 * Add a metadata to the specified metadata file, and return the metadata id;
	 * @param meta    the given metadata
	 * @param metaFile	  the specified metadata file
	 * @param version the metadata file version
	 * @return -1 if failed, or return the new metadata id
	 * @throws IOException
	 */
	public int addNewMetadata(IMetaData meta) throws IOException {
		int ret = -1;
		File f = this.database.getDefaultMetadataFile();
		if(!f.exists())
			throw new IOException("Can not find database files :" + f);
		RandomAccessFile fout = null;
		FileLock fl = null;  //or fc.lock();
		try {
			fout = new RandomAccessFile(f,"rw");
			FileChannel fc = fout.getChannel();
			while((fl = fc.tryLock()) == null) {
				try {
					Thread.currentThread().sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			if(fl != null) {
				//read the max metadata id and increace it
				int headerSize = this.database.sizeOfMetaFileHeader(); 
				fout.seek(headerSize);
				int maxMetaID = fout.readUnsignedShort()+1;
				//set the metadata id and write the meta to the end of the file
				meta.setMetaID(maxMetaID);
				fout.seek(fout.length());
				addNewMetadata(fout, meta);
				
				//change the max meta id to the new one in the file
				fout.seek(headerSize);
				fout.writeShort(maxMetaID);

				ret = maxMetaID;
			}
		}finally {
			if(fl != null)
				fl.release();
			if(fout != null)
				fout.close();
		}
		return ret;
	}

	private void addNewMetadata(DataOutput dout, IMetaData meta) throws IOException {	
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

	private void writeVarChar(DataOutput out, String s) throws IOException{
		if(s.length() > 256) {
			throw new IOException("The name length too large:  "+ s);
		}
		out.writeByte(s.length());
		out.write(s.getBytes());;
	}

}
