package com.onescream.info;

/**
 * The data class to store audio buffers
 *
 * Created by Anwar Almojarkesh
 */
public class BufferInfo {
	public short[] buf;
	public int real_length;
	
	public BufferInfo(short[] p_buf, int p_nRealLen) {
		buf = p_buf;
		real_length = p_nRealLen;
	}
}
