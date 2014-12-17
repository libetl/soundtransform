package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public final class ConvertFromInputStream extends Action {

	public Sound [] fromInputStream (InputStream ais, int channels, long frameLength, int sampleSize, double sampleRate, boolean bigEndian, boolean pcmSigned) throws IOException {
		return this.transformSound.fromInputStream (ais, channels, frameLength, sampleSize, sampleRate, bigEndian, pcmSigned);
	}

	public Sound [] fromInputStream (AudioInputStream ais) throws IOException {
		return this.transformSound.fromInputStream (ais);
	}
}
