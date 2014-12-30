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
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.TransformInputStreamService;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class TransformSoundService implements LogAware {

	Observer []	                        observers	= new Observer [0];

	private final TransformInputStreamService	transformInputStreamService;
	private final CallTransformService	    callTransformService;
	private final ConvertAudioFileService	    convertAudioFileService;

	public TransformSoundService (final Observer... observers) {
		this.setObservers (observers);
		this.transformInputStreamService = new TransformInputStreamService (observers);
		this.callTransformService = new CallTransformService (observers);
		this.convertAudioFileService = new ConvertAudioFileService ();
	}

	public Sound [] convertAndApply (final AudioInputStream ais, final SoundTransformation... transforms) throws IOException {
		final Sound [] in = this.transformInputStreamService.fromInputStream (ais);
		final Sound [] out = this.callTransformService.transformAudioStream (in, transforms);
		return out;
	}

	public Sound [] fromInputStream (final AudioInputStream ais) throws IOException {
		return this.transformInputStreamService.fromInputStream (ais);
	}

	public Sound [] fromInputStream (final InputStream ais, final int channels, final long frameLength, final int sampleSize, final double sampleRate, final boolean bigEndian, final boolean pcmSigned) throws IOException {
		return this.transformInputStreamService.fromInputStream (ais, channels, frameLength, sampleSize, sampleRate, bigEndian, pcmSigned);
	}

	@Override
	public void log (final LogEvent event) {
		for (final Observer to : this.observers) {
			to.notify (event);
		}

	}

	private void notifyAll (final String s) {
		this.log (new LogEvent (LogLevel.INFO, s));
	}

	@Override
	public void setObservers (final Observer [] observers2) {
		this.observers = observers2;
		for (final Observer observer : observers2) {
			this.notifyAll ("Adding observer " + observer.getClass ().getSimpleName ());
		}
	}

	public AudioInputStream toStream (final Sound [] channels, final AudioFormat audioFormat) {
		return this.transformInputStreamService.toStream (channels, audioFormat);
	}

	public AudioInputStream transformAudioStream (final AudioInputStream ais, final SoundTransformation... transforms) throws IOException {
		return this.toStream (this.convertAndApply (ais, transforms), ais.getFormat ());
	}

	public void transformFile (final File fOrigin, final File fDest, final SoundTransformation... sts) throws UnsupportedAudioFileException, IOException {
		final File file = fOrigin;
		final AudioInputStream ais1 = this.convertAudioFileService.callConverter (file);
		this.notifyAll ("input : " + ais1.getFormat ().toString ());
		AudioInputStream ais2 = ais1;
		ais2 = this.transformAudioStream (ais1, sts);
		AudioSystem.write (ais2, AudioFileFormat.Type.WAVE, fDest);
		this.notifyAll ("Wrote output");
		this.notifyAll ("output : " + ais2.getFormat ().toString ());
	}
}
