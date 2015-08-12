package org.toilelibre.libe.soundtransform.model.inputstream;

import java.util.Locale;

import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class StreamInfo extends FormatInfo {
    /**
     *
     */
    private static final long serialVersionUID = 3374492428338107884L;
    private final int         channels;
    private final long        frameLength;
    private final boolean     bigEndian;
    private final boolean     pcmSigned;
    private final String      taggedInfo;

    public StreamInfo (final int channels, final long frameLength, final int sampleSize, final float sampleRate, final boolean bigEndian, final boolean pcmSigned, final String taggedInfo) {
        super (sampleSize, sampleRate);
        this.channels = channels;
        this.frameLength = frameLength;
        this.bigEndian = bigEndian;
        this.pcmSigned = pcmSigned;
        this.taggedInfo = taggedInfo;
    }

    public static StreamInfo from (final FormatInfo fi, final Sound sound) {
        if (fi instanceof StreamInfo) {
            return (StreamInfo) fi;
        }
        return new StreamInfo (sound.getNumberOfChannels (), sound.getSamplesLength (), fi.getSampleSize (), fi.getSampleRate (), false, true, null);
    }

    public int getChannels () {
        return this.channels;
    }

    public long getFrameLength () {
        return this.frameLength;
    }

    public String getTaggedInfo () {
        return this.taggedInfo;
    }

    public boolean isBigEndian () {
        return this.bigEndian;
    }

    public boolean isPcmSigned () {
        return this.pcmSigned;
    }

    @Override
    public String toString () {
        return String
                .format (Locale.ENGLISH, "%1s %1s, %1s, %1d bytes/frame, %6s", this.isPcmSigned () ? "PCM_SIGNED" : "PCM_UNSIGNED", super.toString (), this.getChannels () == 1 ? "mono" : "stereo", this.getSampleSize () * this.getChannels (), this.isBigEndian () ? "big-endian" : "little-endian");
    }
}
