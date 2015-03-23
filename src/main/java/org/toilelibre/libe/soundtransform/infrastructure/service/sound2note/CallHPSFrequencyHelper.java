package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PeakFindWithHPSSoundTransformation;
import org.toilelibre.libe.soundtransform.model.library.note.FrequencyHelper;

final class CallHPSFrequencyHelper implements FrequencyHelper {

    @Override
    public float findFrequency (final Sound [] channels) {
        final PeakFindWithHPSSoundTransformation<? extends Serializable> peak = new PeakFindWithHPSSoundTransformation<Serializable> (true);
        float value = 0;
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
