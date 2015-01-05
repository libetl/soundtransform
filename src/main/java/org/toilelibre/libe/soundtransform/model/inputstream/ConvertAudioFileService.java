package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class ConvertAudioFileService {

	private final AudioFileHelper	audioFileHelper;
	private final AudioFormatParser	audioFormatParser;

	public ConvertAudioFileService () {
		this.audioFileHelper = new org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.JavazoomAudioFileHelper ();
		this.audioFormatParser = new org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.AudioFormatParser ();
	}

	public InputStreamInfo callAudioFormatParser (final InputStream is) throws SoundTransformException {
		return this.audioFormatParser.getInputStreamInfo (is);
	}

	public InputStream callConverter (final File file) throws SoundTransformException {
		return this.audioFileHelper.getAudioInputStream (file);
	}

	public InputStream toStream (final byte [] byteArray, final InputStreamInfo inputStreamInfo) throws SoundTransformException {
		return this.audioFileHelper.toStream (byteArray, this.audioFormatParser.audioFormatfromInputStreamInfo (inputStreamInfo));
	}

	public void writeInputStream (final InputStream ais2, final File fDest) throws SoundTransformException {
		this.audioFileHelper.writeInputStream (ais2, fDest);
	}
}
