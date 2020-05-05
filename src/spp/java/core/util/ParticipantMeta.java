package spp.java.core.util;

import java.util.ArrayList;
import java.util.HashMap;

import spp.java.core.IParticipantMeta;
import spp.java.core.db.file.IPropertyMeta;

public class ParticipantMeta implements IParticipantMeta {
	private String name;
	private int metaID;
	private ArrayList<IPropertyMeta> strProps = new ArrayList<>();
	private ArrayList<IPropertyMeta> nonStrProps = new ArrayList<>();
	private HashMap<String, Integer> maps = new HashMap<>();
	
	protected ParticipantMeta(String name, int metaID) {
		this.name = name;
		this.metaID = metaID;
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getMetaID() {
		return metaID;
	}

	@Override
	public IPropertyMeta getProperty(int index) {
		int strsize = strProps.size();
		if(index < strsize)
			return strProps.get(index);
		else
			return nonStrProps.get(index - strsize);
	}

	@Override
	public int mapNameToIndex(String name) {
		return maps.get(name);
	}

	@Override
	public int getNQProperties() {
		return strProps.size();
	}

	@Override
	public int getQuantumProperties() {
		return nonStrProps.size();
	}

	@Override
	public int size() {
		return getNQProperties() + getQuantumProperties();
	}

	protected boolean addProp(int type, String name) {
		if(maps.containsKey(name))
			return false;
		int index = 0;
		int curBytesPos = -1;
		if(type == IPropertyMeta.TYPE_STRING) {
			index = getNQProperties();
		}else {
			index = size();
			int cur = this.nonStrProps.size();
			if(cur != 0) {
				IPropertyMeta prop = this.nonStrProps.get(cur -1);
				curBytesPos = prop.getPostionIfNonString() + EncodingTool.sizeOfType(prop.getType());
			}
		}
		final int propIndex = index;
		final int propBytesPos = curBytesPos;
		IPropertyMeta prop = new IPropertyMeta() {

			@Override
			public int getPropertyIndex() {
				return propIndex;
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public int getType() {
				return type;
			}

			@Override
			public int getPostionIfNonString() {
				return propBytesPos;
			}
			
		};
		if(type == IPropertyMeta.TYPE_STRING) {
			this.strProps.add(prop);
		}else {
			this.nonStrProps.add(prop);
		}
		maps.put(name, index);
		return true;
	}
	@Override
	public int sizeofNonString() {
		int size = this.getQuantumProperties();
		int ret = 0;
		for(int i = 0;i<size;i++) {
			IPropertyMeta meta = this.nonStrProps.get(i);
			ret += EncodingTool.sizeOfType(meta.getType());
		}
		return ret;
	}
	@Override
	public void setMetaID(int id) {
		this.metaID = id;
	}
}
