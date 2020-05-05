package spp.java.core.db.region;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import spp.java.core.IFileManager;
import spp.java.core.IMaintainService;
import spp.java.core.IParticipantMeta;
import spp.java.core.IRegionMetaManager;
import spp.java.core.IRegionWriter;
import spp.java.core.unused.RegionMetadataManager;

class MaintainService implements IMaintainService {
	private IFileManager fileManager;
	private IRegionMetaManager metaManager;
	private int regionID;
	private OutputStream out;
	
	MaintainService(int regionID, IFileManager f) throws IOException {
		this.fileManager = f;
		this.regionID = regionID;
		InputStream in = null;
		try {
			String metaFile = IRegionWriter.REGION_FILE+ this.regionID+ "_" + IRegionWriter.META_FILE;
			in = fileManager.openForRead(metaFile);
			this.metaManager = new RegionMetadataManager(in);
			this.out = fileManager.openForAppend(metaFile);
		}finally {
			if(in != null) {
				in.close();
			}
		}
	}
	@Override
	public synchronized void defineEntityMeta(IParticipantMeta meta) throws IOException {
		this.metaManager.addNewMetadata(out,meta);
	}

	public void close() throws IOException {
		if(out != null) {
			out.close();
			out = null;
		}
	}
}
