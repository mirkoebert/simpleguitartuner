package com.ebertp.simpleguitartuner;

import java.util.Scanner;

final class UserKeyboardInputThread extends Thread {

	private Scanner myScanner = new Scanner(System.in);
	private CaptureThread captureThread;

	public UserKeyboardInputThread(final CaptureThread captureThread) {
		this.captureThread = captureThread;
	}

	@Override
	public void run() {
		while (true) {
			String saite = getSaite();
			double freqMin;
			double freqMax;
			double freqOK;

			if (saite.equals("e1")) {
				freqMin = 77.781;
				freqMax = 87.307;
				freqOK = 82.406;
			} else if (saite.equals("a")) {
				freqMin = 103.826;
				freqMax = 116.540;
				freqOK = 110.0;
			} else if (saite.equals("d")) {
				freqMin = 138.591;
				freqMax = 155.563;
				freqOK = 146.832;
			} else if (saite.equals("g")) {
				freqMin = 184.997;
				freqMax = 207.652;
				freqOK = 195.997;
			} else if (saite.equals("h")) {
				freqMin = 233.081;
				freqMax = 261.625;
				freqOK = 246.941;
			} else {// if (saite.equals("e")) {
				freqMin = 311.126;
				freqMax = 349.228;
				freqOK = 329.627;
			}
			captureThread.setFreq(freqMin, freqMax, freqOK);
			try {
				sleep(1612);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private String getSaite() {
		String saitenName;
		String realSaite = "";

		saitenName = myScanner.next();

		if (saitenName.equalsIgnoreCase("e1")) {
			System.out.println("Tuning e1-String.");
			realSaite = "e1";
		} else if (saitenName.equalsIgnoreCase("a")) {
			System.out.println("Tuning a-String:.");
			realSaite = "a";
		} else if (saitenName.equalsIgnoreCase("d")) {
			System.out.println("Tuning d-String.");
			realSaite = "d";
		} else if (saitenName.equalsIgnoreCase("g")) {
			System.out.println("Tuning g-String.");
			realSaite = "g";
		} else if (saitenName.equalsIgnoreCase("h")) {
			System.out.println("Tuning h-String.");
			realSaite = "h";
		} else if (saitenName.equalsIgnoreCase("e2")) {
			System.out.println("Tuning e2-String.");
			realSaite = "e2";
		} else {
			System.out.println("The Character " + saitenName + " represents no guitar string. Please use e1, a, d, g, h or e2!");
		}
		return realSaite;
	}

}
