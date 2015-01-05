package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public final class ExportSoundToInputStream extends Action {

	public InputStream toStream (final Sound [] channels, final InputStreamInfo inputStreamInfo) throws SoundTransformException {
		return this.transformSound.toStream (channels, inputStreamInfo);
	}
}
