package spp.java.core.db.region;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import spp.java.core.IEntity;
import spp.java.core.IEntityCreateService;
import spp.java.core.IEntityService;
import spp.java.core.IFileManager;
import spp.java.core.IMaintainService;
import spp.java.core.IParticipantMeta;
import spp.java.core.IRegionWriter;
import spp.java.core.WrongBlockExeption;
import spp.java.core.unused.IRegionFile;
import spp.java.core.util.EncodingTool;
import spp.java.core.util.UnsignedIntStack;

/**
 * This implement of IRegion is for create and update real entity, it not optimized for search.
 * @author Administrator
 *
 */
public class RegionWriter implements IRegionWriter {
	private static final byte INIT_HEART_BEAT = 5;
	private int regionID;
	private IRegionFile file;
	private UnsignedIntStack unlockedBlocks = new UnsignedIntStack();
	private int[] lockedBlockClient = new int[BLOCK_NUMBER];
	private byte[] lockedBlockActiveCount = new byte[BLOCK_NUMBER];
	private int min_unused_block = 0;
	
	private IFileManager fileManager;
	private IMaintainService maintainService;
	private IEntityCreateService entityCreateService;
	
	private static HashMap<Integer, IRegionWriter> instances = new HashMap<>();
	public static IRegionWriter getInstance(String dataPath) throws IOException{
		int regionID = IRegionWriter.getLocalRegionID(dataPath);
		if(regionID == -1)
			throw new IOException("Un-initialized Region " + dataPath);
		return _getInstance(dataPath, regionID);
	}
	public static IRegionWriter getInstance(String dataPath, int regionID) throws IOException{
		int rid = IRegionWriter.getLocalRegionID(dataPath);
		if(rid != -1 && rid != regionID)
			throw new IOException(dataPath + " is Error Region " + regionID +", the region " + rid +" already exists!");
	
		return _getInstance(dataPath, regionID);
	}
	private static final IRegionWriter _getInstance(String dataPath, int regionID) throws IOException{
		IRegionWriter ret = instances.get(regionID);
		if(ret == null) {
			synchronized(instances) {
				if(instances.get(regionID) == null) {
					ret = new RegionWriter(dataPath, regionID);
					instances.put(regionID, ret);
				}
			}
		}
		return ret;		
	}
	
	private RegionWriter(String dataPath, int regionID) throws IOException {
		fileManager = new FileManager(dataPath);
		maintainService = new MaintainService(regionID,fileManager);
//		entityCreateService = new EntityCreateService(fileManager, regionID, blockID);
		file = new RegionFile(regionID,dataPath);
		this.regionID = regionID;
		init();
	}
	
	private void init() throws IOException{
		for(int i = 0;i< lockedBlockClient.length;i++) {
			lockedBlockClient[i] = -1;
		}
		int min = file.getMinNoneDataBlock();
		for(int i = BLOCK_NUMBER - 1;i>= min;i--) {
			unlockedBlocks.push(i);
		}
		this.min_unused_block = min;
		int[] fullBlock = file.getFullBlocks();
		Arrays.sort(fullBlock);
		for(int i = min -1;i>=0;i--) {
			int idx = Arrays.binarySearch(fullBlock, 0, fullBlock.length - 1, i);
			if(idx == -1) {
				unlockedBlocks.push(i);
			}
		}
		
		startHeartbeatMonitor();
	}
	
	@Override
	public int getRegionID() {
		return regionID;
	}

	@Override
	public void createEntity(int clientID, IEntity entity) throws IOException, WrongBlockExeption {
		int nativeID = entity.getNativeID();
		//find the block of the entity exists, and the entity index in the block.
		int block = nativeID / BLOCK_SIZE;
		if(clientID != this.lockedBlockClient[block])
			throw new WrongBlockExeption(clientID, block);
		int indexInBlock = nativeID % BLOCK_SIZE;
		byte[] strings = EncodingTool.strToByteArray(entity);
		byte[] non_strings = EncodingTool.nonStrToByteArray(entity);
		file.appendEntity(block, indexInBlock, 
				entity.getMetadata().getMetaID(),strings, non_strings);
	}

	@Override
	public int lockABlock(int clientID) {
		int ret = unlockedBlocks.pop();
		this.lockedBlockActiveCount[ret] = INIT_HEART_BEAT;
		lockedBlockClient[ret] = clientID;
		if(ret > min_unused_block)
			min_unused_block = ret + 1;
		return ret;
	}

	@Override
	public void unlockBlock(int clientID, int blockID) throws WrongBlockExeption {
		if(this.lockedBlockClient[blockID] != clientID)
			throw new WrongBlockExeption(clientID, blockID);
		this.lockedBlockClient[blockID] = -1;
		this.unlockedBlocks.push(blockID);
		this.lockedBlockActiveCount[blockID]++;
	}
	
	Thread heartbeatMonitor = null;
	private void startHeartbeatMonitor() {
		synchronized(this) {
			if(this.heartbeatMonitor != null)
				return;
			heartbeatMonitor = new Thread(new Runnable() {
	
				@Override
				public void run() {
					for(int i = 0;i< min_unused_block;i++) {
						if(lockedBlockClient[i] == -1)
							continue;
						if(lockedBlockActiveCount[i] < 0)
							try {
								unlockBlock(lockedBlockClient[i], i);
							} catch (WrongBlockExeption e) {
								e.printStackTrace();
							}
						
						lockedBlockActiveCount[i]--;
					}
					try {
						Thread.currentThread().sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	
				}
				
			});
			this.heartbeatMonitor.start();
		}
	}
	@Override
	public void heartbeat(int clientID, int blockID) {

		if(this.lockedBlockClient[blockID] != clientID)
			return;
		if(this.lockedBlockActiveCount[blockID] < INIT_HEART_BEAT)
			this.lockedBlockActiveCount[blockID]++;
	}
	
	@Override
	public IEntityService getService(int nativeID) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public IEntityCreateService lockABlock(int clientID) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void unlockBlock(IEntityCreateService service) throws WrongBlockExeption {
		// TODO Auto-generated method stub
		
	}
	@Override
	public IEntityService getEntityService(int nativeID) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public IEntityCreateService getCreateService(int nativeID) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public IEntityCreateService lockABlock(int clientID) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public IMaintainService getMaintainService() {
		return this.maintainService;
	}

}
