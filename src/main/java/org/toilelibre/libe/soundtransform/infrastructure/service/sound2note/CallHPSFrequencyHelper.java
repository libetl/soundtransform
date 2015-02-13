package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PeakFindWithHPSSoundTransformation;
import org.toilelibre.libe.soundtransform.model.library.note.FrequencyHelper;

public class CallHPSFrequencyHelper implements FrequencyHelper {

    @Override
    public int findFrequency (final Sound [] channels) {
        final PeakFindWithHPSSoundTransformation<?> peak = $.create (PeakFindWithHPSSoundTransformation.class, true);
        int value = 0;
        float volume = 0;
        for (final Sound channel : channels) {
            peak.transform (channel);
            if (volume < peak.getDetectedNoteVolume ()) {
                value = peak.getLoudestFreqs () [0];
                volume = peak.getDetectedNoteVolume ();
            }
        }

        return value;
    }
}
