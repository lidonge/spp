package spp.java.core.db.region;

import java.io.IOException;

import spp.java.core.IRegionWriter;
import spp.java.core.db.file.IPropertyMeta;

public class Tester {
	IRegionWriter regionWriter;
	public void init(String dataPath, int regionID) throws IOException {
		regionWriter = RegionWriter.getInstance(dataPath, regionID);
	}
	
	public void init(String dataPath) throws IOException {
		regionWriter = RegionWriter.getInstance(dataPath);
	}
	
	public int lockBlock() {
		int ret = -1;
//		int blockid = regionWriter.lockABlock(123);
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				while(true) {
//					regionWriter.heartbeat(123, blockid);
//					try {
//						Thread.currentThread().sleep(1000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//			
//		});
		return ret;
	}
	
	/**
	 * Initialize with given regionid.
	 * @throws IOException 
	 */
	public void scense1(String dataPath, int regionid) throws IOException {
		if(regionid != -1)
			init(dataPath, regionid);
		else
			init(dataPath);
	}
	
	/**
	 * Define new entity
	 * @throws IOException 
	 */
	public void scense2() throws IOException {
		ParticipantMeta meta = new ParticipantMeta("human",0) {
			ParticipantMeta init() {
				this.addProp(IPropertyMeta.TYPE_STRING, "First Name");
				this.addProp(IPropertyMeta.TYPE_STRING, "Last Name");
				this.addProp(IPropertyMeta.TYPE_STRING, "Born Address");
				this.addProp(IPropertyMeta.TYPE_TIMESTAMP, "Born Date");
				this.addProp(IPropertyMeta.TYPE_BYTE, "Sex");
				return this;
			}
		}.init();
		regionWriter.getMaintainService().defineEntityMeta(meta);
	}
	public static void main(String[] args) {
		Tester tester = new Tester();
		try {
			tester.scense1(args[0], 123);
			tester.scense2();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
