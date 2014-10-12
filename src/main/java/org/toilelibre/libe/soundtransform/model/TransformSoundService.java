package org.toilelibre.libe.soundtransform.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.toilelibre.libe.soundtransform.model.converted.CallTransformService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.TransformInputStreamService;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.Observer;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class TransformSoundService implements LogAware {

	Observer []	    observers	= new Observer [0];

	private TransformInputStreamService	transformInputStreamService;
	private CallTransformService	    callTransformService;
	private ConvertAudioFileService     convertAudioFileService;

	public TransformSoundService (Observer... observers) {
		this.setObservers (observers);
		this.transformInputStreamService = new TransformInputStreamService (observers);
		this.callTransformService = new CallTransformService (observers);
		this.convertAudioFileService = new ConvertAudioFileService ();
	}

	public Sound [] convertAndApply (AudioInputStream ais, SoundTransformation... transforms) throws IOException {
		Sound [] in = this.transformInputStreamService.fromInputStream (ais);
		Sound [] out = this.callTransformService.transformAudioStream (in, transforms);
		return out;
	}

	public AudioInputStream transformAudioStream (AudioInputStream ais, SoundTransformation... transforms) throws IOException {
		return this.toStream (this.convertAndApply (ais, transforms), ais.getFormat ());
	}

	private void notifyAll (String s) {
		this.log (new LogEvent (LogLevel.INFO, s));
	}

	@Override
	public void log (LogEvent event) {
		for (Observer to : this.observers) {
			to.notify (event);
		}

	}

	public Sound [] fromInputStream (InputStream ais, int channels, long frameLength, int sampleSize, double sampleRate, boolean bigEndian, boolean pcmSigned) throws IOException {
		return this.transformInputStreamService.fromInputStream (ais, channels, frameLength, sampleSize, sampleRate, bigEndian, pcmSigned);
	}

	public Sound [] fromInputStream (AudioInputStream ais) throws IOException {
		return this.transformInputStreamService.fromInputStream (ais);
	}

	public AudioInputStream toStream (Sound [] channels, AudioFormat audioFormat) {
		return this.transformInputStreamService.toStream (channels, audioFormat);
	}

	public void transformFile (File fOrigin, File fDest, SoundTransformation... sts) throws UnsupportedAudioFileException, IOException {
		File file = fOrigin;
		AudioInputStream ais1 = convertAudioFileService.callConverter (file);
		this.notifyAll ("input : " + ais1.getFormat ().toString ());
		AudioInputStream ais2 = ais1;
		ais2 = this.transformAudioStream (ais1, sts);
		AudioSystem.write (ais2, AudioFileFormat.Type.WAVE, fDest);
		this.notifyAll ("Wrote output");
		this.notifyAll ("output : " + ais2.getFormat ().toString ());
	}

	@Override
	public void setObservers (Observer [] observers2) {
		this.observers = observers2;
		for (Observer observer : observers2) {
			this.notifyAll ("Adding observer " + observer.getClass ().getSimpleName ());
		}
	}
}
