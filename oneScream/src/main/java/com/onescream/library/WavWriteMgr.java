package com.onescream.library;

import java.io.DataOutputStream;
import java.io.FileOutputStream;

public class WavWriteMgr {
	/*
	 * WAV File Specification FROM
	 * http://ccrma.stanford.edu/courses/422/projects/WaveFormat/ The canonical
	 * WAVE format starts with the RIFF header: 0 4 ChunkID Contains the letters
	 * "RIFF" in ASCII form (0x52494646 big-endian form). 4 4 ChunkSize 36 +
	 * SubChunk2Size, or more precisely: 4 + (8 + SubChunk1Size) + (8 +
	 * SubChunk2Size) This is the size of the rest of the chunk following this
	 * number. This is the size of the entire file in bytes minus 8 bytes for
	 * the two fields not included in this count: ChunkID and ChunkSize. 8 4
	 * Format Contains the letters "WAVE" (0x57415645 big-endian form).
	 * 
	 * The "WAVE" format consists of two subchunks: "fmt " and "data": The
	 * "fmt " subchunk describes the sound data's format: 12 4 Subchunk1ID
	 * Contains the letters "fmt " (0x666d7420 big-endian form). 16 4
	 * Subchunk1Size 16 for PCM. This is the size of the rest of the Subchunk
	 * which follows this number. 20 2 AudioFormat PCM = 1 (i.e. Linear
	 * quantization) Values other than 1 indicate some form of compression. 22 2
	 * NumChannels Mono = 1, Stereo = 2, etc. 24 4 SampleRate 8000, 44100, etc.
	 * 28 4 ByteRate == SampleRate * NumChannels * BitsPerSample/8 32 2
	 * BlockAlign == NumChannels * BitsPerSample/8 The number of bytes for one
	 * sample including all channels. I wonder what happens when this number
	 * isn't an integer? 34 2 BitsPerSample 8 bits = 8, 16 bits = 16, etc.
	 * 
	 * The "data" subchunk contains the size of the data and the actual sound:
	 * 36 4 Subchunk2ID Contains the letters "data" (0x64617461 big-endian
	 * form). 40 4 Subchunk2Size == NumSamples * NumChannels * BitsPerSample/8
	 * This is the number of bytes in the data. You can also think of this as
	 * the size of the read of the subchunk following this number. 44 * Data The
	 * actual sound data.
	 */

	// our private variables
	private String myPath;
	private long myChannels;
	private long mySampleRate;
	private long myByteRate;
	private int myBlockAlign;
	private int myBitsPerSample;
	private long myDataSize;

	private DataOutputStream outFile;

	// I made this public so that you can toss whatever you want in here
	// maybe a recorded buffer, maybe just whatever you want
	public byte[] myData;

	// get/set for the Path property
	public String getPath() {
		return myPath;
	}

	public void setPath(String newPath) {
		myPath = newPath;
	}

	// empty constructor
	public WavWriteMgr() {
		myPath = "";

		setWaveInfo(44100, 16, 1);
	}

	// constructor takes a wav path
	public WavWriteMgr(String tmpPath) {
		myPath = tmpPath;

		setWaveInfo(44100, 16, 1);
	}

	public void setWaveInfo(int p_nSamplingFreq, int p_nBits, int p_nChannels) {
		mySampleRate = p_nSamplingFreq;
		myBitsPerSample = p_nBits;
		myChannels = p_nChannels;

		myByteRate = mySampleRate * myBitsPerSample / 8 * myChannels;
		myBlockAlign = (int) (myBitsPerSample / 8 * myChannels);
	}

