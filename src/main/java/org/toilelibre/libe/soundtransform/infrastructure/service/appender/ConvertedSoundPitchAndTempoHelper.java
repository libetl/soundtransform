package org.toilelibre.libe.soundtransform.infrastructure.service.appender;

import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.SlowdownSoundTransformation;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundPitchAndTempoHelper;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PitchSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SpeedUpSoundTransformation;

public class ConvertedSoundPitchAndTempoHelper implements SoundPitchAndTempoHelper {

    @Override
    public Sound pitchAndSetLength (final Sound sound, final float percent, final float lengthInSeconds) {

        Sound result = sound;

        final PitchSoundTransformation pitcher = new PitchSoundTransformation (percent);
        if ((percent < 98) || (percent > 102)) {
            result = pitcher.transform (result);
        }
        final double factor = sound.getSamples ().length == 0 ? 0 : (1.0 * lengthInSeconds * sound.getSampleRate ()) / result.getSamples ().length;
        if (factor == 0) {
            return result;
        } else if ((factor < 0.98) || (factor > 1.02)) {
            if (factor < 0.98) {
                final SpeedUpSoundTransformation<?> speedup = $.create (SpeedUpSoundTransformation.class, 100, (float) (1 / factor));
                result = speedup.transform (result);

            } else if (factor > 1.02) {
                final SlowdownSoundTransformation slowdown = $.create (SlowdownSoundTransformation.class, 1024, (float) factor, 2048);
                result = slowdown.transform (result);
            }
        }
        return result;
    }
}
