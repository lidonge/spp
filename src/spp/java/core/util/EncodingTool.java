package spp.java.core.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import spp.java.core.IEntity;
import spp.java.core.UnsupportedTypeExeption;
import spp.java.core.db.file.IPropertyMeta;

public class EncodingTool {
	public static int toByteArray(int type, String value, byte[] buff) {
		FixSizeByteArrayOutputStream bout = new FixSizeByteArrayOutputStream(buff);
		DataOutputStream out = new DataOutputStream(bout);
		try {
			switch(type) {
				case IPropertyMeta.TYPE_BYTE:
				{
					byte v = Byte.parseByte(value);
					out.writeByte(v);
					break;
				}
				case IPropertyMeta.TYPE_DOUBLE:
				{
					double v = Double.parseDouble(value);
					out.writeDouble(v);
					break;
				}
				case IPropertyMeta.TYPE_FLOAT:
				{
					float v = Float.parseFloat(value);
					out.writeFloat(v);
					break;
				}
				case IPropertyMeta.TYPE_INT:
				{
					int v = Integer.parseInt(value);
					out.writeInt(v);
					break;
				}
				case IPropertyMeta.TYPE_LONG:
				case IPropertyMeta.TYPE_TIMESTAMP:
				{
					long v = Long.parseLong(value);
					out.writeLong(v);
					break;
				}
				case IPropertyMeta.TYPE_SHORT:
				{
					short v = Short.parseShort(value);
					out.writeShort(v);
					break;
				}
				default:
					throw new UnsupportedTypeExeption(type);
			}
		}catch(IOException e) {
			
		}
		return sizeOfType(type);
	}
	
	public static int sizeOfType(int type) {
		int ret = 0;
		switch(type){
		case IPropertyMeta.TYPE_BYTE:
			ret = 1;
			break;
		case IPropertyMeta.TYPE_DOUBLE:
			ret = 8;
			break;
		case IPropertyMeta.TYPE_FLOAT:
			ret = 2;
			break;
		case IPropertyMeta.TYPE_INT:
			ret = 4;
			break;
		case IPropertyMeta.TYPE_LONG:
			ret = 8;
			break;
		case IPropertyMeta.TYPE_SHORT:
			ret = 2;
			break;
		case IPropertyMeta.TYPE_TIMESTAMP:
			ret = 8;
			break;
		default:
			break;
		}
		return ret;
	}
	public static byte[] strToByteArray(IEntity entity) throws UnsupportedEncodingException {
		byte[] ret = null;
		int strLen = entity.getMetadata().getNQProperties();
		int strBytes = 0;
		for(int i = 0;i<strLen;i++)
			strBytes +=  entity.getStringProperty(i).length();
		ret = new byte[strBytes];
		int cur = 0;
		for(int i = 0;i<strLen;i++) {
			byte[] bytes = entity.getStringProperty(i).getBytes("UTF-8");
			System.arraycopy(bytes, 0, ret, cur, bytes.length);
			cur +=bytes.length;
		}
		return ret;
	}

	public static byte[] nonStrToByteArray(IEntity entity) {
		byte[] ret = entity.getNonStringProperties();
		return ret;
	}
}
