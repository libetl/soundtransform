package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.HarmonicProductSpectrumSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PeakFindSoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.note.FrequencyHelper;

final class CallHPSFrequencyHelper implements FrequencyHelper {

    @Override
    public float findFrequency (final Sound sound) throws SoundTransformException {
        final PeakFindSoundTransform<Serializable, ?> peak = new HarmonicProductSpectrumSoundTransform<Serializable> (true);
        float value = 0;
        float volume = 0;
        for (final Channel channel : sound.getChannels ()) {
            final float [] freqs = peak.transform (channel);
            if (volume < peak.getDetectedNoteVolume ()) {
                value = freqs [0];
                volume = peak.getDetectedNoteVolume ();
            }
        }

        return value;
    }
}
