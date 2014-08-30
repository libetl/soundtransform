package org.toilelibre.libe.soundtransform.transforms;

import org.toilelibre.libe.soundtransform.Sound2Note;
import org.toilelibre.libe.soundtransform.objects.Note;
import org.toilelibre.libe.soundtransform.objects.Pack;
import org.toilelibre.libe.soundtransform.objects.Sound;
import org.toilelibre.libe.soundtransform.observer.LogAware;
import org.toilelibre.libe.soundtransform.observer.LogEvent;
import org.toilelibre.libe.soundtransform.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.observer.TransformObserver;

public class ShapeSoundTransformation implements SoundTransformation, LogAware {

	private TransformObserver []	observers;
	private Pack pack;
	private String instrument;

	public ShapeSoundTransformation (Pack pack, String instrument) {
		this.pack = pack;
		this.instrument = instrument;
	}

	@Override
	public Sound transform (Sound sound) {
		int threshold = 100;
		int channelNum = sound.getChannelNum ();
		Sound builtSound = new Sound (new long [sound.getSamples().length], 
				sound.getNbBytesPerSample (), sound.getFreq (), channelNum);

		double [] freqs = new double [sound.getSamples ().length / threshold + 1];
		this.log (new LogEvent (LogLevel.VERBOSE, "Finding loudest frequency"));
		Sound2Note.getSoundLoudestFreqs (freqs, sound, threshold);

		double lastFreq = freqs [0];
		int lastBegining = 0;
		int usedarraylength = 0;
		for (int i = 0; i < freqs.length; i++) {
			this.log (new LogEvent (LogLevel.VERBOSE, "Iteration " + i + " / " + freqs.length));
			if (Math.abs (freqs [i] - lastFreq) > freqs [i] / 100) {

				int length = (i - 1 - lastBegining) * threshold;
				Note note = this.pack.get (this.instrument).getNearestNote ((int) lastFreq);
				Sound attack = note.getAttack ((int) lastFreq, channelNum, length);
				Sound decay = note.getDecay ((int) lastFreq, channelNum, length);
				Sound sustain = note.getSustain ((int) lastFreq, channelNum, length);
				Sound release = note.getRelease ((int) lastFreq, channelNum, length);
				builtSound = builtSound.concat (true, usedarraylength, attack, decay, sustain, release);
				usedarraylength += attack.getSamples().length + decay.getSamples().length +
						sustain.getSamples().length + release.getSamples().length;
				lastBegining = i;
				lastFreq = freqs [i];
			}
		}

		return builtSound;
	}

	@Override
	public void setObservers (TransformObserver [] observers1) {
		this.observers = observers1;
	}

	@Override
	public void log (LogEvent logEvent) {
		for (TransformObserver transformObserver : this.observers) {
			transformObserver.notify (logEvent);
		}
	}

}
