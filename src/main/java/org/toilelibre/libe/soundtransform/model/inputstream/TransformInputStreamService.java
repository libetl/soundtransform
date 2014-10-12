package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat.Encoding;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.Observer;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class TransformInputStreamService implements LogAware {

	Observer []	observers	= new Observer [0];
	FrameProcessor	     frameProcessor;

	public TransformInputStreamService (Observer... observers) {
		this.setObservers (observers);
		this.frameProcessor = new org.toilelibre.libe.soundtransform.infrastructure.service.frames.ByteArrayFrameProcessor ();
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
		this.notifyAll ("Converting input into java object");
		Sound [] ret = new Sound [channels];
		long neutral = (pcmSigned ? this.frameProcessor.getNeutral (sampleSize) : 0);
		for (int channel = 0; channel < channels; channel++) {
			ret [channel] = new Sound (new long [(int) frameLength], sampleSize, (int) sampleRate, channel);
		}
		for (int position = 0; position < frameLength; position++) {
			byte [] frame = new byte [sampleSize * channels];
			ais.read (frame);
			this.frameProcessor.byteArrayToFrame (frame, ret, position, bigEndian, pcmSigned, neutral);
		}
		this.notifyAll ("Convert done");
		return ret;
	}

	public Sound [] fromInputStream (AudioInputStream ais) throws IOException {
		int channels = ais.getFormat ().getChannels ();
		long frameLength = ais.getFrameLength ();
		int sampleSize = ais.getFormat ().getFrameSize () / channels;
		double sampleRate = ais.getFormat ().getSampleRate ();
		boolean bigEndian = ais.getFormat ().isBigEndian ();
		boolean pcmSigned = ais.getFormat ().getEncoding () == Encoding.PCM_SIGNED;

		return this.fromInputStream (ais, channels, frameLength, sampleSize, sampleRate, bigEndian, pcmSigned);
	}

	public Sound [] byteArrayToFrames (byte [] byteArray, int channels, long frameLength, int sampleSize, double sampleRate, boolean bigEndian, boolean pcmSigned) throws IOException {
		this.notifyAll ("[Test] byteArray -> ByteArrayInputStream");

		ByteArrayInputStream bais = new ByteArrayInputStream (byteArray);
		return this.fromInputStream (bais, channels, frameLength, sampleSize, sampleRate, bigEndian, pcmSigned);
	}

	public AudioInputStream toStream (Sound [] channels, AudioFormat audioFormat) {

		int length = audioFormat.getFrameSize () * channels [0].getSamples ().length;
		byte [] data = this.frameProcessor.framesToByteArray (channels, audioFormat.getFrameSize () / channels.length, audioFormat.isBigEndian (), audioFormat.getEncoding () == Encoding.PCM_SIGNED);
		this.notifyAll ("Creating output file");
		// now save the file
		ByteArrayInputStream bais = new ByteArrayInputStream (data);
		return new AudioInputStream (bais, audioFormat, length / audioFormat.getFrameSize ());
	}

	@Override
	public void setObservers (Observer [] observers2) {
		this.observers = observers2;
		for (Observer observer : observers2) {
			this.notifyAll ("Adding observer " + observer.getClass ().getSimpleName ());
		}
	}
}