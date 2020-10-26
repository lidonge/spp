package spp.java.core.db.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class DatabaseCreator {
	public static final int CURRENT_FILE_WRITER_VERSION = 1;
	public boolean createNewDB(IDatabase db, boolean forced) throws IOException {
		File f = db.getDefaultMetadataFile();
		boolean newFile = false;
		if(forced && f.exists())
			f.delete();
		if(!f.exists()) {
			newFile = f.createNewFile();
			if(!newFile) {
				throw new IOException("Can not create new database files :" + f);
			}
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
					if(newFile) {
						fout.writeShort(CURRENT_FILE_WRITER_VERSION);
						fout.writeShort(db.getModifiers());
						fout.writeInt(db.getRegionOrDomainID());
						fout.writeShort(db.getBlockCount());
						fout.writeShort(0);//MaxMetaID
					}
				}
			}finally {
				if(fl != null)
					fl.release();
				if(fout != null)
					fout.close();
			}
		}
		return newFile;
	}
	
	public boolean createNewBlock(IDatabase db, int blockID, boolean forced) throws IOException {
		File fIdx = db.getDefaultBlockIndexFile(blockID);
		File fBlk = db.getDefaultBlockFile(blockID);
		File fQtm = db.getDefaultBlockQuantumFile(blockID);
		boolean newFile = false;
		if(forced && fBlk.exists()) {
			fBlk.delete();
			fIdx.delete();
			if(fQtm.exists())
				fQtm.delete();
		}
		if(!fIdx.exists()) {
			newFile = fIdx.createNewFile();
			newFile &= fBlk.createNewFile();
			if(db.isQuantumRegion())
				newFile &= fQtm.createNewFile();
			if(!newFile) {
				throw new IOException("Can not create new block :" + blockID);
			}
			RandomAccessFile rBlk = null;
			try {
				rBlk = new RandomAccessFile(fBlk, "rw");
				rBlk.writeShort(CURRENT_FILE_WRITER_VERSION);
				rBlk.writeInt(db.getRegionOrDomainID());
				rBlk.writeShort(blockID);
			}finally {
				if(rBlk != null) {
					try {
						rBlk.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return newFile;
	}
}
