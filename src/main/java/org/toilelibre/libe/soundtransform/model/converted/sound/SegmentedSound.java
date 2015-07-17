package org.toilelibre.libe.soundtransform.model.converted.sound;

import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;

public class SegmentedSound extends Sound {

    public SegmentedSound (final FormatInfo formatInfo, final List<Sound> sounds) {
        super (new Channel [] { new SegmentedChannel (formatInfo, sounds) });
    }

}