	// write out the wav file
	public boolean startWriteFile(int p_nDataSize) {
		try {
			outFile = new DataOutputStream(new FileOutputStream(myPath));

			// write the wav file per the wav file format
			// 00 - RIFF
			outFile.writeBytes("RIFF");
			// 04 - how big is the rest of this file?
			outFile.write(intToByteArray((int) p_nDataSize + 36), 0, 4);
			// 08 - WAVE
			outFile.writeBytes("WAVE");
			// 12 - fmt
			outFile.writeBytes("fmt ");
			// 16 - size of this chunk
			outFile.write(intToByteArray(16), 0, 4);
			// 20 - what is the audio format? 1 for PCM = Pulse Code Modulation
			outFile.write(shortToByteArray((short) 1), 0, 2);
			// 22 - mono or stereo? 1 or 2? (or 5 or ???)
			outFile.write(shortToByteArray((short) myChannels), 0, 2);
			// 24 - samples per second (numbers per second)
			outFile.write(intToByteArray((int) mySampleRate), 0, 4);
			// 28 - bytes per second
			outFile.write(intToByteArray((int) myByteRate), 0, 4);
			// 32 - # of bytes in one sample, for all channels
			outFile.write(shortToByteArray((short) myBlockAlign), 0, 2);
			// 34 - how many bits in a sample(number)? usually 16 or 24
			outFile.write(shortToByteArray((short) myBitsPerSample), 0, 2);
			// 36 - data
			outFile.writeBytes("data");
			// 40 - how big is this data chunk
			outFile.write(intToByteArray((int) p_nDataSize), 0, 4);
			// 44 - the actual data itself - just a long string of numbers
			// ...

		} catch (Exception e) {
			// System.out.println(e.getMessage());
			return false;
		}

		return true;
	}
	
	public boolean appendData(short[] buf, int p_nRealLen) {
		if (outFile == null)
			return false;
		
		if (buf == null || p_nRealLen == 0 || p_nRealLen > buf.length)
			return false;
		
		try {
			byte[] tmpBuf = new byte[p_nRealLen * 2];
			for (int i = 0; i < p_nRealLen; i++) {
				tmpBuf[i * 2] = (byte)(buf[i] & 0xff);
				tmpBuf[i * 2 + 1] = (byte)((buf[i] >>> 8) & 0xff);
			}
			outFile.write(tmpBuf);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean finish() {
		if (outFile == null)
			return false;
		
		try {
			if (outFile != null)
				outFile.close();
			outFile = null;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	// return a printable summary of the wav file
	public String getSummary() {
		// String newline = System.getProperty("line.separator");
		String newline = "<br>";
		String summary = "<html>Format: 1(PCM)" + newline + "Channels: "
				+ myChannels + newline + "SampleRate: " + mySampleRate
				+ newline + "ByteRate: " + myByteRate + newline
				+ "BlockAlign: " + myBlockAlign + newline + "BitsPerSample: "
				+ myBitsPerSample + newline + "DataSize: " + myDataSize
				+ "</html>";
		return summary;
	}

	// ===========================
	// CONVERT BYTES TO JAVA TYPES
	// ===========================

	// these two routines convert a byte array to a unsigned short
	public static int byteArrayToInt(byte[] b) {
		int start = 0;
		int low = b[start] & 0xff;
		int high = b[start + 1] & 0xff;
		return (int) (high << 8 | low);
	}

	// these two routines convert a byte array to an unsigned integer
	public static long byteArrayToLong(byte[] b) {
		int start = 0;
		int i = 0;
		int len = 4;
		int cnt = 0;
		byte[] tmp = new byte[len];
		for (i = start; i < (start + len); i++) {
			tmp[cnt] = b[i];
			cnt++;
		}
		long accum = 0;
		i = 0;
		for (int shiftBy = 0; shiftBy < 32; shiftBy += 8) {
			accum |= ((long) (tmp[i] & 0xff)) << shiftBy;
			i++;
		}
		return accum;
	}

	// ===========================
	// CONVERT JAVA TYPES TO BYTES
	// ===========================
	// returns a byte array of length 4
	private static byte[] intToByteArray(int i) {
		byte[] b = new byte[4];
		b[0] = (byte) (i & 0x00FF);
		b[1] = (byte) ((i >> 8) & 0x000000FF);
		b[2] = (byte) ((i >> 16) & 0x000000FF);
		b[3] = (byte) ((i >> 24) & 0x000000FF);
		return b;
	}

	// convert a short to a byte array
	public static byte[] shortToByteArray(short data) {
		return new byte[] { (byte) (data & 0xff), (byte) ((data >>> 8) & 0xff) };
	}

}