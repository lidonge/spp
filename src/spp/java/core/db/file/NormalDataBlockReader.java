package spp.java.core.db.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class NormalDataBlockReader<T> {
	private static final int INDEX_SIZE = 8;
	private RandomAccessFile idxFile;
	private RandomAccessFile blkFile;
	private IDataReader<T> reader;
	private IBlockFileHeader header;
	public NormalDataBlockReader(String idx, String blk, IDataReader<T> reader) throws IOException {
		this.idxFile = new RandomAccessFile(idx, "r");
		this.blkFile = new RandomAccessFile(blk, "r");
		this.reader = reader;
		
		int version = this.blkFile.readUnsignedShort();//2
		int modifiers = this.blkFile.readUnsignedShort();//2
		int id = this.blkFile.readInt();//4
		int blockID = this.blkFile.readUnsignedShort();//2
		int blockCount = this.blkFile.readUnsignedShort();//2
		
		header = new IBlockFileHeader() {
			@Override
			public boolean isRegion() {
				return (modifiers & IFileHeader.MOFIDIER_REGION) != 0;
			}

			@Override
			public boolean isQuantumRegion() {
				return isRegion() && ((modifiers & IFileHeader.MOFIDIER_REGION) != 0);
			}

			@Override
			public int getModifiers() {
				return modifiers;
			}

			@Override
			public int getRegionOrDomainID() {
				return id;
			}

			@Override
			public int getVersion() {
				return version;
			}

			@Override
			public int getBlockID() {
				return blockID;
			}

			@Override
			public String getIndexFileName() {
				return idx;
			}

			@Override
			public String getBlockFileName() {
				return blk;
			}

			@Override
			public int getBlockCount() {
				return blockCount;
			}

			@Override
			public int headerSize() {
				return 12;
			}
			
		};
	}


	public T readData(int idInBlock) throws IOException{
		this.idxFile.seek(idInBlock * INDEX_SIZE);
		long blkPos = this.idxFile.readLong();
		this.blkFile.seek(blkPos);
		
		int len = this.blkFile.readUnsignedShort();
		int metaID = this.blkFile.readUnsignedShort();
		
		return this.reader.read(this.blkFile,metaID, len);
	}
	public void close() throws IOException {
		if(this.idxFile != null)
			this.idxFile.close();
		if(this.blkFile != null)
			this.blkFile.close();
	}
}
