package org.toilelibre.libe.soundtransform.transforms;

import org.toilelibre.libe.soundtransform.Sound2Note;
import org.toilelibre.libe.soundtransform.objects.Note;
import org.toilelibre.libe.soundtransform.objects.PacksList;
import org.toilelibre.libe.soundtransform.objects.Sound;
import org.toilelibre.libe.soundtransform.observer.LogAware;
import org.toilelibre.libe.soundtransform.observer.LogEvent;
import org.toilelibre.libe.soundtransform.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.observer.TransformObserver;

public class ShapeSoundTransformation implements SoundTransformation, LogAware {

	private TransformObserver [] observers;

	public ShapeSoundTransformation () {
	}

	@Override
	public Sound transform (Sound sound) {
		this.log (new LogEvent (LogLevel.VERBOSE, "Loading packLists"));
		PacksList packsList = PacksList.getInstance ();
		int threshold = 100;
		int channelNum = sound.getChannelNum ();
		Sound builtSound = new Sound (new long [0], sound.getNbBytesPerSample (), sound.getFreq (), channelNum);
		
		double [] freqs = new double [sound.getSamples ().length / threshold + 1];
		Sound2Note.getSoundLoudestFreqs (freqs, sound, threshold);
		
		double lastFreq = freqs [0];
		int lastBegining = 0;
		for (int i = 0 ; i < freqs.length ; i++){
			this.log (new LogEvent (LogLevel.VERBOSE, "Iteration " + i + " / " + freqs.length));
			if (Math.abs (freqs [i] - lastFreq) > freqs [i] / 100){
				
				int length = (i - 1 - lastBegining) * threshold;
				Note n = packsList.defaultPack.get ("piano").getNearestNote ((int)lastFreq);
				builtSound.concat (n.getAttack ((int)lastFreq, length) [channelNum],
						n.getDecay ((int)lastFreq, length) [channelNum],
						n.getSustain ((int)lastFreq, length) [channelNum],
						n.getRelease ((int)lastFreq, length) [channelNum]);
				
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
