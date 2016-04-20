package com.onescream.engine;

/**
 * FFT Manager Class
 * 
 * @author Anwar Almojarkesh
 *
 */

public class FFTMgr {
	private int m_nBufferSize;
	private int m_nSampleRate;

	private int spectrum_size;

	private float bandWidth;

	protected double[] real;
	protected double[] imag;
	protected float[] spectrum;

	public FFTMgr(int bufferSize, int sampleRate) {
		m_nBufferSize = bufferSize;
		m_nSampleRate = sampleRate;

		spectrum_size = bufferSize / 2 + 1;
		spectrum = new float[spectrum_size];

		bandWidth = (2f / bufferSize) * ((float) sampleRate / 2f);

		/** Allocate array **/
		real = new double[bufferSize];
		imag = new double[bufferSize];

		for (int i = 0; i < bufferSize; i++) {
			real[i] = 0;
			imag[i] = 0;
		}
	}

	/**
	 * Returns the index of the frequency band that contains the requested
	 * frequency.
	 * 
	 * @param freq
	 *            the frequency you want the index for (in Hz)
	 * @return the index of the frequency band that contains freq
	 */
	public int freqToIndex(float freq) {
		// special case: freq is lower than the bandwidth of spectrum[0]
		if (freq < bandWidth / 2)
			return 0;
		// special case: freq is within the bandwidth of
		// spectrum[spectrum.length - 1]
		if (freq > m_nSampleRate / 2.0f - bandWidth / 2.0f)
			return spectrum_size - 1;
		// all other cases
		float fraction = freq / (float) m_nSampleRate;
		int i = Math.round(m_nBufferSize * fraction);
		return i;
	}

	/**
	 * Returns the middle frequency of the i<sup>th</sup> band.
	 * 
	 * @param i
	 *            the index of the band you want to middle frequency of
	 */
	public float indexToFreq(int i) {
		float bw = bandWidth;
		// special case: the width of the first bin is half that of the others.
		// so the center frequency is a quarter of the way.
		if (i == 0)
			return bw * 0.25f;
		// special case: the width of the last bin is half that of the others.
		if (i == spectrum_size - 1) {
			float lastBinBeginFreq = (m_nSampleRate / 2.0f) - (bw / 2);
			float binHalfWidth = bw * 0.25f;
			return lastBinBeginFreq + binHalfWidth;
		}
		// the center frequency of the ith band is simply i*bw
		// because the first band is half the width of all others.
		// treating it as if it wasn't offsets us to the middle
		// of the band.
		return i * bw;
	}

	protected void fillSpectrum() {
		for (int i = 0; i < spectrum.length; i++) {
			spectrum[i] = (float) Math.sqrt(real[i] * real[i] + imag[i]
					* imag[i]);
		}
	}

	/**
	 * FFT forward
	 * 
	 * @param buffer
	 */
	public void forward(float[] buffer) {
		for (int i = 0; i < m_nBufferSize; i++) {
			real[i] = buffer[i];
			imag[i] = 0;
		}

		Fft.transform(real, imag);

		fillSpectrum();
	}

	/**
	 * Returns the amplitude of the requested frequency band.
	 * 
	 * @param i
	 *            the index of a frequency band
	 * @return the amplitude of the requested frequency band
	 */
	public float getBand(int i) {
		if (i < 0)
			i = 0;
		if (i > spectrum.length - 1)
			i = spectrum.length - 1;
		return spectrum[i];
	}

}
