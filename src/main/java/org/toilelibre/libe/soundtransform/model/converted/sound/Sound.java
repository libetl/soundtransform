package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;

public final class Sound {

    private final Channel [] channels;

    public Sound (final Channel [] channels1) {
        this.channels = channels1 == null ? new Channel [0] : channels1.clone ();
    }

    public Channel [] getChannels () {
        return this.channels.clone ();
    }

    public int getNumberOfChannels () {
        return this.channels.length;
    }

    public int getSamplesLength () {
        return this.channels.length == 0 ? 0 : this.channels [0].getSamplesLength ();
    }

    public FormatInfo getFormatInfo () {
        return this.channels.length == 0 ? null : this.channels [0].getFormatInfo ();
    }
}
