package spp.java.core.db.file;

import java.io.File;

public interface IDatabase {
	public static final int MOFIDIER_REGION = 1;
	public static final int MOFIDIER_QUANTUM = 2;
	public boolean isRegion();
	public boolean isQuantumRegion();
	public int getModifiers();
	public int getRegionOrDomainID();
	public int getBlockCount();
	public String getDataBasePath();
	public String getRandomAccessPath();
	
	public default int sizeOfMetaFileHeader() {
		return 	2 + //version
				2 + //modifilers
				4 + //region id
				2 + //block count
				2; 	// max meta id
	}
	
	public default File getDefaultMetadataFile() {
		String regionOrDomain = this.isRegion() ? "Region" : "Domain"; 
		String metaFile = regionOrDomain+this.getRegionOrDomainID()+"_metadata.def";
		
		File f = new File(this.getDataBasePath(), metaFile);
		return f;
	}
	
	public default File getDefaultBlockFile(int blockID) {
		return this._getDefaultBlockFile(blockID, ".dat");
	}
	
	public default File getDefaultBlockIndexFile(int blockID) {
		return this._getDefaultBlockFile(blockID, ".idx");
	}
	
	public default File getDefaultBlockQuantumFile(int blockID) {
		return this._getDefaultBlockFile(blockID, ".qtm");
	}

	default File _getDefaultBlockFile(int blockID, String suffixe) {
		String regionOrDomain = this.isRegion() ? "Region" : "Domain"; 
		String blk = regionOrDomain+this.getRegionOrDomainID()+"_blk" + blockID +suffixe;
		
		File f = new File(this.getDataBasePath(), blk);
		return f;
	}
}
