package org.toilelibre.libe.soundtransform.model.inputstream;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public interface FrameProcessor {

    public abstract void byteArrayToFrame (byte [] frame, Sound [] sound, int position, boolean bigEndian, boolean pcmSigned, long neutral);

    public abstract byte [] framesToByteArray (Sound [] channels, int sampleSize, boolean bigEndian, boolean pcmSigned);

    long getNeutral (int sampleSize);

}