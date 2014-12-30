package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.library.note.Note;
import org.toilelibre.libe.soundtransform.model.library.note.Silence;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class ShapeSoundTransformation implements SoundTransformation, LogAware {

	private Observer []	  observers;
	private final Pack	      pack;
	private final String	      instrument;
	private final SoundAppender	soundAppender;

	public ShapeSoundTransformation (final Pack pack, final String instrument) {
		this.pack = pack;
		this.instrument = instrument;
		this.soundAppender = new org.toilelibre.libe.soundtransform.infrastructure.service.appender.ConvertedSoundAppender ();
	}

	@Override
	public void log (final LogEvent logEvent) {
		for (final Observer transformObserver : this.observers) {
			transformObserver.notify (logEvent);
		}
	}

	@Override
	public void setObservers (final Observer [] observers1) {
		this.observers = observers1;
	}

	@Override
	public Sound transform (final Sound sound) {
		final int threshold = 100;
		final Note silence = new Silence ();
		final int channelNum = sound.getChannelNum ();
		final Sound builtSound = new Sound (new long [sound.getSamples ().length], sound.getNbBytesPerSample (), sound.getSampleRate (), channelNum);

		List<Integer> freqs;
		this.log (new LogEvent (LogLevel.VERBOSE, "Finding loudest frequencies"));

		final PeakFindWithHPSSoundTransformation peak = new PeakFindWithHPSSoundTransformation (threshold, -1);
		peak.setObservers (this.observers);
		peak.transform (sound);
		freqs = peak.getLoudestFreqs ();

		double lastFreq = freqs.get (0);
		int lastBegining = 0;
		int countZeros = 0;
		for (int i = 0; i < freqs.size (); i++) {
			this.log (new LogEvent (LogLevel.VERBOSE, "Iteration " + i + " / " + freqs.size ()));
			final float lengthInSeconds = (i - lastBegining < 1 ? freqs.size () * threshold : (i - 1 - lastBegining) * threshold * 1.0f) / sound.getSampleRate();
			final boolean freqChanged = Math.abs (freqs.get (i) - lastFreq) > freqs.get (i) / 100 && lengthInSeconds > 0.5;
			if (freqChanged && freqs.get (i) == 0){
				countZeros++;
			}else{
				countZeros = 0;
			}
			if (i == freqs.size () - 1 || freqChanged && (lastFreq == 0 || freqs.get (i) == 0 && countZeros >= 3)) {
				countZeros = 0;
				Note note = silence;
				if (lastFreq > 50 && Math.abs (sound.getSampleRate () - lastFreq) > 100){
					note = this.pack.get (this.instrument).getNearestNote ((int) lastFreq);
				}
				final Sound attack = note.getAttack ((int) lastFreq, channelNum, lengthInSeconds);
				final Sound decay = note.getDecay ((int) lastFreq, channelNum, lengthInSeconds);
				final Sound sustain = note.getSustain ((int) lastFreq, channelNum, lengthInSeconds);
				final Sound release = note.getRelease ((int) lastFreq, channelNum, lengthInSeconds);
				this.soundAppender.append (builtSound, threshold * lastBegining, attack, decay, sustain, release);
				lastBegining = i;
				lastFreq = freqs.get (i);
			}
		}

		return builtSound;
	}

}
