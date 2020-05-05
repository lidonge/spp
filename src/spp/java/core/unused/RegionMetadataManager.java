package spp.java.core.unused;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import spp.java.core.IParticipantMeta;
import spp.java.core.IRegionMetaManager;
import spp.java.core.db.file.IPropertyMeta;
import spp.java.core.util.ParticipantMeta;

/**
 * File structure:
 * Metadata file: Region123_metadata.def, variable length file
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
 */
public class RegionMetadataManager implements IRegionMetaManager{
	private ArrayList<IParticipantMeta> metas = new ArrayList<>();
	private HashMap<String, Integer> maps = new HashMap<>();
	
	public RegionMetadataManager(InputStream input) throws IOException{
		DataInputStream in = new DataInputStream(input);
		int metaID = 0;
		while(in.available() != 0) {
			String clsname = this.readString(in);
			int strPropsCount = in.readShort();
			int nonStrPropsCount = in.readShort();

			IParticipantMeta meta = new ParticipantMeta(clsname,metaID ) {
				
				IParticipantMeta init() throws IOException{
					for(int i = 0;i<strPropsCount;i++) {
						this.addProp(IPropertyMeta.TYPE_STRING, readString(in));
					}
					for(int i = 0;i<nonStrPropsCount;i++) {
						byte type = in.readByte();
						this.addProp(type, readString(in));
					}
					return this;
				}

			}.init();
			
			this.metas.add(meta);
			this.maps.put(clsname, metaID++);
		}
	}
	
	@Override
	public int getMetadatas(){
		return metas.size();
	}
	
	@Override
	public IParticipantMeta getMetadata(int index) {
		return metas.get(index);
	}
	
	@Override
	public void addNewMetadata(OutputStream out, IParticipantMeta meta) throws IOException {
		int metaID = this.metas.size() - 1;
		meta.setMetaID(metaID);
		metas.add(meta);
		this.maps.put(meta.getName(), metaID);
		
		DataOutputStream dout = new DataOutputStream(out);
		String clsname = meta.getName();
		int strPropsCount = meta.getNQProperties();
		int nonStrPropsCount = meta.getQuantumProperties();
		writeString(dout, clsname);
		dout.writeShort(strPropsCount);
		dout.writeShort(nonStrPropsCount);
		
		for(int i = 0;i<strPropsCount;i++) {
			writeString(dout,meta.getProperty(i).getName());
		}
		for(int i = 0;i<nonStrPropsCount;i++) {
			IPropertyMeta pmeta = meta.getProperty(i + strPropsCount);
			dout.writeByte(pmeta.getType());
			writeString(dout, pmeta.getName());
		}
	}
	private String readString(DataInputStream in) throws IOException{
		short nameLen = in.readShort();
		byte[] bytes = new byte[nameLen];
		in.read(bytes);
		return new String(bytes);
	}

	private void writeString(DataOutputStream out, String s) throws IOException{
		out.writeShort(s.length());
		out.write(s.getBytes());;
	}

	@Override
	public IParticipantMeta nameToMetadata(String name) {
		return getMetadata(maps.get(name));
	}
}
