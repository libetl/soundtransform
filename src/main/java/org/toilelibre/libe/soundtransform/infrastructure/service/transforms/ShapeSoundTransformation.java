package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.library.note.Note;
import org.toilelibre.libe.soundtransform.model.library.note.Silence;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class ShapeSoundTransformation implements SoundTransformation, LogAware {

	private Observer []	  observers;
	private Pack	      pack;
	private String	      instrument;
	private SoundAppender	soundAppender;

	public ShapeSoundTransformation (Pack pack, String instrument) {
		this.pack = pack;
		this.instrument = instrument;
		this.soundAppender = new org.toilelibre.libe.soundtransform.infrastructure.service.appender.ConvertedSoundAppender ();
	}

	@Override
	public Sound transform (Sound sound) {
		int threshold = 100;
		Note silence = new Silence ();
		int channelNum = sound.getChannelNum ();
		Sound builtSound = new Sound (new long [sound.getSamples ().length], sound.getNbBytesPerSample (), sound.getSampleRate (), channelNum);

		List<Integer> freqs;
		this.log (new LogEvent (LogLevel.VERBOSE, "Finding loudest frequencies"));

		PeakFindWithHPSSoundTransformation peak = new PeakFindWithHPSSoundTransformation (threshold, -1);
		peak.setObservers (this.observers);
		peak.transform (sound);
		freqs = peak.getLoudestFreqs ();

		double lastFreq = freqs.get (0);
		int lastBegining = 0;
		for (int i = 0; i < freqs.size (); i++) {
			this.log (new LogEvent (LogLevel.VERBOSE, "Iteration " + i + " / " + freqs.size ()));
			float lengthInSeconds = (i - lastBegining < 1 ? freqs.size () * threshold : (i - 1 - lastBegining) * threshold) / sound.getSampleRate();
			if (i == freqs.size () - 1 || (Math.abs (freqs.get (i) - lastFreq) > freqs.get (i) / 100 && lengthInSeconds > 0.5)) {
				Note note = silence;
				if (lastFreq > 50 && Math.abs (sound.getSampleRate () - lastFreq) > 100){
					note = this.pack.get (this.instrument).getNearestNote ((int) lastFreq);
				}
				Sound attack = note.getAttack ((int) lastFreq, channelNum, lengthInSeconds);
				Sound decay = note.getDecay ((int) lastFreq, channelNum, lengthInSeconds);
				Sound sustain = note.getSustain ((int) lastFreq, channelNum, lengthInSeconds);
				Sound release = note.getRelease ((int) lastFreq, channelNum, lengthInSeconds);
				soundAppender.append (builtSound, threshold * lastBegining, attack, decay, sustain, release);
				lastBegining = i;
				lastFreq = freqs.get (i);
			}
		}

		return builtSound;
	}

	@Override
	public void setObservers (Observer [] observers1) {
		this.observers = observers1;
	}

	@Override
	public void log (LogEvent logEvent) {
		for (Observer transformObserver : this.observers) {
			transformObserver.notify (logEvent);
		}
	}

}
