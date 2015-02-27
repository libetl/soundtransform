package org.toilelibre.libe.soundtransform.infrastructure.service.appender;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.SlowdownSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundPitchAndTempoHelper;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PitchSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SpeedUpSoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class ConvertedSoundPitchAndTempoHelper implements SoundPitchAndTempoHelper {

    private static final int   THRESHOLD_DOWN_PITCH               = 98;
    private static final int   THRESHOLD_UP_PITCH                 = 102;

    private static final float THRESHOLD_SLOWDOWN                 = 1.02f;
    private static final float THRESHOLD_SPEEDUP                  = 0.98f;

    private static final int   HELPER_DEFAULT_SPEEDUP_STEP_VALUE  = 100;

    private static final int   HELPER_DEFAULT_SLOWDOWN_STEP_VALUE = 1024;
    private static final int   HELPER_DEFAULT_WINDOW_LENGTH_VALUE = 2 * ConvertedSoundPitchAndTempoHelper.HELPER_DEFAULT_SLOWDOWN_STEP_VALUE;

    @Override
    public Sound pitchAndSetLength (final Sound sound, final float percent, final float lengthInSeconds) throws SoundTransformException {

        Sound result = sound;

        final PitchSoundTransformation pitcher = new PitchSoundTransformation (percent);
        if ((percent < ConvertedSoundPitchAndTempoHelper.THRESHOLD_DOWN_PITCH) || (percent > ConvertedSoundPitchAndTempoHelper.THRESHOLD_UP_PITCH)) {
            result = pitcher.transform (result);
        }
        final double factor = sound.getSamplesLength () == 0 ? 0 : (1.0 * lengthInSeconds * sound.getSampleRate ()) / result.getSamplesLength ();
        if (factor == 0) {
            return result;
        } else if ((factor < ConvertedSoundPitchAndTempoHelper.THRESHOLD_SPEEDUP) || (factor > ConvertedSoundPitchAndTempoHelper.THRESHOLD_SLOWDOWN)) {
            if (factor < ConvertedSoundPitchAndTempoHelper.THRESHOLD_SPEEDUP) {
                final SpeedUpSoundTransformation<Serializable> speedup = new SpeedUpSoundTransformation<Serializable> (ConvertedSoundPitchAndTempoHelper.HELPER_DEFAULT_SPEEDUP_STEP_VALUE, (float) (1 / factor));
                result = speedup.transform (result);

            } else if (factor > ConvertedSoundPitchAndTempoHelper.THRESHOLD_SLOWDOWN) {
                final SlowdownSoundTransformation slowdown = new SlowdownSoundTransformation (ConvertedSoundPitchAndTempoHelper.HELPER_DEFAULT_SLOWDOWN_STEP_VALUE, (float) factor, ConvertedSoundPitchAndTempoHelper.HELPER_DEFAULT_WINDOW_LENGTH_VALUE);
                result = slowdown.transform (result);
            }
        }
        return result;
    }
}
