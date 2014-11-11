package com.ebertp.simpleguitartuner;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public final class SimpleTuner {

	public void start(final String saite) {
		double freqMin;
		double freqMax;
		double freqOK;

		if (saite.equals("E")) {
			freqMin = 77.781;
			freqMax = 87.307;
			freqOK = 82.406;
		} else if (saite.equals("A")) {
			freqMin = 103.826;
			freqMax = 116.540;
			freqOK = 110.0;
		} else if (saite.equals("D")) {
			freqMin = 138.591;
			freqMax = 155.563;
			freqOK = 146.832;
		} else if (saite.equals("G")) {
			freqMin = 184.997;
			freqMax = 207.652;
			freqOK = 195.997;
		} else if (saite.equals("H")) {
			freqMin = 233.081;
			freqMax = 261.625;
			freqOK = 246.941;
		} else {// if (saite.equals("e")) {
			freqMin = 311.126;
			freqMax = 349.228;
			freqOK = 329.627;
		}

		AudioFormat audioFormat = new AudioFormat(8000.0F, 8, 1, true, false);
		DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
		try {
			TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);

			CaptureThread captureThread = new CaptureThread(targetDataLine);
			captureThread.setFreq(freqMin, freqMax, freqOK);
			captureThread.start();
		} catch (Exception e2) {
			System.out.println("Error: Unable to start sound data acqusition: " + e2.getLocalizedMessage());
		}
	}

	public static void main(final String args[]) {
		SimpleTuner st = new SimpleTuner();
		st.start(args[0]);
	}
}
