package org.toilelibre.libe.soundtransform.model.converted.sound;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;

public class Sound implements Serializable {

    private static final long serialVersionUID = 8150171679999317728L;
    
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
