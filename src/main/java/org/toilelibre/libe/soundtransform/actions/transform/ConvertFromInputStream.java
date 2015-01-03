package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public final class ConvertFromInputStream extends Action {

    public Sound [] fromInputStream (final AudioInputStream ais) throws IOException {
        return this.transformSound.fromInputStream (ais);
    }

    public Sound [] fromInputStream (final InputStream ais, final int channels, final long frameLength, final int sampleSize, final double sampleRate,
            final boolean bigEndian, final boolean pcmSigned) throws IOException {
        return this.transformSound.fromInputStream (ais, channels, frameLength, sampleSize, sampleRate, bigEndian, pcmSigned);
    }
}
