package spp.java.core;

import java.io.IOException;

public interface IMaintainService {
	public void defineEntityMeta(IParticipantMeta meta) throws IOException;

	public void close() throws IOException;
}
