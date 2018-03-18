package com.ebertp.simpleguitartuner;

import java.util.Arrays;

import javax.sound.sampled.TargetDataLine;

final class CaptureThread extends Thread {

	private TargetDataLine targetDataLine;

	private double freqMin;
	private double freqMax;
	private double freqTarget;

	private final static double DIVI = 8.192;
	private final static int SAMPLE_SIZE = 8192;
	private final static int spectreSize = 32768; // sampleSize * 2 * 2;

	public CaptureThread(final TargetDataLine targetDataLine) {
		this.targetDataLine = targetDataLine;
	}

	public void setFreq(final double fmin, final double fmax, final double fok) {
		freqMin = fmin;
		freqMax = fmax;
		freqTarget = fok;
	}

	@Override
	public void run() {
		try {
			byte data[] = new byte[spectreSize];
			targetDataLine.start();
			double[] ar = new double[spectreSize];
			double[] ai = new double[spectreSize];
			Arrays.fill(ai, 0.0);
			while ((targetDataLine.read(data, 0, SAMPLE_SIZE) > 0)) {
				try {
					for (int i = 0; i < SAMPLE_SIZE; i++) {
						ar[i] = data[i];
					}
					computeFFT(spectreSize, ar, ai);

					double maxAmpl = 0;
					double maxIndex = 0;
					double fError = 0;

					for (int i = (int) (freqMin * DIVI); i < (freqMax * DIVI); i++) {
						if (Math.abs(ai[i]) > maxAmpl) {
							maxAmpl = Math.abs(ai[i]);
							maxIndex = i;
						}
					}
					if (maxAmpl > 0.02) {
						double freqCurrent = maxIndex / DIVI;
						fError = ((freqCurrent - freqTarget) / (freqTarget - freqMin));
						System.out.printf("\r");
						if (fError > 0.1) {
							System.out.format("Tune down.");
						} else if (fError < -0.1) {
							System.out.format("Tune up.");
						} else {
							System.out.format("Tune Ok.");
						}
						System.out.printf("\t f_target: %3.4f  f_current: %3.4f   deviation: %2.4f ", freqTarget, freqCurrent, fError);
					}
				} catch (Exception e2) {
					System.err.println("Error 1: "+e2.getLocalizedMessage());
					e2.printStackTrace();
				}
				targetDataLine.flush();
			}

		} catch (Exception e) {
			System.err.println("Error 2: "+e.getLocalizedMessage());
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