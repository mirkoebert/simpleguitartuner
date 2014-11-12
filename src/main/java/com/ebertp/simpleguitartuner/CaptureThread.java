package com.ebertp.simpleguitartuner;

import javax.sound.sampled.TargetDataLine;

class CaptureThread extends Thread {

	private TargetDataLine targetDataLine;

	private double freqMin;
	private double freqMax;
	private double freqOK;

	private final static double divi = 8.192;
	private final static int sampleSize = 8192;
	private final static int spectreSize = 32768; // sampleSize * 2 * 2;

	public CaptureThread(final TargetDataLine targetDataLine) {
		this.targetDataLine = targetDataLine;
	}

	public void setFreq(final double min, final double max, final double ok) {
		freqMin = min;
		freqMax = max;
		freqOK = ok;
	}

	@Override
	public void run() {
		try {
			byte data[] = new byte[spectreSize];
			targetDataLine.start();
			double[] ar = new double[spectreSize];
			double[] ai = new double[spectreSize];

			while ((targetDataLine.read(data, 0, sampleSize) > 0)) {
				try {
					for (int i = 0; i < sampleSize; i++) {
						ar[i] = data[i];
					}
					for (int i = sampleSize; i < spectreSize; i++) {
						ar[i] = 0.0;
					}
					computeFFT(spectreSize, ar, ai);

					double maxAmpl = 0;
					double maxIndex = 0;
					double erreur = 0;

					for (int i = (int) (freqMin * divi); i < (freqMax * divi); i++) {
						if (Math.abs(ai[i]) > maxAmpl) {
							maxAmpl = Math.abs(ai[i]);
							maxIndex = i;
						}
					}
					if (maxAmpl > 0.02) {
						double f = maxIndex / divi;
						erreur = ((f - freqOK) / (freqOK - freqMin));
						System.out.println("f: " + f + "\t freqOK: " + freqOK + "\t deviation: " + erreur);
					}

				} catch (Exception e2) {
					System.out.println(e2);
				}
				targetDataLine.flush();
			}

		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
	}

	public static void computeFFT(final int n, final double[] ar, final double[] ai) {
		final double scale = 2.0 / n;
		int i, j;
		for (i = j = 0; i < n; ++i) {
			if (j >= i) {
				double tempr = ar[j] * scale;
				double tempi = ai[j] * scale;
				ar[j] = ar[i] * scale;
				ai[j] = ai[i] * scale;
				ar[i] = tempr;
				ai[i] = tempi;
			}
			int m = n / 2;
			while ((m >= 1) && (j >= m)) {
				j -= m;
				m /= 2;
			}
			j += m;
		}
		int mmax, istep;
		for (mmax = 1, istep = 2 * mmax; mmax < n; mmax = istep, istep = 2 * mmax) {
			double delta = Math.PI / mmax;
			for (int m = 0; m < mmax; ++m) {
				double w = m * delta;
				double wr = Math.cos(w);
				double wi = Math.sin(w);
				for (i = m; i < n; i += istep) {
					j = i + mmax;
					double tr = wr * ar[j] - wi * ai[j];
					double ti = wr * ai[j] + wi * ar[j];
					ar[j] = ar[i] - tr;
					ai[j] = ai[i] - ti;
					ar[i] += tr;
					ai[i] += ti;
				}
			}
			mmax = istep;
		}
	}

}