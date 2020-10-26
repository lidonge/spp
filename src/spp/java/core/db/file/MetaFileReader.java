package spp.java.core.db.file;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MetaFileReader {
	private int maxMetaID;
	private IDatabase database;
	private ArrayList<IMetaData> metas = new ArrayList<>();
	private HashMap<String, Integer> maps = new HashMap<>();
	
	public MetaFileReader(String databasePath, String randomAccessPath) throws IOException {
		File f = new File(databasePath);
		if(!f.isDirectory())
			throw new IOException("The database path \""+ databasePath +"\" shuould be a directory!");
		File[] lists = f.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(".def");
			}
			
		});
		if(lists.length == 0)
			throw new IOException("The database path \""+ databasePath +"\" doesn't contains metadata file!");
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(lists[0]);
			BufferedInputStream bin = new BufferedInputStream(fin);
			DataInputStream din = new DataInputStream(bin);
			readHeader(databasePath, randomAccessPath, din);
			readMetadata(din);
		} finally {
			if(fin != null)
				fin.close();
		}
		
	}
	
	public int getLastMetaID() {
		return this.maxMetaID;
	}
	
	public IDatabase getDatabase() {
		return database;
	}
	
	private void readHeader(String databasePath, String randomAccessPath, DataInput in) throws IOException {
		int version = in.readUnsignedShort();
		int modifiers = in.readUnsignedShort();
		int id = in.readInt();
		int blockCount = in.readUnsignedShort();
		this.database = new IDatabase() {

			@Override
			public boolean isRegion() {
				return (modifiers & IDatabase.MOFIDIER_REGION) != 0;
			}

			@Override
			public boolean isQuantumRegion() {
				return isRegion() && ((modifiers & IDatabase.MOFIDIER_QUANTUM) != 0);
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
			public int getBlockCount() {
				return blockCount;
			}

			@Override
			public String getDataBasePath() {
				return databasePath;
			}

			@Override
			public String getRandomAccessPath() {
				return randomAccessPath;
			}
			
		};		
	}
	
	private void readMetadata(DataInput in) throws IOException{
		maxMetaID = in.readUnsignedShort();
		
		while(true) {
			try {
				int metaID = in.readUnsignedShort();
				
				String clsname = this.readVarchar(in);
	
				MetaData meta = new MetaData(clsname,metaID );			
				metas.add(meta);
				maps.put(clsname, metaID);
				readProperties(meta,in);
			}catch(EOFException e) {
				break;
			}
		}
	}
	private void readProperties(MetaData meta, DataInput in) throws IOException {
		int propCount = in.readUnsignedShort();
		for(int i = 0;i<propCount;i++) {
			String name = readVarchar(in);
			byte type = in.readByte();
			byte modifiers = in.readByte();
			meta.addProp(name, type, modifiers);
		}
	}
	private byte[] buffer = new byte[256];
	private String readVarchar(DataInput in) throws IOException{
		int nameLen = in.readUnsignedByte();
		in.readFully(buffer,0,nameLen);
		return new String(buffer,0,nameLen);
	}
}
