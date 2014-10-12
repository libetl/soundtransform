package org.toilelibre.libe.soundtransform.transforms;

import java.util.List;

import org.toilelibre.libe.soundtransform.objects.Note;
import org.toilelibre.libe.soundtransform.objects.Pack;
import org.toilelibre.libe.soundtransform.objects.Sound;
import org.toilelibre.libe.soundtransform.observer.LogAware;
import org.toilelibre.libe.soundtransform.observer.LogEvent;
import org.toilelibre.libe.soundtransform.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.observer.TransformObserver;
import org.toilelibre.libe.soundtransform.sound.SoundAppender;

public class ShapeSoundTransformation implements SoundTransformation, LogAware {

	private TransformObserver []	observers;
	private Pack	             pack;
	private String	             instrument;

	public ShapeSoundTransformation (Pack pack, String instrument) {
		this.pack = pack;
		this.instrument = instrument;
	}

	@Override
	public Sound transform (Sound sound) {
		int threshold = 100;
		int channelNum = sound.getChannelNum ();
		Sound builtSound = new Sound (new long [sound.getSamples ().length], sound.getNbBytesPerSample (), sound.getSampleRate (), channelNum);

		List<Integer> freqs;
		this.log (new LogEvent (LogLevel.VERBOSE, "Finding loudest frequencies"));

		PeakFindSoundTransformation peak = new PeakFindSoundTransformation (100, 
				(int)Math.pow (2, Math.ceil (Math.log (sound.getSamples ().length) / Math.log (2))));
		peak.transform (sound);
		freqs = peak.getLoudestFreqs ();

		double lastFreq = freqs.get (0);
		int lastBegining = 0;
		for (int i = 0; i < freqs.size (); i++) {
			this.log (new LogEvent (LogLevel.VERBOSE, "Iteration " + i + " / " + freqs.size ()));
			int length = (i - lastBegining < 1 ? freqs.size() * threshold : (i - 1 - lastBegining) * threshold);
			if (i == freqs.size () - 1 || (Math.abs (freqs.get (i) - lastFreq) > freqs.get (i) / 100 && length > sound.getSampleRate () / 2)) {
				Note note = this.pack.get (this.instrument).getNearestNote ((int) lastFreq);
				Sound attack = note.getAttack ((int) lastFreq, channelNum, length);
				Sound decay = note.getDecay ((int) lastFreq, channelNum, length);
				Sound sustain = note.getSustain ((int) lastFreq, channelNum, length);
				Sound release = note.getRelease ((int) lastFreq, channelNum, length);
				SoundAppender.append (builtSound, threshold * lastBegining, attack, decay, sustain, release);
				lastBegining = i;
				lastFreq = freqs.get (i);
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
