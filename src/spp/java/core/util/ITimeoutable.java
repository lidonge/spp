package spp.java.core.util;

public interface ITimeoutable {
	public long getStart();
	public int getTimeout();
	public void timeout();
}
