package spp.java.core.db.region;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import spp.java.core.IEntityCreateService;
import spp.java.core.IFileManager;
import spp.java.core.IRandomAccessable;
import spp.java.core.IRegionWriter;
import spp.java.core.util.FixSizeByteArrayOutputStream;

/**
 * 2 full block file: Region123_full.idx, fix length file. each cell is a block index with len(4). save full data block's index.
 * 3 block index file: Region123_blk123.idx, fix length file. Each cell is 24bytes:
 * 						entity meta id:len(4),null status begin position len:len(8), string begin position:len(8), string length(4),non_string position:len(8)
 * 	So entity counts is file.length() / (4+8+8+4+8)
 * 4 block string data file:Region123_blk123_str.dat, variable length file
 * 						entity1{property1{ string len:len(2), byte[string len]},
 * 								property2..
 * 								}
 * 						entity2....	
 * 5 block non_string data file:Region123_blk123.dat, variable length file
 * 						entity1{property1{ byte[property len]},
 * 								property2..
 * 								}
 * 						entity2....
 * 6 block null status file:Region123_blk123_null.dat, variable length file
 * 						entity1: byte[]
 * 						entity2...
 * @author Administrator
 *
 */
public class EntityCreateService implements IEntityCreateService {
	private static final int INDEX_SIZE = 32;

	private IFileManager fileManager;
	private int blockID;

	private IRandomAccessable idxOut;
	private OutputStream strOut, nonStrOut, nullOut;
	private long strLen, nonStrLen, nullLen;
	private FixSizeByteArrayOutputStream idxBuff = new FixSizeByteArrayOutputStream(INDEX_SIZE);
	private DataOutputStream idxBuffOut = new DataOutputStream(idxBuff);

	EntityCreateService(IFileManager f,int regionID, int blockID) throws IOException{
		this.fileManager = f;
		String BLK_PREFIX = IRegionWriter.getBlockProfix(regionID, blockID);
		idxOut = fileManager.openForUpdate(BLK_PREFIX + IRegionWriter.INDEX_FILE);
		
		strOut = fileManager.openForAppend(BLK_PREFIX +"_"+ IRegionWriter.STR_DATA_FILE);
		strLen = fileManager.length(BLK_PREFIX +"_"+ IRegionWriter.STR_DATA_FILE);
		
		nonStrOut = fileManager.openForAppend(BLK_PREFIX + IRegionWriter.DATA_FILE);
		nonStrLen = fileManager.length(BLK_PREFIX + IRegionWriter.DATA_FILE);
		
		nullOut = fileManager.openForAppend(BLK_PREFIX + "_"+ IRegionWriter.NULL_DATA_FILE);
		nullLen = fileManager.length(BLK_PREFIX + "_"+ IRegionWriter.NULL_DATA_FILE);
	}
	
	void close(){
		if(idxOut != null)
			try {
				idxOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		if(strOut != null)
			try {
				strOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		if(nonStrOut != null)
			try {
				nonStrOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	@Override
	public int getBlockID() {
		return blockID;
	}

	@Override
	public int getCursor() throws IOException {
		return (int) (idxOut.length() / INDEX_SIZE);
	}

	@Override
	public void appendEntity(int indexInBlock, int metaID, byte[] nullStatus, byte[] strings, byte[] non_strings)
			throws IOException {
//		if(indexInBlock != getCursor())
//			throw new IOException("Entity appends at error cursor, current is " + getCursor() + ", append to  "+ indexInBlock);
		long begNull = nullLen;
		long begStr = strLen;
		long begNonStr = nonStrLen;
		
		//write to index file
		idxOut.seek(indexInBlock * INDEX_SIZE);
		idxBuff.reset();
		//block index file: Region123_blk123.idx, fix length file. Each cell is 24bytes:
			//entity meta id:len(4),string begin position:len(8), string length(4),non_string position:len(8)
			//So entity counts is file.length() / (4+8+4+8)

		idxBuffOut.writeInt(metaID);
		idxBuffOut.writeLong(begNull);
		idxBuffOut.writeLong(begStr);
		idxBuffOut.writeInt(strings.length);
		idxBuffOut.writeLong(begNonStr);
		idxOut.write(idxBuff.toByteArray());
		
		//write to string file
		strOut.write(strings);
		strLen += strings.length;
		
		//write to non_string file
		nonStrOut.write(non_strings);
		nonStrLen += non_strings.length;
		
		//write to non_string file
		nullOut.write(nullStatus);
		nullLen += nullStatus.length;		
	}

}
