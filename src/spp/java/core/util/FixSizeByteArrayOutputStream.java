package spp.java.core.util;

import java.io.ByteArrayOutputStream;

public class FixSizeByteArrayOutputStream extends ByteArrayOutputStream {
	public FixSizeByteArrayOutputStream(byte[] buf) {
		this.buf = buf;
	}
	public FixSizeByteArrayOutputStream(int size) {
		super(size);
	}
    public synchronized byte[] toByteArray() {
        return buf;
    }
    public synchronized void write(int b) {
        buf[count] = (byte) b;
        count += 1;
    }

    public synchronized void write(byte b[], int off, int len) {
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }
}
