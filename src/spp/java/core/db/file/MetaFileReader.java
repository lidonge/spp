package spp.java.core.db.file;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MetaFileReader {
	private int maxMetaID;
	private int version;
	
	public int getLastMetaID() {
		return this.maxMetaID;
	}
	public int getVersion() {
		return this.version;
	}
	public void read(InputStream input,ArrayList<IMetaData> metas,HashMap<String, Integer> maps) throws IOException{
		DataInputStream in = new DataInputStream(input);
		version = in.readUnsignedShort();
		maxMetaID = in.readUnsignedShort();
		
		while(in.available() != 0) {
			int metaID = in.readUnsignedShort();
			
			String clsname = this.readVarchar(in);

			MetaData meta = new MetaData(clsname,metaID );			
			metas.add(meta);
			maps.put(clsname, metaID);
			readProperties(meta,in);
		}
	}
	private void readProperties(MetaData meta, DataInputStream in) throws IOException {
		int propCount = in.readUnsignedShort();
		for(int i = 0;i<propCount;i++) {
			String name = readVarchar(in);
			byte type = in.readByte();
			byte modifiers = in.readByte();
			meta.addProp(name, type, modifiers);
		}
	}
	private byte[] buffer = new byte[256];
	private String readVarchar(DataInputStream in) throws IOException{
		int nameLen = in.readUnsignedByte();
		in.read(buffer,0,nameLen);
		return new String(buffer,0,nameLen);
	}
}
