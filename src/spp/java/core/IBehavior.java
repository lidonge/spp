package spp.java.core;

import spp.java.core.db.file.IPropertyMeta;

public interface IBehavior {
	public String getName();
	public IPropertyMeta[] getProperties();
}
