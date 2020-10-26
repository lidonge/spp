package spp.java.core.db.file.test;

public class DemoStringGenerator {
	private String prefix = "";
	private int count = 0;
	public DemoStringGenerator(String prefix) {
		this.prefix = prefix;
	}
	
	public String next() {
		return prefix + count++ + "\n";
	}
	
	public void clear() {
		count = 0;
	}
}
