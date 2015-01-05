package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class TransformInputStreamService implements LogAware {

	public enum TransformInputStreamServiceErrorCode implements ErrorCode {
		COULD_NOT_READ_STREAM ("Could not read stream");

		private String	messageFormat;

		TransformInputStreamServiceErrorCode (final String mF) {
			this.messageFormat = mF;
		}

		@Override
		public String getMessageFormat () {
			return this.messageFormat;
		}
	}

	private Observer []	            observers	= new Observer [0];
	private final FrameProcessor	frameProcessor;
	private final AudioFormatParser	audioFormatParser;

	public TransformInputStreamService (final Observer... observers) {
		this.setObservers (observers);
		this.frameProcessor = new org.toilelibre.libe.soundtransform.infrastructure.service.frames.ByteArrayFrameProcessor ();
		this.audioFormatParser = new org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.AudioFormatParser ();
	}

	public Sound [] byteArrayToFrames (final byte [] byteArray, final InputStreamInfo isInfo) throws SoundTransformException {
		this.notifyAll ("[Test] byteArray -> ByteArrayInputStream");

		final ByteArrayInputStream bais = new ByteArrayInputStream (byteArray);
		return this.fromInputStream (bais, isInfo);
	}

	public Sound [] fromInputStream (final InputStream ais) throws SoundTransformException {
		return this.fromInputStream (ais, this.audioFormatParser.getInputStreamInfo (ais));
	}

	public Sound [] fromInputStream (final InputStream ais, final InputStreamInfo isInfo) throws SoundTransformException {
		this.notifyAll ("Converting input into java object");
		final Sound [] ret = this.frameProcessor.fromInputStream (ais, isInfo);
		return ret;
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

	public byte [] soundToByteArray (final Sound [] channels, final InputStreamInfo inputStreamInfo) {
		return this.frameProcessor.framesToByteArray (channels, inputStreamInfo.getSampleSize (), inputStreamInfo.isBigEndian (), inputStreamInfo.isPcmSigned ());
	}
}