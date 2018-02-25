package com.ebertp.simpleguitartuner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public final class SimpleTuner {

	public void start() {
		System.out.println("Simple Guitar Tuner");
		System.out.println("To exit the progeramm, press <CTRL> + <C>");
		System.out.println("Which guitar string do you want to tune? Enter key: \033[0;1me1, a, d, g, h, e2\033[0;0m?");

		AudioFormat audioFormat = new AudioFormat(8000.0F, 8, 1, true, false);
		DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
		try {
			TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);

			CaptureThread captureThread = new CaptureThread(targetDataLine);

			UserKeyboardInputThread ukit = new UserKeyboardInputThread(captureThread);
			ukit.start();

			captureThread.start();
		} catch (Exception e2) {
			System.err.println("Error: Unable to start sound data acqusition: " + e2.getLocalizedMessage());
		}
	}

	public static void main(final String args[]) {
		SimpleTuner st = new SimpleTuner();
		st.start();
	}
}
