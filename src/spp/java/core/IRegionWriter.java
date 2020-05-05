package spp.java.core;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public interface IRegionWriter extends IRegion {
	public static final int BLOCK_NUMBER = 1000;
	public static final int BLOCK_SIZE = 2^31 /1000;
	public static final String REGION_FILE = "Region";
	public static final String FULL_BLOCK_FILE = "full";
	public static final String BLOCK_FILE = "blk";
	public static final String INDEX_FILE = ".idx";
	public static final String DATA_FILE = ".dat";
	public static final String STR_DATA_FILE = "str.dat";
	public static final String NULL_DATA_FILE = "null.dat";
	public static final String META_FILE = "metadata.def";

	public static String getBlockProfix(int regionID, int blockID) {
		return  REGION_FILE + regionID+ "_"+BLOCK_FILE + blockID;
	}

	public static int getLocalRegionID(String dataPath) {
		int ret = -1;
		File[] files = new File(dataPath).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith( REGION_FILE ) && name.endsWith(FULL_BLOCK_FILE +INDEX_FILE);
			}
			
		});
		if(files != null && files.length == 1) {
			String s = files[0].getName();
			int idx = s.indexOf(FULL_BLOCK_FILE);
			s = s.substring(REGION_FILE.length(), idx - 1);
			ret = Integer.parseInt(s);
		}
		return ret;
	}
	public static int getMinNoneDataBlock(int regionID, String dataPath) {
		int ret = -1;
		String BLK_PREFIX = REGION_FILE + regionID+ "_"+BLOCK_FILE;
		File path = new File(dataPath);
		File[] files = path.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith( BLK_PREFIX );
			}
			
		});
		for(int i = 0;i<files.length;i++) {
			String num = files[i].getName().substring(0,BLK_PREFIX.length() );
			num = num.substring(0,num.lastIndexOf('.'));
			int n = Integer.parseInt(num);
			ret = ret > n ? ret : n;
		}
		return ret+1;
	}
	
	public void createEntity(int clientID,IEntity entity) throws IOException, WrongBlockExeption;
	public IEntityCreateService lockABlock(int clientID);
	public void unlockBlock(IEntityCreateService service) throws WrongBlockExeption;
	public void heartbeat(int clientID, int blockID);
	public IMaintainService getMaintainService();
	public IEntityService getEntityService(int nativeID);
	public IEntityCreateService getCreateService(int nativeID);
}
