package spp.java.core.db.file;

import java.util.ArrayList;
import java.util.HashMap;

public class MetaData implements IMetaData {
	private String name;
	private int metaID;
	private ArrayList<IPropertyMeta> props = new ArrayList<>();
	private HashMap<String, Integer> maps = new HashMap<>();
	
	protected MetaData(String name, int metaID) {
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
	public void setMetaID(int id) {
		this.metaID = id;
	}
	
	@Override
	public IPropertyMeta getProperty(int index) {
		return props.get(index);
	}

	@Override
	public int mapNameToIndex(String name) {
		return maps.get(name);
	}

	@Override
	public int size() {
		return props.size();
	}

	protected boolean addProp(String name, int type, byte modifiers) {
		if(maps.containsKey(name))
			return false;

		final int propIndex = props.size();
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
			public byte getModifiers() {
				return modifiers;
			}

			@Override
			public boolean isNullable() {
				return (modifiers & MODIFIER_NULLABLE) != 0;
			}

			@Override
			public boolean isQuantum() {
				return (modifiers & MODIFIER_QUANTUM) != 0;
			}

			@Override
			public boolean isNon_Nagative() {
				return (modifiers & MODIFIER_NON_NAGATIVE) != 0;
			}
			
		};
		this.props.add(prop);
		maps.put(name, propIndex);
		return true;
	}
}
