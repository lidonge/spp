package spp.java.core.db.region;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;

import spp.java.core.IParticipantMeta;
import spp.java.core.IRegionWriter;
import spp.java.core.unused.IRegionFile;
import spp.java.core.unused.RegionMetadataManager;
import spp.java.core.util.FixSizeByteArrayOutputStream;

/**
 * File structure:
 * 1 Metadata file: Region123_metadata.def, variable length file
 * 					entity counts:len(4)
 * 					first entity{ meta id, class name:len(2)byte[], string properties counts: len(2), non_string properties counts: len(2),
 * 						string properties{
 * 							property1 {name:len(2)}
 * 							property2....
 * 						}
 * 						non_string properties{
 * 						}
 * 							property1 {type:len(1),name:len(2)}
 * 							property2....
 * 					}
 * 					second entity...
 * 2 full block file: Region123_full.idx, fix length file. each cell is a block index with len(4). save full data block's index.
 * 3 block index file: Region123_blk123.idx, fix length file. Each cell is 24bytes:
 * 						entity meta id:len(4),string begin position:len(8), string length(4),non_string position:len(8)
 * 	So entity counts is file.length() / (4+8+4+8)
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
 * 						
 * @author lidong
 *
 */
public class RegionFile implements IRegionFile {
	private static final String REGION_FILE = "Region";
	private static final String FULL_BLOCK_FILE = "full";
	private static final String BLOCK_FILE = "blk";
	private static final String INDEX_FILE = ".idx";
	private static final String DATA_FILE = ".dat";
	private static final String STR_DATA_FILE = "str.dat";
	private static final String META_FILE = "metadata.def";
	
	
	private static final int INDEX_SIZE = 24;
	private File path;
	private int regionID;
	private RegionMetadataManager metaFile;
	private FileOutputStream metaStream;
	private File fullBlockFile;
	private BlockFile[] blockFiles = new BlockFile[IRegionWriter.BLOCK_NUMBER];
	

	public RegionFile(int regionID, String dataPath) throws IOException{
		this.path = new File(dataPath);
		this.regionID = regionID;
		if(!path.exists())
			path.mkdirs();
		fullBlockFile = new File(path, REGION_FILE + this.regionID+ "_" + FULL_BLOCK_FILE + INDEX_FILE );
		if(!fullBlockFile.exists())
			fullBlockFile.createNewFile();
		initMetaFile();
	}
	

	public void openBlock(int block) throws IOException {
		if(this.blockFiles[block] == null){
			this.blockFiles[block] = new BlockFile(path,regionID,block);
		}
	}
	public void closeBlock(int block) throws IOException {
		if(this.blockFiles[block] == null){
			this.blockFiles[block].close();
			this.blockFiles[block] = null;
		}
	}

	@Override
	public int[] getFullBlocks() throws IOException {
		FileInputStream fin = null;
		int[] ret = new int[(int) (fullBlockFile.length() / 4)];
		try{
			fin =new FileInputStream(fullBlockFile);
			DataInputStream in = new DataInputStream(fin);
			int idx = 0;
			while(in.available() != 0) {
				ret[idx++] = in.readInt();
			}
		}finally {
			if( fin != null)
				fin.close();
		}
		return ret;
	}


	@Override
	public void close() {
		try {
			metaStream.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		for(int i = 0;i<blockFiles.length;i++ ) {
			if(blockFiles[i] == null)
				continue;
			blockFiles[i].close();
		}
	}
	@Override
	public void addNewMetadata(IParticipantMeta meta) throws IOException {
		this.metaFile.addNewMetadata(this.metaStream, meta);
	}

}
